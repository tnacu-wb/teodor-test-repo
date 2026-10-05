# Build Guide — Whitbread Digital Monorepo

Step-by-step instructions to build the full project from scratch on macOS.

## Prerequisites

| Tool | Version | Notes |
|------|---------|-------|
| JDK | **25** | Required by Spring Boot 4.0.6 (`--release 25`) |
| Maven | Provided | Via Maven Wrapper (`./mvnw`), no local install needed |
| macOS | Tested on Apple Silicon (arm64) | Homebrew paths assume `/opt/homebrew/` |

## Step 1 — Install JDK 25

```bash
brew install openjdk
```

After installation, the JDK is located at:

```
/opt/homebrew/Cellar/openjdk/25.0.2/libexec/openjdk.jdk/Contents/Home
```

Verify installation:

```bash
/opt/homebrew/Cellar/openjdk/25.0.2/libexec/openjdk.jdk/Contents/Home/bin/java -version
```

Expected output:

```
openjdk version "25.0.2" 2026-01-20
OpenJDK Runtime Environment Homebrew (build 25.0.2)
OpenJDK 64-Bit Server VM Homebrew (build 25.0.2, mixed mode, sharing)
```

## Step 2 — Set JAVA_HOME

The project requires JDK 25. If your default `java` points to a different version, export `JAVA_HOME` before building.

### Temporary (current terminal session only)

```bash
export JAVA_HOME=/opt/homebrew/Cellar/openjdk/25.0.2/libexec/openjdk.jdk/Contents/Home
```

### Permanent (recommended)

Add the following to your `~/.zshrc`:

```bash
export JAVA_HOME=/opt/homebrew/Cellar/openjdk/25.0.2/libexec/openjdk.jdk/Contents/Home
export PATH="$JAVA_HOME/bin:$PATH"
```

Then reload:

```bash
source ~/.zshrc
```

Confirm the correct version is active:

```bash
java -version
# Should show: openjdk version "25.0.2"
```

## Step 3 — Clone the Repository

```bash
git clone git@github.com:whitbread-eos/digital-monorepo.git
cd digital-monorepo
```

## Step 4 — Run the Full Build

Navigate to the `backend/` directory and run the Maven Wrapper:

```bash
cd backend
./mvnw clean install
```

This will:
1. Compile all modules (libraries and services) across all squads
2. Run unit tests
3. Install artifacts to the local Maven repository (`~/.m2/repository`)

### Skip Tests (faster build)

```bash
./mvnw clean install -DskipTests
```

## Step 5 — Build a Single Service (optional)

```bash
cd backend
./mvnw clean install -pl <squad>/services/<service-name> -am
```

Example:

```bash
./mvnw clean install -pl book-pay-squad/services/basket-async-order-processor -am
```

The `-am` (also-make) flag ensures any dependencies (e.g. shared libraries) are built first.

## Troubleshooting

### Wrong JDK version

```
error: release version 25 not supported
```

**Fix:** Ensure `JAVA_HOME` points to JDK 25 and that `java -version` confirms `25.x`.

### Maven Wrapper permission denied

```
zsh: permission denied: ./mvnw
```

**Fix:**

```bash
chmod +x mvnw
```

### Out of memory during build

**Fix:** Increase Maven's heap size:

```bash
export MAVEN_OPTS="-Xmx2g"
./mvnw clean install
```

## Summary of Commands

```bash
# 1. Set JDK 25
export JAVA_HOME=/opt/homebrew/Cellar/openjdk/25.0.2/libexec/openjdk.jdk/Contents/Home

# 2. Navigate to backend
cd digital-monorepo/backend

# 3. Full build
./mvnw clean install

# 4. Full build (skip tests)
./mvnw clean install -DskipTests
```
