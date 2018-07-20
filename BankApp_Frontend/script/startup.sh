#!/bin/bash
node ../server.js > log.txt 2>&1 &
echo $! > pid.file