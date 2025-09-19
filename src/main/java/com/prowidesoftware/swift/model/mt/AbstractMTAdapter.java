/*
 * Copyright 2006-2023 Prowide
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.prowidesoftware.swift.model.mt;

import com.google.gson.*;
import com.prowidesoftware.swift.model.*;
import com.prowidesoftware.swift.model.field.Field;
import com.prowidesoftware.swift.model.mt.mt1xx.MT101.SequenceB;
import org.apache.commons.lang3.EnumUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Json serialization for AbstractMT and subclasses using Gson.
 *
 * @since 7.10.3
 */
public class AbstractMTAdapter implements JsonSerializer<AbstractMT>, JsonDeserializer<AbstractMT> {

	private static final transient Logger log = Logger.getLogger(AbstractMTAdapter.class.getName());
	public static final String BLOCK1_FINAL_NAME = "basicHeaderBlock";
	public static final String BLOCK2_FINAL_NAME = "applicationHeaderBlock";
	public static final String BLOCK3_FINAL_NAME = "userHeaderBlock";
	public static final String BLOCK4_FINAL_NAME = "textBlock";
	public static final String BLOCK5_FINAL_NAME = "trailerBlock";

    public static final boolean isAPITest = true;
    /**
     * Parses the JSON array with fields into specific Field instances
     */
    private static List<Field> parseFields(JsonElement fieldsElement) {
        List<Field> fields = new ArrayList<>();
        for (JsonElement element : fieldsElement.getAsJsonArray()) {
            Field field = Field.fromJson(element.toString());
            if (field != null) {
                fields.add(field);
            }
        }
        return fields;
    }

    @Override
    public JsonElement serialize(AbstractMT src, Type typeOfSrc, JsonSerializationContext context) {
        String json = src.m.toJson();
        JsonObject o = JsonParser.parseString(json).getAsJsonObject();
        JsonObject response = new JsonObject();

        response.addProperty("type", "MT");

        if (src.m.getBlock1() != null) {
            // default serialization from SwiftMessage
            response.add(BLOCK1_FINAL_NAME, o.get("data").getAsJsonObject().get("block1"));
        }

        if (src.m.getBlock2() != null) {
            // default serialization from SwiftMessage
            response.add(BLOCK2_FINAL_NAME, o.get("data").getAsJsonObject().get("block2"));
        }

        if (src.m.getBlock3() != null && !src.m.getBlock3().getTags().isEmpty()) {
        	if(isAPITest)
        		setFinalBlock3(response, "block3", src.m.getBlock3().getTags());
        	else        		
        		setFinalBlockNameAndFields(response, "block3", src.m.getBlock3().getTags());
        }

        if (src.m.getBlock4() != null && !src.m.getBlock4().getTags().isEmpty()) {
        	if(isAPITest)
        		setFinalBlock4(response, "block4", src);
        	else        		
        		setFinalBlockNameAndFields(response, "block4", src.m.getBlock4().getTags());
        }

        if (src.m.getBlock5() != null && !src.m.getBlock5().getTags().isEmpty()) {
            // default serialization from SwiftMessage with tags renamed to fields
        	if(isAPITest)
        		setFinalBlock5(response, "block5", src.m.getBlock5().getTags());
        	else {
        		JsonArray tags = o.get("data")
	                    .getAsJsonObject()
	                    .get("block5")
	                    .getAsJsonObject()
	                    .get("tags")
	                    .getAsJsonArray();
	            JsonObject trailer = new JsonObject();
	            trailer.add("fields", tags);
	            response.add(BLOCK5_FINAL_NAME, trailer);
        	}
        }

        return response;
    }

    @Override
    public AbstractMT deserialize(
            JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        SwiftMessage swiftMessage = new SwiftMessage();

        SwiftBlock1 block1 =
                jsonDeserializationContext.deserialize(jsonObject.get(BLOCK1_FINAL_NAME), SwiftBlock1.class);
        if (block1 != null) {
            swiftMessage.addBlock(block1);
        }

        SwiftBlock2 block2 =
                jsonDeserializationContext.deserialize(jsonObject.get(BLOCK2_FINAL_NAME), SwiftBlock2.class);
        if (block2 != null) {
            swiftMessage.addBlock(block2);
        }

        JsonElement userHeaderBlock = jsonObject.get(BLOCK3_FINAL_NAME);
        if (userHeaderBlock != null) {
            JsonElement fields = userHeaderBlock.getAsJsonObject().get("fields");
            if (fields != null) {
                SwiftBlock3 block3 = new SwiftBlock3();
                block3 = (SwiftBlock3) setFieldsOnBlock(fields, block3);
                swiftMessage.addBlock(block3);
            }
        }

        JsonElement textBlock = jsonObject.get(BLOCK4_FINAL_NAME);
        if (textBlock != null) {
            JsonElement fields = textBlock.getAsJsonObject().get("fields");
            if (fields != null) {
                SwiftBlock4 block4 = new SwiftBlock4();
                block4 = (SwiftBlock4) setFieldsOnBlock(fields, block4);
                swiftMessage.addBlock(block4);
            }
        }

        JsonElement trailerBlock = jsonObject.get(BLOCK5_FINAL_NAME);
        if (trailerBlock != null) {
            JsonElement fields = trailerBlock.getAsJsonObject().get("fields");
            if (fields != null) {
                SwiftBlock5 block5 = new SwiftBlock5();
                for (JsonElement element : fields.getAsJsonArray()) {
                    Tag tag = new Tag();
                    tag.setName(element.getAsJsonObject().get("name").getAsString());
                    // trailer tags can have null value (for example PDE field)
                    JsonElement valueElement = element.getAsJsonObject().get("value");
                    if (valueElement != null) {
                        tag.setValue(valueElement.getAsString());
                    }
                    block5.append(tag);
                }
                swiftMessage.addBlock(block5);
            }
        }

        return swiftMessage.toMT();
    }

    private SwiftTagListBlock setFieldsOnBlock(JsonElement fields, SwiftTagListBlock block) {
        for (Field field : parseFields(fields)) {
            block.append(field);
        }
        return block;
    }

    private void setFinalBlockNameAndFields(JsonObject response, String blockName, List<Tag> tags) {
        String finalBlockName = BLOCK4_FINAL_NAME;
        if (blockName.equals("block3")) {
            finalBlockName = BLOCK3_FINAL_NAME;
        } else if (blockName.equals("block5")) {
            finalBlockName = BLOCK5_FINAL_NAME;
        }
        JsonArray fields = getFieldsFromTags(tags);
        JsonObject block = new JsonObject();
        block.add("fields", fields);
        response.add(finalBlockName, block);
    }
    private void setFinalBlock3(JsonObject response, String blockName, List<Tag> tags) {
        JsonObject block = new JsonObject();
        block.add(BLOCK3_FINAL_NAME+"Fields", getFieldsFromTags1(tags));
        response.add(BLOCK3_FINAL_NAME, block);
    }
    private void setFinalBlock4(JsonObject response, String blockName, AbstractMT amt) {
        JsonObject block = new JsonObject();
        block.add(BLOCK4_FINAL_NAME+"Fields", getFieldsFromSequence1(amt));
        response.add(BLOCK4_FINAL_NAME, block);
    }
    private void setFinalBlock5(JsonObject response, String blockName, List<Tag> tags) {
        JsonObject block = new JsonObject();
        block.add(BLOCK5_FINAL_NAME+"Fields", getFieldsFromTags2(tags));
        response.add(BLOCK5_FINAL_NAME, block);
    }
    /**
     * Converts the tag elements into fields, and the fields into json
     */
    private JsonArray getFieldsFromTags(List<Tag> tags) {
        JsonArray fields = new JsonArray();
        for (Tag tag : tags) {
            String json = tag.asField().toJson();
            JsonObject jsonObj = JsonParser.parseString(json).getAsJsonObject();
            fields.add(jsonObj);
        }
        return fields;
    }
    /**
     * Converts the tag elements converted to json (field+Name, JsonObject), and then fields into json
     * to add the filedName in fields
     */
    private JsonObject getFieldsFromTags1(List<Tag> tags) {
        //JsonArray fields = new JsonArray();
        JsonObject jsonObj = new JsonObject();
        for (Tag tag : tags) {
        	if(tag!=null && tag.asField()!=null) {
	            String json = tag.asField().toJson();
	            jsonObj.add("field"+tag.asField().getName(), JsonParser.parseString(json).getAsJsonObject());
        	}
        }
        //fields.add(jsonObj);
        return jsonObj;
    }
    private JsonObject getFieldsFromTags2(List<Tag> tags) {
        //JsonArray fields = new JsonArray();
        JsonObject jsonObj = new JsonObject();
        for (Tag tag : tags) {
        	JsonObject jsonTag = new JsonObject();
        	jsonTag.addProperty("name",tag.getName());
        	jsonTag.addProperty("value",tag.getValue());
        	jsonObj.add("field"+tag.getName(), jsonTag);
        }
        //fields.add(jsonObj);
        return jsonObj;
    }
    /**
     * Converts the tag elements into fields, and the fields into json
     */
    @SuppressWarnings("unchecked")
	private JsonObject getFieldsFromSequence(AbstractMT amt) {
    	//System.out.println("####### MT getMessageType ######## : "+amt.getMessageType());
    	String seqEnum = "SMT"+amt.getMessageType();
    	if(amt.m.isSTP()) {
    		seqEnum = seqEnum+MTVariant.STP;
    	}
    	else if(amt.m.isREMIT()) {
    		seqEnum = seqEnum+MTVariant.REMIT;
    	}
    	else if(amt.m.isCOV()) {
    		seqEnum = seqEnum+MTVariant.COV;
    	}
        MtSequenceEnum mtSequenceEnum = MtSequenceEnum.valueOf(seqEnum);
        Set<String> sequences = mtSequenceEnum.sequences();
        JsonObject fields = new JsonObject();
        for (String seqName : sequences) {
        	if(amt.containsSequence(seqName)) {
        		//System.out.println("####### MT getMTSequenceOrList "+seqName+" ######## : "+amt.getMTSequenceOrList(seqName));
        		Object seqObj = amt.getMTSequenceOrList(seqName);
        		if(seqObj instanceof List) {
        			JsonArray sequenceList = new JsonArray();
        			List<SwiftTagListBlock> seqBlockList = (List<SwiftTagListBlock>) seqObj;
        			for (SwiftTagListBlock seqBlock : seqBlockList) {
        				//TODO REPEAT Starts Here
        				MtSequenceEnum mtRepeatEnum = MtSequenceEnum.valueOf("RMT"+amt.getMessageType());
        				Set<String> repeatTags = mtRepeatEnum.sequences();
        				JsonObject sequence = new JsonObject();
        				for (Tag tag : seqBlock.getTags()) {
        					Field field = tag.asField();
        					String tagName = field.getName();
        					String json = field.toJson();
        					if(repeatTags.contains(tagName)) {
        						String repeatTagName = "RepeatField"+tagName;
        						if(sequence.get(repeatTagName)==null) {
        							JsonArray repeatArray = new JsonArray();
        							repeatArray.add(JsonParser.parseString(json).getAsJsonObject());
            						sequence.add(repeatTagName, repeatArray);
        						} else {
        							((JsonArray)sequence.get(repeatTagName)).add(JsonParser.parseString(json).getAsJsonObject());
            					}
        					} else {
	        					sequence.add("field"+tag.asField().getName(), JsonParser.parseString(json).getAsJsonObject());
        					}
        				}
        				sequenceList.add(sequence);
        			}
        			fields.add("sequence"+seqName,sequenceList);
        		} else {
        			JsonObject sequence = new JsonObject();
        			SwiftTagListBlock seqBlock = (SwiftTagListBlock) seqObj;
        			for (Tag tag : seqBlock.getTags()) {
                        String json = tag.asField().toJson();
                        sequence.add("field"+tag.asField().getName(), JsonParser.parseString(json).getAsJsonObject());
                    }
        			fields.add("sequence"+seqName,sequence);
        		}
        	}
        }
        return fields;
    }
    
    @SuppressWarnings("unchecked")
    private JsonObject getFieldsFromSequence1(AbstractMT amt) {
    	try {
	        String messageType = amt.getMessageType();
	
	        // Build Enum Names
	        String seqEnum = buildEnumName("SMT", messageType, amt);
	        String repeatEnum = buildEnumName("RMT", messageType, amt);
	        
	        // Get Sequences and Repeat Tags
	        if (EnumUtils.isValidEnum(MtSequenceEnum.class, seqEnum)) {
	        	MtSequenceEnum mtSequenceEnum = MtSequenceEnum.valueOf(seqEnum);
	            Set<String> sequences = mtSequenceEnum.sequences();
	
	            Set<String> repeatTags = new HashSet<String>();
	            if (EnumUtils.isValidEnum(MtSequenceEnum.class, repeatEnum)) {
	                MtSequenceEnum mtRepeatEnum = MtSequenceEnum.valueOf(repeatEnum);
			        repeatTags = mtRepeatEnum.sequences();
				} /*
					 * else { System.out.println("Invalid MTSequence Repetition Enum value: " +
					 * repeatEnum); }
					 */
	            
		        JsonObject fields = new JsonObject();
		        for (String seqName : sequences) {
		            if (!amt.containsSequence(seqName)) continue;
		
		            Object seqObj = amt.getMTSequenceOrList(seqName);
		
		            if (seqObj instanceof List<?>) {
		               	fields.add("sequence" + seqName, processSequenceList((List<SwiftTagListBlock>) seqObj, repeatTags));
					} else if (seqObj instanceof SwiftTagListBlock) {
		                fields.add("sequence" + seqName, processSequenceBlock((SwiftTagListBlock) seqObj, repeatTags));
		            }
		        }
		        return fields;
	        } else {
	            System.out.println("Invalid MTSequenceEnum value: " + seqEnum);
	        }
    	}
        catch (Exception e) {
        	throw new IllegalArgumentException(e.getMessage());
		}

        return new JsonObject();
    }

    private String buildEnumName(String prefix, String messageType, AbstractMT amt) {
        StringBuilder enumName = new StringBuilder(prefix).append(messageType);

        if (amt.m.isSTP()) {
            enumName.append(MTVariant.STP);
        } else if (amt.m.isREMIT()) {
            enumName.append(MTVariant.REMIT);
        } else if (amt.m.isCOV()) {
            enumName.append(MTVariant.COV);
        }

        return enumName.toString();
    }

    private JsonArray processSequenceList(List<SwiftTagListBlock> seqBlockList, Set<String> repeatTags) throws Exception {
        JsonArray sequenceList = new JsonArray();
        for (SwiftTagListBlock seqBlock : seqBlockList) {
            JsonObject sequence = new JsonObject();
            for (Tag tag : seqBlock.getTags()) {
                processTag(tag, sequence, repeatTags);
            }
            sequenceList.add(sequence);
        }
        return sequenceList;
    }

    private JsonObject processSequenceBlock(SwiftTagListBlock seqBlock, Set<String> repeatTags) throws Exception {
        JsonObject sequence = new JsonObject();
        for (Tag tag : seqBlock.getTags()) {
        	processTag(tag, sequence, repeatTags);
        }
        return sequence;
    }

    private void processTag(Tag tag, JsonObject sequence, Set<String> repeatTags) throws Exception {
    	try {
    		String tagName = tag.asField().getName();
            JsonObject json = JsonParser.parseString(tag.asField().toJson()).getAsJsonObject();

//            if (repeatTags.contains(tagName)) {
//                String repeatTagName = "RepeatField" + tagName;
//                sequence.computeIfAbsent(repeatTagName, k -> new JsonArray()).getAsJsonArray().add(json);
//            } else {
//                sequence.add("field" + tagName, json);
//            }
            
            if (!repeatTags.isEmpty() && repeatTags.contains(tagName)) {
                String repeatTagName = "RepeatField" + tagName;
                if (!sequence.has(repeatTagName)) {
                    JsonArray repeatArray = new JsonArray();
                    sequence.add(repeatTagName, repeatArray);
                }
                sequence.getAsJsonArray(repeatTagName).add(json);
            } else {
                sequence.add("field" + tagName, json);
            }
		} catch (Exception e) {
			if(e.getMessage() != null && e.getMessage().isEmpty())
				log.warning(e.getMessage());
		}
    }
}
