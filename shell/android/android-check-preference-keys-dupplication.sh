#!/bin/bash
#########################################################
#
#   > Android check preference key duplication
#
#   Maintained by Gregor Santner, 2018-
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

for file in *xml; do

grep -q "pref_key__" "$file" || continue

echo -e "${Gre}$file::${RCol}"

cat "$file"  | grep "pref_key__" \
	| sed 's@<string name=@@' | sed 's@</string>@"@' | sed 's@translatable="false">@"@' \
	| sed 's/" "/"\n"/' | sed 's/^ *//g' | sed 's/ *$//g' \
	| sort |  uniq -c | grep -v "^      2" | sort -nr

done
