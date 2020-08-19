package services;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class KafkaAvroConsumerV1 {

    static Map<Integer, List<com.example.kafkaorch.AEvent>> processIds = new HashMap<>();

    public static void main(String[] args) throws ExecutionException, InterruptedException {


        Properties properties = new Properties();
        // normal consumer
        properties.setProperty("bootstrap.servers", "127.0.0.1:9092");
        properties.put("group.id", "customer-consumer-group-v1");
        properties.put("auto.commit.enable", "false");
        properties.put("auto.offset.reset", "earliest");

        TopicHandler topicHandler = new TopicHandler(properties);

        // avro part (deserializer)
        properties.setProperty("key.deserializer", StringDeserializer.class.getName());
        properties.setProperty("value.deserializer", KafkaAvroDeserializer.class.getName());
        properties.setProperty("schema.registry.url", "http://127.0.0.1:8081");
        properties.setProperty("specific.avro.reader", "true");

        KafkaConsumer<String, com.example.kafkaorch.AEvent> kafkaConsumer = new KafkaConsumer<>(properties);
        //String topic = "avro-events6";
        //String[] topics = {"avro-events6", "avro-events7"};
        List<String> topics = topicHandler.listTopics();
        String[] systemTopics = {"_schemas", "connect-statuses", "connect-offsets", "connect-configs"};
        Arrays.stream(systemTopics)
                .forEach(topic -> topics.remove(topic));

        System.out.println("Topics:" + topics);
        kafkaConsumer.subscribe(topics);

        System.out.println("Subscribed to topics: " + topics.toString());
        System.out.println("Waiting for data...");


        while (true) {
            System.out.println("Polling");
            ConsumerRecords<String, com.example.kafkaorch.AEvent> records = kafkaConsumer.poll(1000);

            for (ConsumerRecord<String, com.example.kafkaorch.AEvent> record : records) {
                com.example.kafkaorch.AEvent aEvent = com.example.kafkaorch.AEvent.newBuilder()
                        .setName(record.value().getName())
                        .setAPropertie(record.value().getAPropertie())
                        .build();

                topics.add(aEvent.getName());

                if (checkIfFirstEvent(aEvent)) {
                    System.out.println("true");
                } else {
                    assignEventToProcess(aEvent);
                    System.out.println("Not first event in process. Trying to assign existing Process.");
                }

                createNewProcessInstance(aEvent);

                System.out.println("Eventname: " + aEvent.getName());
                System.out.println(aEvent.getAPropertie().get(0));

            }
            kafkaConsumer.commitSync();
        }
    }

    private static boolean checkIfFirstEvent(com.example.kafkaorch.AEvent event) {
        return event.getAPropertie().get(0).getKey().equals("1");
    }

    static int i = 0;

    private static void createNewProcessInstance(com.example.kafkaorch.AEvent event) {
        if (checkIfFirstEvent(event)) {
            List<com.example.kafkaorch.AEvent> aevents = new ArrayList<>();
            aevents.add(event);
            processIds.put(i, aevents);
            System.out.println("new ProcessInstance created: ID: " + String.valueOf(i));
            i++;
        } else {
            System.out.println("Assign Event to Process 2");
            assignEventToProcess(event);
        }
    }

    private static void assignEventToProcess(com.example.kafkaorch.AEvent event) {
        System.out.println("assignEveht");
        processIds.forEach((key, eventlist) -> {
            if (eventlist.size() > 1 ) {
                System.out.println();
                System.out.println("ERROR. Could not assign event to process!");
            } else if (processIds.size() > key) {
                System.out.println("Assigned event to processId " + key.toString());
                eventlist.add(event);
            } else {
                System.out.println("ERROR. Could not assign event to process!");
            }
        });
    }
}
