#!/usr/bin/python
#########################################################
#
#   androidIntentDataSegmentFileExtensionGenerator
#
#   Maintained by Gregor Santner, 2016-
#   https://gsantner.net/
#
#   License: Apache 2.0
#  https://github.com/gsantner/opoc/#licensing
#  https://www.apache.org/licenses/LICENSE-2.0
#
#########################################################
#
# Create Intent filter data segments for android manifest
# See https://stackoverflow.com/questions/3400072/pathpattern-to-match-file-extension-does-not-work-if-a-period-exists-elsewhere-i/4621284#4621284
#
import sys

DEFAULT_EXTENSIONS = ["markdown", "md", "mdown", "mdwn", "mkd", "mkdn", "Rmd", "text", "txt"]
DEFAULT_LEVEL = 10

args = sys.argv
args.pop(0)
level = DEFAULT_LEVEL if len(args) == 0 else int(args.pop(0))
extensions = DEFAULT_EXTENSIONS if len(args) == 0 else args

output=""
for extension in extensions:
    output = output + '<!-- Handling of file extension ".{}" -->\n'.format(extension)
    patpre = ""
    for i in range (level):
        patpre = patpre + ".*\\."
        dataelem = '<data android:mimeType="*/*" android:pathPattern="{}{}"/>'.format(patpre, extension)
        output = output + dataelem + "\n"
    output = output + "\n"
print(output.rstrip())
