# CKN Stream Processor for TAPIS Camera Traps Application

[![License](https://img.shields.io/badge/License-BSD%203--Clause-blue.svg)](https://opensource.org/licenses/BSD-3-Clause)

The **CKN Stream Processor** aggregates events in real-time from the TAPIS Camera Traps application using Apache Kafka.

## Getting Started

### Quickstart

1. **Set the Message Broker environment variable**

    If a message broker is unavailable, follow the instructions in [CKN repository](https://github.com/Data-to-Insight-Center/cyberinfrastructure-knowledge-network) to start the services.

    ```shell
    export CKN_BROKERS="<CKN_BROKER_ADDRESS>"
    ```

2. **Compile the JAR File**
    Ensure the JAR files are in the `./target` directory. If not, compile them by running:
    ```shell
    mvn clean package
    ```

3. **Build and Run the Docker Container**
    ```shell
    docker build -f Dockerfile.RawAlert -t ckn-processor-oracle-alert .
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

For instructions, refer `plugins/oracle_ckn_daemon/tests/README.md` on the [CKN repository](https://github.com/Data-to-Insight-Center/cyberinfrastructure-knowledge-network).

---

## License
The Cyberinfrastructure Knowledge Network (CKN) is developed by the Indiana University Board of Trustees and distributed under the BSD 3-Clause License. See `LICENSE.txt` for more details.

## Acknowledgements
This research is funded in part through the National Science Foundation under award #2112606, AI Institute for Intelligent CyberInfrastructure with Computational Learning in the Environment (ICICLE), and in part through Data to Insight Center (D2I) at Indiana University.

## Reference
S. Withana and B. Plale, "CKN: An Edge AI Distributed Framework," *2023 IEEE 19th International Conference on e-Science (e-Science)*, Limassol, Cyprus, 2023, pp. 1-10, doi: 10.1109/e-Science58273.2023.10254827.
