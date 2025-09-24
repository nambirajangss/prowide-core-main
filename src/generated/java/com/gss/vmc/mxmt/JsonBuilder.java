package com.gss.vmc.mxmt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Builds JSON from MtMessage POJO using Jackson.
 */
class JsonBuilder {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Converts MtMessage to JSON and writes to file.
     *
     * @param mtMessage MtMessage POJO.
     * @param outputPath Path to output JSON file.
     * @throws IOException If writing fails.
     */
    public void buildJson(JsonObject mtJsonRootMessage, String outputPath) throws IOException {
    	 // Pretty print JSON and save
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(outputPath)) {
            gson.toJson(mtJsonRootMessage, writer);
        }
        //objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(outputPath), mtMessage);
    }
}