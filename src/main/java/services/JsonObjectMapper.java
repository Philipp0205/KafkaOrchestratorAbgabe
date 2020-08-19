package services;

import avro.Event;
import avro.Property;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JsonObjectMapper {
    byte[] jsonData;
    ObjectMapper objectMapper;
    JsonNode array;

    List<Event> eventList = new ArrayList<>();

    public JsonObjectMapper(byte[] jsonData) throws IOException {
        this.jsonData = jsonData;
        this.objectMapper = new ObjectMapper();
        this.array = objectMapper.readValue(jsonData, JsonNode.class);
    }

    public void getProperties() {
    }

    public void getAllEvents() throws Exception {

        Iterator<Map.Entry<String, JsonNode>> events = array.get("events").fields();

        while (events.hasNext()) {
            List<Property> propertieList = new ArrayList<>();
            Map.Entry<String, JsonNode> event = events.next();
            JsonNode properties = event.getValue().get("properties");

            Property manualInstance = Property.newBuilder()
                    .setKey("1")
                    .setName(properties.get("name").asText())
                    .setDate(properties.get("date").asText())
                    .build();

            // AutoaAvroCoder wird hier umgangen
            //Object dynamicInstance = AvroAutoCoder.createAndSet(APropertie.class, prop -> properties.get(prop).asText());

            //propertieList.add((APropertie) dynamicInstance);
            propertieList.add((Property) manualInstance);

            Event aEvent = Event.newBuilder()
                    .setName(event.getKey())
                    .setAPropertie(propertieList)
                    .build();
            eventList.add(aEvent);
        }

        System.out.println("Eventlist" + eventList);
    }

    public List<Event> getAEvents() {
        return eventList;
    }

    public void setAEvents(List<Event> avroEvent2s) {
        this.eventList = avroEvent2s;
    }
}
