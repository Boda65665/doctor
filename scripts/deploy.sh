#!/usr/bin/env bash

mvn clean package

echo 'Copy files...'

scp -i ~/.ssh/id_ed25519.pub \
    P:/kafka2/target/demo-0.0.1-SNAPSHOT.jar \
    root@89.39.121.237:/root/

echo 'Restart server...'

ssh -i ~/.ssh/id_ed25519.pub root@89.39.121.237 << EOF

pgrep java | xargs kill -9
nohup java -jar demo-0.0.1-SNAPSHOT.jar > log.txt &

EOF

echo 'Bye'