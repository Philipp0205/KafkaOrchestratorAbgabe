package services;

import avro.Property;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class DynamicBuilder {
    byte[] jsonData;
    JsonNode properties;

    public DynamicBuilder(byte[] jsonData, JsonNode properties) throws IOException {
        this.jsonData = jsonData;
        this.properties = properties;
    }

    public void getAvroObject() {
        Property apropertie = Property.newBuilder()
                .setName(properties.get(" name").asText())
                .setDate(properties.get(" date").asText())
                .build();
    }
}
