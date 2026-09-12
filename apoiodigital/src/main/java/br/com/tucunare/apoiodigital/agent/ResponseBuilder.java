package br.com.tucunare.apoiodigital.agent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class ResponseBuilder {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public <T> T build(String responseRaw, Class<T> targetClass){
        try{
            return objectMapper.readValue(responseRaw, targetClass);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
