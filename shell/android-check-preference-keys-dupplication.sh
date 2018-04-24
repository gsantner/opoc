#!/bin/bash
argc=$# ; RCol='\e[0m' ; Gre='\e[0;32m' ; Red='\e[0;31m'

for file in *xml; do

grep -q "pref_key__" "$file" || continue

echo -e "${Gre}$file::${RCol}"

cat "$file"  | grep "pref_key__" \
	| sed 's@<string name=@@' | sed 's@</string>@"@' | sed 's@translatable="false">@"@' \
	| sed 's/" "/"\n"/' | sed 's/^ *//g' | sed 's/ *$//g' \
	| sort |  uniq -c | grep -v "^      2" | sort -nr

done
