#!/bin/bash

export counter=1                                                                                                               ─╯

while true; do
  # Launch curl in the background and save output to a temp file
  echo "Request $counter"
  curl -u joe-$counter:schmoe localhost:8080/v1/hello/say-my-name &

  # Increment counter
  ((counter++))

  # Very short pause before next request
  sleep 0.01
done