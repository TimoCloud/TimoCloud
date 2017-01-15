#!/bin/bash
PROCESS=$(screen -ls |grep $1)
kill $(echo $PROCESS |cut -f1 -d'.')
exit