#!/bin/bash

function stamp {
	echo 'Memory used: ' `top -l 1 | grep PhysMem | cut -d' ' -f2`
	echo 'CPU: ' `ps aux  | awk 'BEGIN { sum = 0 }  { sum += $3 }; END { print sum }'`
}

echo "Starting Hadoop measurements..."
for i in $(seq 2000 2016); do
	echo "Year $i"
	echo
	curl -o /dev/null -s -w "Time Elapsed: %{time_total}s\n" http://localhost:17071/championship_chart/$i
	stamp
	echo 
done

