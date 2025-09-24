package com.gss.vmc.mxmt.rule.integrator;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.gss.vmc.jaxb.pacs00800109.FIToFICustomerCreditTransferV09;
import com.gss.vmc.jaxb.pacs00800109.Message;
import com.gss.vmc.mxmt.CXR17ClearingIdentifier;
import com.gss.vmc.mxmt.FieldFormatter;
import com.gss.vmc.MtJsonMessage;
import com.gss.vmc.mxmt.MxParser;
import com.prowidesoftware.swift.model.field.Field;
import com.prowidesoftware.swift.model.field.Field55A;
import com.prowidesoftware.swift.model.field.Field70;
import com.prowidesoftware.swift.model.mt.AbstractMTAdapter;

import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

/**
 * Applies MX to MT mapping rules, including custom logic like remittance information.
 * Uses pseudocode from MX_To_MTRemittanceInformation.txt for specific transformations.
 */
public class RuleEnginePacs008 {

    private final FieldFormatter fieldFormatter;

    public RuleEnginePacs008(FieldFormatter fieldFormatter) {
        this.fieldFormatter = fieldFormatter;
    }

    /**
     * Applies transformation rules to the extracted elements and builds an MtMessage POJO.
     * This includes mapping MX fields to MT fields, applying pseudocode logic for remittance.
     *
     * @param extractedElements Map of extracted XML elements.
     * @return Populated MtMessage.
     */
    public JsonObject applyRules(Message message) {
    	JsonObject response = new JsonObject();
    	response.addProperty("type", "MT");
    	MtJsonMessage mtJsonMessage = new MtJsonMessage();
		if (mtJsonMessage.getBasicHeaderBlock().toJsonElement() != null) {
            response.add(AbstractMTAdapter.BLOCK1_FINAL_NAME, mtJsonMessage.getBasicHeaderBlock().toJsonElement());
        }
		if (mtJsonMessage.getApplicationHeaderBlock().toJsonElement() != null) {
            response.add(AbstractMTAdapter.BLOCK2_FINAL_NAME, mtJsonMessage.getApplicationHeaderBlock().toJsonElement());
        }
		if (mtJsonMessage.getUserHeaderBlock().toJsonElement() != null) {
            response.add(AbstractMTAdapter.BLOCK3_FINAL_NAME, mtJsonMessage.getUserHeaderBlock().toJsonElement());
        }
		// START TEXT JSON Block preparation
		JsonObject textBlockFields = new JsonObject();
		JsonObject sequences = new JsonObject();
		JsonObject sequenceAFields = new JsonObject();
		JsonObject sequenceBFields = new JsonObject();
		
		//Apply List of Rules Below:
		applyCXR17Field70(message,sequenceAFields,sequenceBFields);
		applyCXR17Field55A(message,sequenceAFields,sequenceBFields);
		//applyCXR2(doc,mxParser,sequenceAFields,sequenceBFields);
		
		
		
		// END TEXT JSON Block preparation
		sequences.add("sequenceA", sequenceAFields);
		sequences.add("sequenceB", sequenceBFields);
		textBlockFields.add(AbstractMTAdapter.BLOCK4_FINAL_NAME+"Fields", sequences);
		//textBlockFields.add(AbstractMTAdapter.BLOCK4_FINAL_NAME+"Fields", sequenceB);
		response.add(AbstractMTAdapter.BLOCK4_FINAL_NAME, textBlockFields);
    	
        return response;
    }
    
    public void applyCXR17Field70(Message doc, JsonObject sequenceAFields, JsonObject sequenceBFields) {
    	Map<String, String> cxr17Elements;
		// STEP-1 => Extract either String or Hashmap from XML Elements 
		//cxr17Elements = mxParser.extractCXR17Elements(doc);
		
		// STEP-2 => Get XML data from Hashmap and pass to Rules input
		CXR17ClearingIdentifier cxr17 = new CXR17ClearingIdentifier();
		String mtClearingCode =  cxr17.transformClearingIdentifier(doc.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(0).getDbtr().getNm());	
		
		// STEP-3 => Extract either String or Hashmap from Rules Output and Set to the Field (Like 55A/72) values.
		Field70 f70 = new Field70();
		//f70.setComponent1(mtClearingCode);
		f70.setNarrative(doc.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(0).getDbtr().getNm());
		
		// STEP-4 => Convert Field to Json and then prepare the JsonObject
		JsonObject field70 = JsonParser.parseString(f70.toJson()).getAsJsonObject();
		
		// STEP-5 => Add the field JsonObject to the sequenceAFields/sequenceBFields
		sequenceAFields.add("field70", field70);
    }
    
    public void applyCXR17Field55A(Message doc, JsonObject sequenceAFields, JsonObject sequenceBFields) {
    	Map<String, String> cxr17Elements;
		// STEP-1 => Extract either String or Hashmap from XML Elements 
		//cxr17Elements = mxParser.extractCXR17Elements(doc);
		
		// STEP-2 => Get XML data from Hashmap and pass to Rules input
		CXR17ClearingIdentifier cxr17 = new CXR17ClearingIdentifier();
		String mtClearingCode =  cxr17.transformClearingIdentifier(doc.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(0).getDbtr().getNm());	
		
		// STEP-3 => Extract either String or Hashmap from Rules Output and Set to the Field (Like 55A/72) values.
		Field55A f55A = new Field55A();
		f55A.setAccount("mtClearingCode-TEST");
		f55A.setIdentifierCode("BICFI-TEST");
		
		// STEP-4 => Convert Field to Json and then prepare the JsonObject
		JsonObject field55A = JsonParser.parseString(f55A.toJson()).getAsJsonObject();
		
		// STEP-5 => Add the field JsonObject to the sequenceAFields/sequenceBFields
		sequenceAFields.add("field55A", field55A);
		// STEP-3 => Extract either String or Hashmap from Rules Output and Set to the Field (Like 55A/72) values.
		Field55A f55ASeqB = new Field55A();
		f55ASeqB.setAccount("mtClearingCode-TEST-SEQB");
		f55ASeqB.setIdentifierCode("BICFI-TEST-SEQB");
		
		// STEP-4 => Convert Field to Json and then prepare the JsonObject
		JsonObject field55ASeqB = JsonParser.parseString(f55ASeqB.toJson()).getAsJsonObject();
		sequenceBFields.add("field55A", field55ASeqB);
    }
    
    /**
     * Implements the MX_To_MTRemittanceInformation pseudocode logic.
     * Converts MX remittance info to MT field 70 format (4*35x).
     *
     * @param extractedElements Extracted MX elements.
     * @return Formatted remittance information string.
     */
    private String applyRemittanceRule(Map<String, String> extractedElements) {
        // Implement pseudocode logic here
        // Extract MXEndToEndId, MXUltimateCreditor, etc. from map
        // Apply cases 1-4 as per pseudocode
        // Use fieldFormatter for truncation and multi-line splitting
        String mt70FullString = ""; // Build string based on priorities
        // Example stub: Case 1 - Ultimate Creditor/Debtor, Purpose, EndToEndId
        // ... (implement conditions, concatenations, flags)
        // Finally, format to 4*35x
        return fieldFormatter.formatToMultiLine(mt70FullString, 35, 4);
    }
}

