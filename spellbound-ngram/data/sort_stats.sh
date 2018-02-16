#!/bin/bash
sort -t'	' -k4,4nr -k2,2nr -k3,3nr ngram-stats.tsv -o ngram-stats.tsv
