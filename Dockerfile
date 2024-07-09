FROM openjdk:18-jdk-slim

WORKDIR /usr/src/app

COPY target/ckn-stream-processors-0.1-SNAPSHOT.jar .
COPY run.sh .

EXPOSE 9092

# Setting env variables, move this to docker-compose if required
ENV KAFKA_BOOTSTRAP_SERVERS=<IP>:9092
ENV ORACLE_ACC_CRITICAL_THRESHOLD=0.3
ENV ORACLE_INPUT_TOPIC=oracle-events
ENV ORACLE_ACC_ALERT_TOPIC=oracle-alerts
ENV APP_ID=ckn-camera-traps-oracle-processor2

CMD ["./run.sh"]