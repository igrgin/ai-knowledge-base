#!/usr/bin/env python3
"""Offline checks for repository records, local links and task PR metadata."""

import json
import os
from pathlib import Path
import re
import subprocess
from urllib.parse import unquote, urlsplit


ROOT = Path(__file__).resolve().parents[1]
CHANGE = "docs/changes/20261023-ai-knowledge-base"
errors = []


def require(condition, message):
    if not condition:
        errors.append(message)


def prose(text):
    """Ignore fenced examples when finding headings and Markdown links."""
    return re.sub(r"^(`{3,}|~{3,})[^\n]*\n.*?^\1[^\n]*$", "", text,
                  flags=re.MULTILINE | re.DOTALL)


def headings(path):
    return re.findall(r"^#{1,6}\s+(.+?)\s*#*\s*$", prose(path.read_text()), re.MULTILINE)


def anchors(path):
    result, counts = set(), {}
    for heading in headings(path):
        slug = re.sub(r"[^\w\- ]", "", heading.lower()).replace(" ", "-")
        count = counts.get(slug, 0)
        counts[slug] = count + 1
        result.add(f"{slug}-{count}" if count else slug)
    return result


def record_references(value, source):
    if isinstance(value, dict):
        if isinstance(value.get("path"), str):
            target = (ROOT / value["path"]).resolve()
            require(target.is_relative_to(ROOT) and target.is_file(),
                    f"{source}: missing repository path {value['path']}")
            if target.is_file() and "section" in value:
                require(value["section"] in headings(target),
                        f"{source}: missing section {value['section']!r} in {value['path']}")
        for child in value.values():
            record_references(child, source)
    elif isinstance(value, list):
        for child in value:
            record_references(child, source)


paths = subprocess.check_output(
    ["git", "ls-files", "--cached", "--others", "--exclude-standard", "-z"],
    cwd=ROOT).decode().split("\0")
files = sorted({ROOT / name for name in paths if name and (ROOT / name).is_file()})
records = {}
for path in files:
    relative = path.relative_to(ROOT).as_posix()
    if path.suffix == ".json":
        try:
            records[relative] = json.loads(path.read_text())
        except (ValueError, UnicodeError) as error:
            errors.append(f"{relative}: invalid JSON: {error}")
    if path.suffix == ".md":
        for link in re.findall(r"\]\(([^\s)]+)\)", prose(path.read_text())):
            url = urlsplit(link.strip("<>"))
            if url.scheme or url.netloc:
                continue
            target = (path.parent / unquote(url.path)).resolve() if url.path else path
            require(target.is_relative_to(ROOT) and target.exists(),
                    f"{relative}: broken local link {link}")
            if target.is_file() and target.suffix == ".md" and url.fragment:
                require(unquote(url.fragment) in anchors(target),
                        f"{relative}: broken local anchor {link}")

for name in ("lighthouse.json", f"{CHANGE}/change.json", f"{CHANGE}/github-issues.json"):
    require(name in records, f"Missing valid required JSON record: {name}")
for name in ("lighthouse.json", f"{CHANGE}/change.json"):
    if name in records:
        record_references(records[name], name)

config = records.get("lighthouse.json", {})
require(config.get("schemaVersion") == 1, "Unsupported Lighthouse schemaVersion")
require(isinstance(config.get("capabilities", {}).get("concurrency", {}).get("maxParallelTasks"), int),
        "Lighthouse concurrency must have an explicit integer limit")

plan = (ROOT / CHANGE / "plan.md").read_text()
task_rows = {}
for row in plan.splitlines():
    columns = [column.strip() for column in row.strip("|").split("|")]
    if len(columns) == 7 and re.fullmatch(r"T\d+", columns[0]):
        require(columns[0] not in task_rows, f"Duplicate plan task {columns[0]}")
        task_rows[columns[0]] = columns
mapping = records.get(f"{CHANGE}/github-issues.json", {})
tasks = mapping.get("tasks", [])
require(isinstance(tasks, list), "Issue mapping tasks must be a list")
if not isinstance(tasks, list):
    tasks = []
require({item.get("taskId") for item in tasks} == set(task_rows), "Plan/issue task IDs differ")
require(len(tasks) == len(task_rows), "Each plan task must have exactly one issue")
require(mapping.get("repository") == "igrgin/ai-knowledge-base", "Unexpected issue repository")
numbers = []
for item in tasks:
    task, number, title = item.get("taskId"), item.get("issueNumber"), item.get("title", "")
    require(isinstance(number, int) and number > 0, f"{task}: invalid actual issue number")
    numbers.append(number)
    require(item.get("issueUrl") == f"https://github.com/igrgin/ai-knowledge-base/issues/{number}",
            f"{task}: issue URL does not match mapping")
    slug = re.sub(r"[^a-z0-9]+", "-", title.lower()).strip("-")
    require(item.get("branch") == f"{number}-{slug}", f"{task}: branch/title association differs")
    require(bool(title), f"{task}: missing issue title")
require(len(set(numbers)) == len(numbers), "An issue is mapped to more than one task")

workflow = (ROOT / ".github/workflows/repository-checks.yml").read_text()
for required in ("  pull_request:", "    branches: [main]", "  contents: read",
                 "    name: Repository checks", "    timeout-minutes: 5",
                 "        run: python3 scripts/check_repository.py"):
    require(required in workflow, f"Bootstrap workflow missing {required.strip()!r}")
require("pull_request_target" not in workflow, "Bootstrap checks must use pull_request")
for action in re.findall(r"uses:\s*(\S+)", workflow):
    require(bool(re.fullmatch(r"actions/[a-z-]+@[0-9a-f]{40}", action)),
            f"Bootstrap action must be pinned to a full commit: {action}")

event_path = os.environ.get("GITHUB_EVENT_PATH")
if os.environ.get("GITHUB_EVENT_NAME") == "pull_request" and event_path:
    event = json.loads(Path(event_path).read_text())
    pr = event["pull_request"]
    body = pr.get("body") or ""
    task_match = re.search(r"Lighthouse task:\s*(T\d+)\b", body, re.IGNORECASE)
    if task_match:
        task = task_match[1].upper()
        item = next((entry for entry in tasks if entry["taskId"] == task), None)
        require(item is not None, f"PR references unmapped task {task}")
        if item:
            require(pr["head"]["ref"] == item["branch"], f"{task}: PR uses the wrong issue branch")
            require(bool(re.search(rf"\b(?:Closes|Refs)\s+#{item['issueNumber']}\b", body, re.IGNORECASE)),
                    f"{task}: PR must link its mapped issue with Closes or Refs")
    else:
        require(not re.match(r"\d+-", pr["head"]["ref"]),
                "Issue branch PR must identify its Lighthouse task")

if errors:
    raise SystemExit("Repository checks failed:\n- " + "\n- ".join(errors))
print(f"Repository checks passed: {len(files)} files, {len(task_rows)} task mappings, local references and workflow metadata.")
