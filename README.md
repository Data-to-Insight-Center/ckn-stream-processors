# CKN Stream Processor for TAPIS Camera traps application

## How to run

All stream processors take in configurations via environment variables. Each stream processor has specific environment variables you can set. 
You must set the following environment variables. 
```shell
export $CKN_BROKERS="<CKN_BROKER_LOCATION>"
```
Please refer to the corresponding stream processor files for configuratble parameters. 

### Via Docker
Before running, have docker installed in your local system. 

There are multiple docker files each corresponding to a single stream processor. You can change the environment variables in each of these files. 
Make sure to point your CKN_BROKERS to the hosted CKN brokers in your system. 

Make sure you have the jar files located in the ```./target``` directory. If not run the command:
```shell
mvn clean package
```

Build and run the corresponding docker file. An example is shown below. 
```shell
docker build -f Dockerfile.RawAlert -t ckn-processor-oracle-alert .
docker run --name ckn-alerter ckn-processor-oracle-alert 
```

### From source
Have the pre-requisites installed: Maven3, JDK

Execute from the current root directory. This will package the stream processors into a jar file.
```shell
mvn clean package
```

Jar file is available at:
```shell
./target
```

Run the jar file as following:
```shell
java -cp ckn-stream-processors-0.1-SNAPSHOT.jar edu.d2i.ckn.OracleAggregationProcessor 
```

Based on the stream processor, you need to change the executable class when running. The example above runs the OracleAggregationProcessor. 
To run the Oracle accuracy alert processor:

```shell
java -cp ckn-stream-processors-0.1-SNAPSHOT.jar edu.d2i.ckn.OracleAccAlertProcessor
```

