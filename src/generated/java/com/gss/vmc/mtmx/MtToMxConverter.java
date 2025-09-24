package com.gss.vmc.mtmx;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.gss.vmc.MtJsonMessage;
import com.gss.vmc.jaxb.pacs00800109.FIToFICustomerCreditTransferV09;
import com.gss.vmc.jaxb.pacs00800109.Message;
import com.gss.vmc.jaxb.pacs00900109.FinancialInstitutionCreditTransferV09;
import com.gss.vmc.json.MTMasterFieldsJson;
import com.gss.vmc.mtmx.rule.integrator.RuleEngineMtToMxPacs008;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Marshaller;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.StringWriter;
import java.util.Map;

/**
 * Main class to orchestrate the MT (JSON) to MX (XML) conversion process.
 * This class ties together validation, parsing, rule application, mapping, and XML building.
 * Mirrors the structure of MxToMtConverter but in reverse: JSON input -> POJO -> Rules -> JAXB Model -> XML output.
 */
public class MtToMxConverter {

    private final JsonValidator jsonValidator;
    private final MtParser mtParser;
    private final RuleEngineMtToMxPacs008 ruleEngineMtToMxPacs008;
    private final MxBuilder mxBuilder;
    String pacs008xsd = "D:\\Project\\VMC\\src\\main\\java\\com\\prowidesoftware\\controller\\pacs.008.001.09.xsd";
    String pacs009xsd = "D:\\Project\\VMC\\src\\main\\java\\com\\prowidesoftware\\controller\\pacs.009.001.09.xsd";
    
    public MtToMxConverter() {
        this.jsonValidator = new JsonValidator();
        this.mtParser = new MtParser();
        this.ruleEngineMtToMxPacs008 = new RuleEngineMtToMxPacs008();
        this.mxBuilder = new MxBuilder();
    }
    
    public static Object marshalToXML(Object jaxbObject, JAXBContext ctx) throws Exception {
        Marshaller marshaller = ctx.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        StringWriter writer = new StringWriter();
        marshaller.marshal(jaxbObject, writer);
        return writer.toString(); // Or parse to Document if needed
    }
    
    public static String identifyMtMessageType(String jsonFilePath) throws Exception {
        // Parse JSON to check message type (e.g., MT103, MT202)
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> jsonMap = objectMapper.readValue(new File(jsonFilePath), Map.class);
        
        // Assuming MT JSON has a "type" or "messageType" field in applicationHeaderBlock
        String messageType = (String) ((Map<String, Object>) jsonMap.get("applicationHeaderBlock")).get("messageType");
        
        if ("103".equals(messageType)) {
            return "pacs.008.001.09"; // Map MT103 to pacs.008
        } else if ("202".equals(messageType)) {
            return "pacs.009.001.09"; // Map MT202 to pacs.009
        }
        return "Unknown message type";
    }

    /**
     * Converts an MT JSON file to MX XML format.
     *
     * @param jsonFilePath Path to the input JSON file (MT format).
     * @param xsdFilePath Path to the XSD schema file for validation (optional post-generation).
     * @param outputXmlPath Path to save the output XML file.
     * @throws Exception If validation, parsing, or conversion fails.
     */
    public void convert(String jsonFilePath, String xsdFilePath, String outputXmlPath) throws Exception {
        
        // Step 1: Parse JSON and Identify the target MX type (pacs.008 or pacs.009)
        String messageType = identifyMtMessageType(jsonFilePath);
        
        if (messageType.equals("pacs.008.001.09")) {
            jsonValidator.validate(jsonFilePath); // Validate JSON structure (custom schema or rules)
            
            // Parse JSON to MTMasterFieldsJson POJO
            MTMasterFieldsJson mtJsonMessage = mtParser.parse(jsonFilePath);
            
            // Step 2: Apply rules to transform MT POJO to MX JAXB model
            Message mxMessage = ruleEngineMtToMxPacs008.applyRules(mtJsonMessage);
            
            // Step 3: Build XML from JAXB Model
            mxBuilder.buildXml(mxMessage, outputXmlPath, pacs008xsd);
            
        } else if (messageType.equals("pacs.009.001.09")) {
            // Similar flow for pacs.009
            // jsonValidator.validate(jsonFilePath);
            // MtJsonMessage mtJsonMessage = mtParser.parse(jsonFilePath);
            // FinancialInstitutionCreditTransferV09 mxModel = ruleEngineMtToMxPacs009.applyRules(mtJsonMessage); // Separate rule engine if needed
            // mxBuilder.buildXml(mxModel, outputXmlPath, pacs009xsd);
        }
    }
    
    public Object parseMtToMxBody(String jsonFilePath) throws Exception {
        // Parse JSON to MTMasterFieldsJson POJO
    	MTMasterFieldsJson mtJsonMessage = mtParser.parse(jsonFilePath);
        
        // Apply rules to map to MX model (stub for pacs.008)
        FIToFICustomerCreditTransferV09 pacs008Body = ruleEngineMtToMxPacs008.applyBodyRules(mtJsonMessage);
        
        if (pacs008Body != null) {
            return pacs008Body;
        }
        
        // Stub for pacs.009
        FinancialInstitutionCreditTransferV09 pacs009Body = new FinancialInstitutionCreditTransferV09(); // Apply rules similarly
        if (pacs009Body != null) {
            return pacs009Body;
        }
        
        return null;
    }

    public static void main(String[] args) {
        try {
            String a = "D:\\Project\\VMC\\src\\main\\java\\com\\prowidesoftware\\controller\\mt103.json";
            String b = "D:\\Project\\VMC\\src\\main\\java\\com\\prowidesoftware\\controller\\pacs.008.001.09.xsd";
            String c = "D:\\Project\\VMC\\src\\main\\java\\com\\prowidesoftware\\controller\\pacs.008.001.09-test-mtmx.xml";
            
            new MtToMxConverter().convert(a, b, c);
            System.out.println("Conversion completed successfully.");
        } catch (Exception e) {
            System.err.println("Conversion failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

// For unit tests, use JUnit5
// Example: TestMtToMxConverter.java with @Test methods for validation, parsing, rules, etc.
// Use sample files for input/output assertions.