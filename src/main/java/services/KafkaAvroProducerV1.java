package services;

import avro.*;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.log4j.BasicConfigurator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;


public class KafkaAvroProducerV1 {

    public KafkaAvroProducerV1() throws IOException {
    }

    public static void main(String[] args) throws Exception {
        BasicConfigurator.configure();
        Properties properties = new Properties();
        // normal producer
        properties.setProperty("bootstrap.servers", "127.0.0.1:9092");
        properties.setProperty("acks", "all");
        properties.setProperty("retries", "10");

        TopicHandler topicHandler = new TopicHandler(properties);

        // avro part
        properties.setProperty("key.serializer", StringSerializer.class.getName());
        properties.setProperty("value.serializer", KafkaAvroSerializer.class.getName());
        properties.setProperty("schema.registry.url", "http://127.0.0.1:8081");
        properties.setProperty("confluent.value.schema.validation", "true");
        properties.setProperty("confluent.key.schema.validation", "true");

        byte[] jsonData = Files.readAllBytes(Paths.get("/home/philipp/Development/kafka/projects_main/kafka-orchestrator/src/main/java/resources/dataschema.json"));
        JsonObjectMapperSmall objectMapperSmall = new JsonObjectMapperSmall(jsonData);
        objectMapperSmall.getAllEvents();

        List<Event> avroEvents = objectMapperSmall.getAEvents();

        Producer<String, Event> eventProducer = new KafkaProducer<String, Event>(properties);

        String eventTopic = "avro-events";
        AtomicInteger key = new AtomicInteger(1);

        // Add eventsNames to topicList
        ArrayList<String> topics = new ArrayList<>();
        avroEvents.forEach(aEvent -> topics.add(aEvent.getName() + "-in"));
        topicHandler.createTopics(topics);


        System.out.println("Topics: " + topics.toString());

        //Integer.toString(key.get());

        /*
        avroEvents.forEach(avroEvent2 ->
                new ProducerRecord<String, AEvent>(
                        eventTopic, avroEvent2)
        );
         */

        avroEvents.stream()
                .map(avroEvent2 ->
                    new ProducerRecord<String, Event>(avroEvent2.getName() + "-in", avroEvent2))
                .forEach(stringAEventProducerRecord -> eventProducer.send(stringAEventProducerRecord, new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                        if (e == null) {
                            System.out.println(recordMetadata);
                        } else {
                            e.printStackTrace();
                        }
                    }
                }));

        eventProducer.flush();
        eventProducer.close();


    }


}
