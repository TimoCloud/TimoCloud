#!/bin/bash
#1: Server directory. 2: ServerName. 3: Port. 4: RAM
cd $1/
screen -mS $2 java -server -Xmx$4G -XX:+UseG1GC -XX:+UnlockExperimentalVMOptions -XX:+AggressiveOpts -XX:+DoEscapeAnalysis -XX:+UseCompressedOops -XX:MaxGCPauseMillis=10 -XX:GCPauseIntervalMillis=100 -XX:+UseAdaptiveSizePolicy -XX:ParallelGCThreads=2 -XX:UseSSE=3 -Dcom.mojang.eula.agree=true -jar -p $3 spigot.jar