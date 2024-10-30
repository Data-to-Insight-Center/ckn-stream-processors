# CKN Stream Processor for TAPIS Camera traps Application
Leveraging Apache Kafka as the message broker, the CKN Stream Processor aggregates incoming events, filtering and transforming data before storage.

## Getting Started

### 1. Start Broker
Set the environment variables by running:
```shell
export $CKN_BROKERS="<CKN_BROKER_ADDRESS>"
```

Refer to the corresponding stream processor files for configurable parameters.

### 2. Compile the jar file
Make sure you have the jar files located in the ```./target``` directory. If not run the command:
```shell
mvn clean package
```

### 3. Start Docker
Build and run the corresponding docker file. An example is shown below.
```shell
docker build -f Dockerfile.RawAlert -t ckn-processor-oracle-alert .
docker run --name ckn-alerter ckn-processor-oracle-alert 
```

## Building from source
### Pre-requisites:
- Maven3
- JDK

### 1. Compile the jar file
```shell
mvn clean package
```

### 2. Run the jar file
```shell
./target
```
- To Run OracleAggregationProcessor:
```shell
java -cp ckn-stream-processors-0.1-SNAPSHOT.jar edu.d2i.ckn.OracleAggregationProcessor 
```

- To Run OracleAccAlertProcessor:
```shell
java -cp ckn-stream-processors-0.1-SNAPSHOT.jar edu.d2i.ckn.OracleAccAlertProcessor
```

