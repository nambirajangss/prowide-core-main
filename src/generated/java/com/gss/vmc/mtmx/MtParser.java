package com.gss.vmc.mtmx;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gss.vmc.MtJsonMessage; // Reuse the MtJsonMessage POJO from MX to MT
import com.gss.vmc.json.MTMasterFieldsJson;

import java.io.File;
import java.io.IOException;

/**
 * Parses MT JSON files into MtJsonMessage POJO.
 */
public class MtParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Parses the JSON file into an MtJsonMessage POJO.
     *
     * @param jsonFilePath Path to the JSON file.
     * @return Parsed MtJsonMessage.
     * @throws IOException If file reading fails.
     */
    public MTMasterFieldsJson parse(String jsonFilePath) throws IOException {
        return objectMapper.readValue(new File(jsonFilePath), MTMasterFieldsJson.class);
    }
}