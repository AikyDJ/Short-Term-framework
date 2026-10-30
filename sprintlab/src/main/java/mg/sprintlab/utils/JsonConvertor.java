package mg.sprintlab.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JsonConvertor {
    private final ObjectMapper objectMapper;

    public JsonConvertor() {
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public String toJson(Object toConvert) throws JsonProcessingException {
        return this.objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(toConvert);
    }
}
