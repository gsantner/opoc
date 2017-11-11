#!/usr/bin/python
#
# ------------------------------------------------------------------------------
# Gregor Santner <gsantner.net> wrote this. You can do whatever you want
# with it. If we meet some day, and you think it is worth it, you can buy me a
# coke in return. Provided as is without any kind of warranty. Do not blame or
# sue me if something goes wrong. No attribution required.    - Gregor Santner
#
# License: Creative Commons Zero (CC0 1.0)
#  http://creativecommons.org/publicdomain/zero/1.0/
# ----------------------------------------------------------------------------
#
# Create Intent filter data segments for android manifest
# See https://stackoverflow.com/questions/3400072/pathpattern-to-match-file-extension-does-not-work-if-a-period-exists-elsewhere-i/4621284#4621284

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
