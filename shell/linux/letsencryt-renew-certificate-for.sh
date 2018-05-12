#!/bin/bash
#########################################################
#
#   Renew letsencrypt certificate manually
#   Crontab: @monthly bash /path/to/script.sh >/dev/null 2>&1
#
#   Maintained by Gregor Santner, 2016-
#   https://gsantner.net/
#
#   License: Apache 2.0
#  https://github.com/gsantner/opoc/#licensing
#  https://www.apache.org/licenses/LICENSE-2.0
#
#########################################################
# vim: sw=4 ts=4 noexpandtab:

SCRIPTDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
SCRIPTFILE=$(readlink -f $0)
SCRIPTDIR_REAL=$(dirname $SCRIPTFILE)
SCRIPTDIR_PARENT="$(dirname "$SCRIPTDIR")"
WORKINGDIR="$(pwd)"
argc=$# ;RCol='\e[0m'; Gra='\e[0;90m'; Gre='\e[0;32m'; Red='\e[0;31m'; Yel='\e[0;33m'; Pur='\e[0;35m';

#########################################################

CERTBOT="/usr/local/bin/certbot-auto"
DOMAIN="mydomain.xyz"

sudo "$CERTBOT" certonly --webroot -w "/var/www/$DOMAIN" -d "$DOMAIN" --quiet
sudo chown root:sslcert -R "/etc/letsencrypt/live" "/etc/letsencrypt/archive"
sudo chmod 750 "/etc/letsencrypt/live" "/etc/letsencrypt/archive"

if type nginx >/dev/null 2>&1 && type service >/dev/null 2>&1 ; then
        sudo service nginx reload
        sudo service nginx status
fi
if type apache2 >/dev/null 2>&1 && type service >/dev/null 2>&1 ; then
        sudo service apache2 reload
        sudo service apache2 status
fi
if type nginx >/dev/null 2>&1 && type systemctl >/dev/null 2>&1 ; then
        sudo systemctl reload nginx
        sudo systemctl status nginx
fi
if type apache2 >/dev/null 2>&1 && type systemctl >/dev/null 2>&1 ; then
        sudo systemctl reload apache2
        sudo systemctl status apache2
fi
