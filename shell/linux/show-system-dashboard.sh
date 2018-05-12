#!/bin/bash
#########################################################
#
#   > Show a dashboard for running linux processes
#
#   Maintained by Gregor Santner, 2016-
#   https://gsantner.net/
#
#   License: Apache 2.0
#  https://github.com/gsantner/opoc/#licensing
#  https://www.apache.org/licenses/LICENSE-2.0
#
#########################################################


#########################################################
#### Colors
##
RCol='\e[0m'    # Text Reset
# Regular           Bold                Underline           High Intensity      BoldHigh Intens     Background          High Intensity Backgrounds
Bla='\e[0;30m';     BBla='\e[1;30m';    UBla='\e[4;30m';    IBla='\e[0;90m';    BIBla='\e[1;90m';   On_Bla='\e[40m';    On_IBla='\e[0;100m';
Red='\e[0;31m';     BRed='\e[1;31m';    URed='\e[4;31m';    IRed='\e[0;91m';    BIRed='\e[1;91m';   On_Red='\e[41m';    On_IRed='\e[0;101m';
Gre='\e[0;32m';     BGre='\e[1;32m';    UGre='\e[4;32m';    IGre='\e[0;92m';    BIGre='\e[1;92m';   On_Gre='\e[42m';    On_IGre='\e[0;102m';
Yel='\e[0;33m';     BYel='\e[1;33m';    UYel='\e[4;33m';    IYel='\e[0;93m';    BIYel='\e[1;93m';   On_Yel='\e[43m';    On_IYel='\e[0;103m';
Blu='\e[0;34m';     BBlu='\e[1;34m';    UBlu='\e[4;34m';    IBlu='\e[0;94m';    BIBlu='\e[1;94m';   On_Blu='\e[44m';    On_IBlu='\e[0;104m';
Pur='\e[0;35m';     BPur='\e[1;35m';    UPur='\e[4;35m';    IPur='\e[0;95m';    BIPur='\e[1;95m';   On_Pur='\e[45m';    On_IPur='\e[0;105m';
Cya='\e[0;36m';     BCya='\e[1;36m';    UCya='\e[4;36m';    ICya='\e[0;96m';    BICya='\e[1;96m';   On_Cya='\e[46m';    On_ICya='\e[0;106m';
Whi='\e[0;37m';     BWhi='\e[1;37m';    UWhi='\e[4;37m';    IWhi='\e[0;97m';    BIWhi='\e[1;97m';   On_Whi='\e[47m';    On_IWhi='\e[0;107m';
#########################################################


ELEINROW_MAX=4
ELEINROW=0
NAME=""
STATUS=0

outputStatus(){
	symbol="●"
	color="${Red}"
	count=$(echo $NAME | wc -c)


	# Apply color
	[ $STATUS -eq 1 ] && color="${Gre}"
	[ $STATUS -eq 2 ] && color="${Yel}"
	[ $STATUS -eq 99 ] && color="${Cya}"


	# Output text
	echo -e "$color$symbol${RCol}  $NAME\t\c"

	# Additional tab if short text
	if [ $count -lt 6 ] ; then
		echo -e "\t\c"
	fi

	if [ $count -gt 12 ] ; then
		echo -e "\t\c"
		ELEINROW=$((ELEINROW+1))
	fi

	# Start new line if line if reaches max
	ELEINROW=$((ELEINROW+1))
	if [ $ELEINROW -eq $ELEINROW_MAX ] ; then
		ELEINROW=0
		echo ""
	fi

}

setStatus() {
	STATUS=$1
	NAME="$2"
}

getSystemdStatus(){
	# $1:  "service/name"
	local name=$(echo $1 | xargs)

	ret=$(systemctl status "$(echo $name | cut -d '/' -f 1)" | grep "   Active:" | cut -b 12- | cut -d ')' -f 1)
	ret="$ret)"

	local status=0
	if [ "$ret" == "active (running)" ]; then
		status=1
	fi
	if [ "$ret" == "active (exited)" ]; then
		status=2
	fi

	NAME="$(echo $name | cut -d '/' -f 2)"
	STATUS=$status
}
getHttpStatus(){
	# $1: URL
	ret=$(curl -s -w "%{http_code}" "$1" -o /dev/null)

	local status=0
	[ "$ret" == "200" ] && status=1
	STATUS=$status
}


showIpBox(){
	setStatus 99 "Pub IP $(curl -s ipinfo.io/ip)"
	outputStatus
	echo "";
	ELEINROW=0
}

################################
###########################
##### Main
##
[ "$1" == "withIp" ] && showIpBox

ELEINROW=0

SYSTEMDCHECKS=(
	"apache2/Apache" "tor/Tor" "mysql/MySql" "gitea/Gitea" \
	"pyload/PyLoad" "fail2ban" "smbd/Samba" \
	"nfs-kernel-server/NFS" )
for i in "${SYSTEMDCHECKS[@]}"; do
	getSystemdStatus "$i"
	outputStatus
done

# Jenkins
getHttpStatus "http://localhost:4321/jenkins/login"
NAME="Jenkins" && outputStatus

# UFW
STATUS=$(sudo ufw status 2>&1 | grep "^Status:" | head -n 1 | grep -q " active" && echo 1 || echo 0)
NAME="Firewall" && outputStatus

# DNSCrypt (yellow = Error/suspicious)
getSystemdStatus "dnscrypt-autoinstall/DNSCrypt"
[ $STATUS -ne 0 ] && STATUS=$(sudo systemctl status dnscrypt-autoinstall | grep -q "ERROR" && echo 2 || echo 1)
NAME="DNSCrypt" && outputStatus


# Finish current line so the cmd input window start again from pos 0
[ $ELEINROW -ne 0 ] && echo ""
