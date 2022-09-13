mvn clean install
tar -xzvf server/target/tpe1-g9-server-1.0-SNAPSHOT-bin.tar.gz
tar -xzvf client/target/tpe1-g9-client-1.0-SNAPSHOT-bin.tar.gz
cd tpe1-g9-server-1.0-SNAPSHOT
chmod u+x ./run-registry.sh
chmod u+x ./run-server.sh
cd ..
cd tpe1-g9-client-1.0-SNAPSHOT
chmod u+x ./run-admin.sh