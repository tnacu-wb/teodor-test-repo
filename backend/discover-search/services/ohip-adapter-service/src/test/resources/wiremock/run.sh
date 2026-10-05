# Note: You need to install the WireMock Plugin in IntelliJ IDEA to run this script. This script is tested for
# IntelliJ IDEA-EAP 2024.1
WIRE_MOCK=$(find "${HOME}/Library/Application Support/JetBrains" -name "wiremock-standalone-rt.jar" -type f)
java -jar "${WIRE_MOCK}" --port 8080 --root-dir .