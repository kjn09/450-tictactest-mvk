from builtins import int, next
import sys
import xml.etree.ElementTree as ET

root = ET.parse(sys.argv[1]).getroot()
counter = next(c for c in root.findall("counter") if c.get("type") == "BRANCH")
missed = int(counter.get("missed"))
covered = int(counter.get("covered"))
print(f"{100 * covered / (missed + covered):.1f}")