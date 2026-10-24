#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"
NODE_VERSION="$(cat frontend/.node-version)"
case "$(uname -s)-$(uname -m)" in
  Darwin-arm64) NODE_PLATFORM=darwin-arm64;;
  Darwin-x86_64) NODE_PLATFORM=darwin-x64;;
  Linux-aarch64) NODE_PLATFORM=linux-arm64;;
  Linux-x86_64) NODE_PLATFORM=linux-x64;;
  *) echo 'Unsupported Node platform; provide Node 24.22.1 on PATH' >&2; NODE_PLATFORM=unsupported;;
esac
if [[ -n "${JAVA_HOME:-}" ]]; then export PATH="$JAVA_HOME/bin:$PATH"; fi
export PATH="$ROOT/.local/tools/node-v$NODE_VERSION-$NODE_PLATFORM/bin:$PATH"
# Export configuration in the invoking shell. Never evaluate a .env file as code.

require_java26() {
  version="$(java -XshowSettings:properties -version 2>&1 | sed -n 's/^[[:space:]]*java.specification.version = //p')"
  [[ "$version" == 26 ]] || { echo 'Java 26 is required' >&2; exit 1; }
}
