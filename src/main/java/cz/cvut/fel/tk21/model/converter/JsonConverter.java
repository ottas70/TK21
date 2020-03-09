package cz.cvut.fel.tk21.model.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Converter
public class JsonConverter implements AttributeConverter<Map<String, Object>, String> {

    private static final Logger log = LoggerFactory.getLogger(JsonConverter.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, Object> stringObjectMap) {
        String json = null;
        try {
            json = objectMapper.writeValueAsString(stringObjectMap);
        } catch (final JsonProcessingException e) {
            log.error("JSON writing error", e);
            json = "";
        }

        return json;
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String s) {
        Map<String, Object> map = null;
        try {
            map = objectMapper.readValue(s, Map.class);
        } catch (final IOException e) {
            log.error("JSON reading error", e);
            map = new HashMap<>();
        }

        return map;
    }

}
