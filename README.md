# CKN Stream Processor for TAPIS Camera Traps Application

The **CKN Stream Processor** processes events in real-time from the TAPIS Camera Traps application using Apache Kafka.

## Getting Started

### Step 1: Set Up the Message Broker

Set the broker environment variable for the stream processor:
```shell
export CKN_BROKERS="<CKN_BROKER_ADDRESS>"
```

Check the relevant configuration files for additional settings.

### Step 2: Compile the JAR File

Ensure the JAR files are in the `./target` directory. If not, compile them by running:
```shell
mvn clean package
```

### Step 3: Build and Run the Docker Container

1. **Build the Docker Image**: Use the appropriate Dockerfile for the stream processor.
    ```shell
    docker build -f Dockerfile.RawAlert -t ckn-processor-oracle-alert .
    ```

2. **Run the Docker Container**:
    ```shell
    docker run --name ckn-alerter ckn-processor-oracle-alert
    ```

---

## Building from Source

### Prerequisites

- **Maven 3**: For building and managing dependencies.
- **JDK**: Ensure the Java Development Kit is installed.

### Compile the JAR File

1. From the project root, compile the code by running:
    ```shell
    mvn clean package
    ```

2. Navigate to the `/target` directory to find the generated JAR files.

### Running the Processors

After building, you can execute the processors directly:

- **OracleAggregationProcessor**:
    ```shell
    java -cp ckn-stream-processors-0.1-SNAPSHOT.jar edu.d2i.ckn.OracleAggregationProcessor
    ```

- **OracleAccAlertProcessor**:
    ```shell
    java -cp ckn-stream-processors-0.1-SNAPSHOT.jar edu.d2i.ckn.OracleAccAlertProcessor
    ```

---

## Testing

For detailed testing instructions, refer to the `README.md` located in the `plugins/oracle_ckn_daemon/tests` directory on the [CKN GitHub repository](https://github.com/Data-to-Insight-Center/cyberinfrastructure-knowledge-network).

---

## License

The Cyberinfrastructure Knowledge Network (CKN) is developed by the Indiana University Board of Trustees and distributed under the BSD 3-Clause License. See `LICENSE.txt` for more details.

---

## Reference

S. Withana and B. Plale, "CKN: An Edge AI Distributed Framework," *2023 IEEE 19th International Conference on e-Science (e-Science)*, Limassol, Cyprus, 2023, pp. 1-10, doi: 10.1109/e-Science58273.2023.10254827.
