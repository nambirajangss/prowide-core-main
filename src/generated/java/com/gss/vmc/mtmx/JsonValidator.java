package com.gss.vmc.mtmx;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

/**
 * Validates MT JSON files against a schema or custom rules.
 * (Note: Since no formal JSON schema is provided, implement custom validation logic here.)
 */
public class JsonValidator {

    /**
     * Validates the given JSON file against custom rules or a schema.
     *
     * @param jsonFilePath Path to the JSON file.
     * @throws IOException If file reading fails.
     * @throws Exception If validation fails.
     */
    public void validate(String jsonFilePath) throws IOException, Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(new File(jsonFilePath));
        
        // Custom validation examples:
        // Check required blocks: block1, block2, block4, etc.
        if (!jsonNode.has("block1") || !jsonNode.has("block2") || !jsonNode.has("block4")) {
            throw new Exception("Missing required MT blocks in JSON.");
        }
        
        // Check messageType in block2
        String messageType = jsonNode.path("block2").path("messageType").asText();
        if (!"103".equals(messageType) && !"202".equals(messageType)) {
            throw new Exception("Unsupported MT message type: " + messageType);
        }
        
        // Add more field-specific validations as needed
    }
}