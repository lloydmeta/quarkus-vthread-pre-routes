#!/bin/bash

export counter=1                                                                                                               ─╯

while true; do
  echo "Request $counter"
  curl -u joe-$counter:schmoe localhost:8080/v1/hello/say-my-name &
  ((counter++))
  sleep 0.01
done