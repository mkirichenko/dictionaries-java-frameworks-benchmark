#!/usr/bin/env bash

if [[ $# -eq 0 ]] ; then
    echo 'Pass K6 loading script and serviceName and host:port as arguments'
    exit 0
fi

bench=$1
serviceName=$2
hostAndPort=$3

benchName=$(echo ${bench} | cut -f1 -d".")
echo Running benchmark ${bench} for ${serviceName}...

k6 run -e TARGET_HOST=${hostAndPort} --out "web-dashboard=period=1s&export=${serviceName}-${benchName}-report-page.html&record=${serviceName}-${benchName}-report-data-raw.ndjson" ${bench}

mkdir -p reports
mkdir -p reports/raw
mkdir -p reports/services

mv ${serviceName}-${benchName}-report-page.html reports/${serviceName}-${benchName}-report-page.html

cat ${serviceName}-${benchName}-report-data-raw.ndjson | \
  grep ^'{"event":"snapshot"' | \
  sed '$ ! s/$/,/' | \
  sed '1s/^/[\n/' | \
  sed '$s/$/\n]/' | \
  sed 's/[\(\)]//g' > ${serviceName}-${benchName}-report-data-snapshot.json

cat ${serviceName}-${benchName}-report-data-snapshot.json | \
  jq '.[].data | "\(.[11][0]);\(.[5][0]);\(.[5][1]);\(.[5][2]);\(.[5][3]);\(.[5][4]);\(.[5][5]);\(.[5][6])"' | \
  awk '{gsub(/null/,"0")}{gsub(/\./,",")}{print NR";"substr($0, 2, length($0) - 2)}' \
  > ${serviceName}-${benchName}-report-data-rps-avg-max-med-min-p90-95-99.csv


mv ${serviceName}-${benchName}-report-data-raw.ndjson reports/raw/${serviceName}-${benchName}-report-data-raw.ndjson
mv ${serviceName}-${benchName}-report-data-snapshot.json reports/raw/${serviceName}-${benchName}-report-data-snapshot.json
mv ${serviceName}-${benchName}-report-data-rps-avg-max-med-min-p90-95-99.csv reports/services/${serviceName}-${benchName}-report-data-rps-avg-max-med-min-p90-95-99.csv

echo Finished benchmark ${bench} for ${serviceName}
