#!/usr/bin/env python3
#########################################################
#
#   >  Add a keybinding to Gnome Shell
#   Tool to add custom gnome keybindings
#   Updated to work with Gnome 3.20
#   by Gregor Santner (https://gsantner.net/)
#
#   Maintained by Gregor Santner, 2016-
#   https://gsantner.net/
#
#   Original Source: https://askubuntu.com/a/597414
#
#   License: MIT
#
#########################################################

# Arguments             Name         Command      Binding
# Example arguments: "Terminator" "terminator" "<Ctrl><Alt>T"


import subprocess
import sys

# Key-Matching
matchKey=["/name", "/command", "/binding"]
selectedMatchKey=0
sysargs = sys.argv

# defining keys & strings to be used
KEY_GNOME3 = "org.gnome.settings-daemon.plugins.media-keys custom-keybindings"
KEY_MATE   = "org.mate.desktop keybindings"

key = KEY_GNOME3
subkey1 = key.replace(" ", ".")[:-1]+":"
item_s = "/"+key.replace(" ", "/").replace(".", "/")+"/"
firstname = "custom"

if sysargs[1] == "MATE_DESKTOP":
    key = KEY_MATE
    sysargs.pop(1)

# fetch the current list of custom shortcuts
get = lambda cmd: subprocess.check_output(["/bin/bash", "-c", cmd]).decode("utf-8")
try:
    current = eval(get("gsettings get "+key))
except:
    current = []

# make sure the additional keybinding mention is no duplicate
alreadyIn=False
n = 1
while True:
    new = item_s+firstname+str(n)+"/"
    if new in current:
        itemValue=str(get("dconf read " + item_s+firstname+str(n) + matchKey[selectedMatchKey]  ))
        itemValue=itemValue.strip().replace("'","")
        if itemValue == sys.argv[selectedMatchKey+1]:
            sys.stderr.write("Keybinding exists (" + matchKey[selectedMatchKey] + "): '" + itemValue + "\n")
            exit()
        n = n+1;
    else:
        break

# add the new keybinding to the list
current.append(new)

# create the shortcut, set the name, command and shortcut key
cmd0 = 'gsettings set '+key+' "'+str(current)+'"'
cmd1 = 'gsettings set '+subkey1+new+" name '"+sys.argv[1]+"'"
cmd2 = 'gsettings set '+subkey1+new+" command '"+sys.argv[2]+"'"
cmd3 = 'gsettings set '+subkey1+new+" binding '"+sys.argv[3]+"'"

for cmd in [cmd0, cmd1, cmd2, cmd3]:
    subprocess.call(["/bin/bash", "-c", cmd])
