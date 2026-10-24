#!/usr/bin/env python3
"""Real database/package/restart checks. Uses an isolated, disposable Compose project."""
import json
import os
from pathlib import Path
import socket
import subprocess
import time
import urllib.error
import urllib.request
import uuid

ROOT = Path(__file__).resolve().parent.parent
os.chdir(ROOT)
project = "akb-t1-smoke-" + uuid.uuid4().hex[:8]
env = dict(os.environ, COMPOSE_PROJECT_NAME=project, DB_HOST="127.0.0.1", DB_PORT="54330",
           DB_NAME="knowledge_base", DB_USER="knowledge_base", DB_PASSWORD="local-development-only", APP_PORT="18080")
compose = ["docker", "compose", "-f", str(ROOT / "compose.yaml"), "-p", project]

def run(args, **kwargs):
    return subprocess.run(args, env=env, check=True, text=True, **kwargs)

def sql(statement):
    return run(compose + ["exec", "-T", "db", "sh", "-c", 'psql -v ON_ERROR_STOP=1 -U "$POSTGRES_USER" -d "$POSTGRES_DB" -Atc "$1"', "sh", statement], capture_output=True).stdout.strip()

def wait_status(expected):
    deadline = time.monotonic() + 45
    while time.monotonic() < deadline:
        try:
            with urllib.request.urlopen("http://127.0.0.1:18080/actuator/health/readiness", timeout=5) as response:
                status, body = response.status, json.load(response)
        except urllib.error.HTTPError as error:
            status, body = error.code, json.load(error)
        except (OSError, TimeoutError):
            time.sleep(.5)
            continue
        if status == expected:
            assert body["status"] == ("UP" if expected == 200 else "DOWN")
            return
        time.sleep(.5)
    raise AssertionError(f"Readiness never became {expected}")

# Refuse an already-used port rather than probing somebody else's service.
for port in (54330, 18080):
    with socket.socket() as probe:
        probe.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        probe.bind(("127.0.0.1", port))
if run(compose + ["ps", "-aq"], capture_output=True).stdout.strip():
    raise SystemExit("akb-t1-smoke already exists; inspect it before rerunning")
probe = run(["java", "-Xmx256m", "-jar", ".local/package/pdf-extractor.jar", "--version"], capture_output=True)
assert json.loads(probe.stdout) == {"contractVersion": 1, "javaFeature": 26, "extractionAvailable": False}
app = None
try:
    run(compose + ["up", "-d", "--wait", "db"])
    assert sql("SELECT extversion FROM pg_extension WHERE extname='vector'") == "0.8.7"
    assert sql("SELECT '[1,2,3]'::vector <-> '[1,2,4]'::vector") == "1"
    sql("CREATE TABLE t1_probe(value text); INSERT INTO t1_probe VALUES ('restart-preserved')")
    run(compose + ["restart", "db"])
    run(compose + ["up", "-d", "--wait", "db"])
    assert sql("SELECT value FROM t1_probe") == "restart-preserved"
    sql("DROP TABLE t1_probe")
    with open(".local/smoke-backend.log", "w") as log:
        app = subprocess.Popen(["java", "-jar", ".local/package/backend.jar"], env=env, stdout=log, stderr=subprocess.STDOUT)
        wait_status(200)
        browser_env = dict(env, PLAYWRIGHT_BASE_URL="http://127.0.0.1:18080")
        subprocess.run(["npm", "run", "test:e2e", "--prefix", "frontend"], env=browser_env, check=True)
        run(compose + ["stop", "db"])
        wait_status(503)
        # The browser verifies the failure message against this real database outage.
        subprocess.run(["npm", "run", "test:e2e", "--prefix", "frontend"], env=dict(browser_env, EXPECT_DATABASE_DOWN="1"), check=True)
        run(compose + ["up", "-d", "--wait", "db"])
        wait_status(200)
        app.terminate()
        app.wait(timeout=15)
        app = subprocess.Popen(["java", "-jar", ".local/package/backend.jar"], env=env, stdout=log, stderr=subprocess.STDOUT)
        wait_status(200)
    print("PASS: pgvector, persistent restart, packaged browser, database outage/recovery, application restart")
finally:
    if app is not None:
        app.terminate()
        try:
            app.wait(timeout=15)
        except subprocess.TimeoutExpired:
            app.kill()
            app.wait()
    # Only this command's isolated probe volume is removed. User development data is untouched.
    run(compose + ["down", "--volumes"])
