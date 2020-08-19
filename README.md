# KafkaOrchestratorAbgabe
Die finale Abgabe des Projekts begleitend zur Bachelorarbeit "Entwicklung einer ereignisorientierten Architektur für BPM-Szenarien auf Basis von Apache Kafka"

## Übersicht über das Projekt

- [UML-Klassendiagramm](classd.png)
- [Übersicht der Komponenten](componentsd.png)

## Dependencies

### Apache Kafka installieren (Linux)
```
wget http://apache.mirrors.spacedump.net/kafka/2.4.0/kafka_2.12-2.4.0.tgz
tar -xzf kafka_2.12-2.4.0.tgz
cd kafka_2.12-2.4.0
```

### Kafka ZooKeeper starten
```
sudo bin/zookeeper-server-start.sh config/zookeeper.properties
```

### Kafker Server starten
```
sudo bin/kafka-server-start.sh config/server.properties}}
```

### Topics ausgeben
```
bin/kafka-topics.sh --bootstrap-server localhost:9092 --describe
```
oder 
```
bin/kafka-topics.sh --list --zookeeper localhost:2181
```

### Console Producer starten (nur Terminal)
```
bin/kafka-console-producer.sh --broker-list localhost:9092 --topic streams-plaintext-input
```

## Bachelorprojekt starten

### Start confulent dev-server
```
sudo docker-compose up
```

### Start confluent tools
```
sudo docker run -it --rm --net=host confluentinc/cp-schema-registry:3.3.1 bash
```

# Anderes
### Topic erstellen
```
./bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic saledorder
```

### Topic löschen
In kafka root folder:
```
bin/kafka-topics.sh --zookeeper localhost:2181 --delete --topic DummyTopic
```

### Records löschen
```
./bin/kafka-delete-records.sh --bootstrap-server localhost:9092 --offset-json-file ./offsetfile.json
```

### Ein Record mit einem Feld erzeugen
```
kafka-avro-console-producer \
    --broker-list 127.0.0.1:9092 --topic test-avro \
    --property schema.registry.url=http://127.0.0.1:8081 \
    --property value.schema='{"type":"record","name":"myrecord","fields":[{"name":"f1","type":"string"}]}'
```
