#!/bin/bash
LD_LIBRARY_PATH=/usr/local/lib java -cp /usr/local/lib/java/*:target/esketch.jar org.esialb.edison.esketch.Main "$@"
