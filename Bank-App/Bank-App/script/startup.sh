#!/bin/bash
nohup java -jar ../target/Bank-App-0.1.0.jar > log.txt 2>&1 &
echo $! > pid.file