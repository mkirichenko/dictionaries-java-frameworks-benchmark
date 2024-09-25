#!/usr/bin/env bash

benchs=('k6-get.js')
services=(
	'dictionaries-helidonvt/target/dictionaries-helidonvt.jar',
	'dictionaries-kora/build/libs/dictionaries-kora-0.1.0-SNAPSHOT-all.jar',
	'dictionaries-ktor/build/libs/dictionaries-ktor-all.jar',
	'dictionaries-springtomcatvt/target/springtomcatvt-0.0.1-SNAPSHOT.jar',
	'dictionaries-springundertow/target/springundertow-0.0.1-SNAPSHOT.jar',
	'dictionaries-webflux/target/webflux-0.0.1-SNAPSHOT.jar'
)

for service in ${services[@]}; do
  serviceName=$(echo ${service} | cut -f1 -d"/")
  portName="8080"

  for bench in ${benchs[@]}; do
    benchName=$(echo ${bench} | cut -f1 -d".")

    echo --------------------------------------------
    echo Running ${bench} benchmark...

    java -Xmx1G -Xms1G -XX:ActiveProcessorCount=2 -jar ${service} &

    javaPid=$(echo $! | cut -f2 -d' ')
    echo Started app with pid ${javaPid} ...
    sleep 15
    source ./benchmark.sh ${bench} ${serviceName} localhost:${portName}
    sleep 10
    echo Closing app with pid ${javaPid} ...
    kill ${javaPid}
    sleep 2
  done
done
