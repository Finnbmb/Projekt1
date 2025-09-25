#!/bin/bash
# test-azure-connection.sh

echo "Testing Azure MySQL Connection..."
mvn exec:java -Dexec.mainClass="de.swtp1.terminkalender.test.AzureMySQLConnectionTest" -Dexec.classpathScope="test"