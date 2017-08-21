#!/bin/sh

for i in $(seq 1 255); do sudo ifconfig lo0 alias 172.17.0.$i; done
