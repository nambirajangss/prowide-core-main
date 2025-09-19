package com.prowidesoftware.controller;
import com.google.gson.*;

import java.util.Map;

public class JsonFlattener {
    
    public static String flattenJson(String jsonString) throws NullPointerException {
    	try {
	    	JsonObject structuredJson = JsonParser.parseString(jsonString).getAsJsonObject();
	        JsonObject flattenJson = structuredJson.deepCopy();
	        
	        // Get "userHeaderBlockFields" from input JSON
	        JsonArray flatBlock3Array = new JsonArray();
	        if(structuredJson.getAsJsonObject("userHeaderBlock")!=null) {
		        JsonObject userBlock3Fields = structuredJson.getAsJsonObject("userHeaderBlock").getAsJsonObject("userHeaderBlockFields");
		        for (Map.Entry<String, JsonElement> entry : userBlock3Fields.entrySet()) { // Recursively process each sequence and flatten
		        	extractBlock3and5Fields(entry.getValue(), flatBlock3Array);
		        }
		        flattenJson.getAsJsonObject("userHeaderBlock").remove("userHeaderBlockFields");
		        flattenJson.getAsJsonObject("userHeaderBlock").add("fields", flatBlock3Array);
	        }
	        // Get "textBlockFields" from input JSON
	        JsonArray flatBlock4Array = new JsonArray();
	        JsonObject textBlock4Fields = structuredJson.getAsJsonObject("textBlock").getAsJsonObject("textBlockFields");
	        for (Map.Entry<String, JsonElement> entry : textBlock4Fields.entrySet()) { // Recursively process each sequence and flatten
	            extractBlock4Fields(entry.getValue(), flatBlock4Array);
	        }
	        flattenJson.getAsJsonObject("textBlock").remove("textBlockFields");
	        flattenJson.getAsJsonObject("textBlock").add("fields", flatBlock4Array);
	        
	        // Get "trailerBlockFields" from input JSON
	        JsonArray flatBlock5Array = new JsonArray();
	        if(structuredJson.getAsJsonObject("trailerBlock")!=null) {
		        JsonObject trailerBlock5Fields = structuredJson.getAsJsonObject("trailerBlock").getAsJsonObject("trailerBlockFields");
		        for (Map.Entry<String, JsonElement> entry : trailerBlock5Fields.entrySet()) { // Recursively process each sequence and flatten
		            extractBlock3and5Fields(entry.getValue(), flatBlock5Array);
		        }
		        flattenJson.getAsJsonObject("trailerBlock").remove("trailerBlockFields");
		        flattenJson.getAsJsonObject("trailerBlock").add("fields", flatBlock5Array);
	        }
	        
	        Gson gson = new GsonBuilder().setPrettyPrinting().create();
	        return gson.toJson(flattenJson);
    	} catch (NullPointerException e) {
			throw e;
		}
    }
    
    private static void extractBlock3and5Fields(JsonElement element, JsonArray flatArray) {
    	if (element.isJsonObject()) {
            flatArray.add(element.getAsJsonObject());
        }
    }

    private static void extractBlock4Fields(JsonElement element, JsonArray flatArray) throws NullPointerException {
    	try {
			if (element.isJsonObject()) {
	            JsonObject jsonObject = element.getAsJsonObject();
	
	            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
	                JsonElement value = entry.getValue();
	
	                if (value.isJsonObject()) {
	                    // Extract and directly add the value object (ignoring field name like "field20")
	                    flatArray.add(value.getAsJsonObject());
	                } else if (value.isJsonArray()) {
	                    // Process nested array elements
	                    //extractFields(value, flatArray);
	                	// Process nested arrays inside sequenceC (RepeatField13C)
	                    for (JsonElement subElement : value.getAsJsonArray()) {
	                        if (subElement.isJsonObject()) {
	                            flatArray.add(subElement.getAsJsonObject());
	                        }
	                    }
	                }
	            }
	        } else if (element.isJsonArray()) {
	            JsonArray jsonArray = element.getAsJsonArray();
	            for (JsonElement jsonElement : jsonArray) {
	                if (jsonElement.isJsonObject()) {
	                    JsonObject nestedObject = jsonElement.getAsJsonObject();
	                    for (Map.Entry<String, JsonElement> entry : nestedObject.entrySet()) {
	                        JsonElement value = entry.getValue();
	
	                        if (value.isJsonObject()) {
	                            // Extract objects inside arrays (corrects sequenceBList issue)
	                            flatArray.add(value.getAsJsonObject());
	                        } else if (value.isJsonArray()) {
	                            // Process nested arrays inside sequenceBList & sequenceC (RepeatField71F, RepeatField13C)
	                            for (JsonElement subElement : value.getAsJsonArray()) {
	                                if (subElement.isJsonObject()) {
	                                    flatArray.add(subElement.getAsJsonObject());
	                                }
	                            }
	                        }
	                    }
	                }
	            }
	        }
		} catch (NullPointerException e) {
			throw e;
		}
    }

    public static void main(String[] args) throws Exception {
        String jsonString = "{"
                + "\"textBlock\": {"
                + "    \"textBlockFields\": {"
                + "        \"sequenceA\": {"
                + "            \"field20\": {"
                + "                \"name\": \"20\","
                + "                \"reference\": \"REFERENCE12345\""
                + "            },"
                + "            \"field23\": {"
                + "                \"name\": \"23\","
                + "                \"code1\": \"CREDIT\""
                + "            }"
                + "        },"
                + "        \"sequenceBList\": ["
                + "            {"
                + "                \"field21\": {"
                + "                    \"name\": \"21\","
                + "                    \"reference\": \"PREVREF123\""
                + "                },"
                + "                \"field32B\": {"
                + "                    \"name\": \"32B\","
                + "                    \"currency\": \"USD\","
                + "                    \"amount\": \"2500,00\""
                + "                },"
                + "                \"RepeatField71F\": ["
                + "                    {"
                + "                        \"name\": \"71F\","
                + "                        \"currency\": \"USD\","
                + "                        \"amount\": \"10,00\""
                + "                    },"
                + "                    {"
                + "                        \"name\": \"71F\","
                + "                        \"currency\": \"EUR\","
                + "                        \"amount\": \"5,00\""
                + "                    }"
                + "                ]"
                + "            }"
                + "        ],"
                + "        \"sequenceC\": {"
                + "            \"field32A\": {"
                + "                \"name\": \"32A\","
                + "                \"date\": \"240328\","
                + "                \"currency\": \"USD\","
                + "                \"amount\": \"7500,50\""
                + "            },"
                + "            \"RepeatField13C\": ["
                + "                {"
                + "                    \"name\": \"13C\","
                + "                    \"code\": \"123456\","
                + "                    \"timeIndication\": \"2025\","
                + "                    \"sign\": \"0\","
                + "                    \"timeOffset\": \"328\""
                + "                },"
                + "                {"
                + "                    \"name\": \"13C\","
                + "                    \"code\": \"789012\","
                + "                    \"timeIndication\": \"2025\","
                + "                    \"sign\": \"0\","
                + "                    \"timeOffset\": \"329\""
                + "                }"
                + "            ]"
                + "        }"
                + "    }"
                + "}"
                + "}";

        String flatJson = flattenJson(jsonString);

        // Print the transformed JSON
        System.out.println(flatJson);
    }
}
