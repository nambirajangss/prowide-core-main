package com.prowidesoftware.controller;
import antlr.StringUtils;
import com.altova.io.StringInput;
import com.altova.io.StringOutput;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.mapforce.MappingMapToMT103_REMIT_STP_pacs008;
import com.mapforce.MappingMapToissettled_apphdr_pacs009;
import com.mapforce.MappingMapToissettled_apphdr_pacs0092;
import com.mapforce.demo.*;
import com.prowidesoftware.swift.io.parser.SwiftParser;
import com.prowidesoftware.swift.model.SwiftMessage;
import com.prowidesoftware.swift.model.mt.AbstractMT;
import com.xmlpath.TestResult;
import com.xmlpath.XmlDiff;
import com.xmlpath.XmlDiffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import java.io.*;
import java.net.http.HttpHeaders;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
 
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

//import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.BaseColor;

 
@Controller
@RequestMapping("/swift")
public class SwiftMessageController {
    private static final Logger log = Logger.getLogger(SwiftMessageController.class.getName());
    private static final LogCaptureHandler logCaptureHandler = new LogCaptureHandler();
    //private static final List<String> logMessages = new ArrayList<String>();
    private static final AtomicInteger apiMt2jsonCallCount = new AtomicInteger(0);
    private static final AtomicInteger apiMt2XmlCallCount = new AtomicInteger(0);
    private static final AtomicInteger apiJson2mtCallCount = new AtomicInteger(0);
    private static final AtomicInteger apiMt2json2mtCallCount = new AtomicInteger(0);
    String foundErrorMsg = "FOUND BELOW VALIDATION ERRORS:\n Please revalidate the malformed Block structure (or) unrecognized Field in the MT message:\n";
    String msgtype = "MT message type identification in the form of businessprocess.messagetype.variant: ";
    String logMsgService1 = "API Request /swift/mt2json Process ID: REQMT2JSON101";
    String logMsgService2 = "API Request /swift/json2mt Process ID: REQJSON2MT201";
    String logMsgService3 = "API Request /swift/mt2json2mt Process ID: REQMT2JSON2MT301";
    String start = " START";
    String end = " END";
    String exception = " EXCEPTION";
    static {
        try {
            // Setup FileHandler for FileProcessor.log
            FileHandler fileHandler = new FileHandler("SwiftMessageController.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
 
            log.addHandler(fileHandler);
            log.setLevel(Level.INFO);
            log.setUseParentHandlers(false); // Disable default console logging
            // Set global logging to capture third-party logs
            Logger rootLogger = Logger.getLogger("");
            rootLogger.addHandler(fileHandler);
            rootLogger.setLevel(Level.INFO);
            rootLogger.addHandler(logCaptureHandler);
 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void logClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For"); // Check for proxy
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr(); // Direct connection
        }
        log.info("Client IP: "+ip+" called endpoint: "+request.getRequestURI());
    }
    @GetMapping("/parse")
    public String parse() {
    	try {
    		return MessageToJsonExample.jsonTest("");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return "Hello, Spring Booted!";
    }
    
    @Autowired
    private RestTemplate restTemplate;
    
    @PostMapping(value = "/mt2json", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> mt2json(@RequestBody String swiftMessage, HttpServletRequest request) {
    	logCaptureHandler.clearLogs();
    	int currentCount = apiMt2jsonCallCount.incrementAndGet();
    	log.info(logMsgService1 + currentCount + start);
    	logClientIp(request);

    	try {
    		ParsedResult result = parseAndValidate(swiftMessage);
    		if (result.hasError()) {
    			return ResponseEntity.status(500).contentType(MediaType.TEXT_PLAIN).body(foundErrorMsg + result.getErrorMsg());
    		}
    		log.info(msgtype + result.getMtId());
    		log.info(logMsgService1 + currentCount + end);
    		return ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON).body(result.getJson());
    	} catch (Exception e) {
    		log.severe(e.getMessage());
    		log.info(logMsgService1 + currentCount + exception);
    		e.printStackTrace();
    		return ResponseEntity.status(500).contentType(MediaType.TEXT_PLAIN).body("Internal Server Error: Found input validation issues in logs.\n" + e.getMessage());
    	}
    }
    
    @PostMapping(value = "/demo/mt2json", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> demomt2json(@RequestBody String swiftMessage, HttpServletRequest request) {
    	logCaptureHandler.clearLogs();
    	int currentCount = apiMt2jsonCallCount.incrementAndGet();
    	log.info(logMsgService1 + currentCount + start);
    	logClientIp(request);

    	try {
    		ParsedResult result = parseAndValidate(swiftMessage);
    		if (result.hasError()) {
    			return ResponseEntity.status(500).contentType(MediaType.TEXT_PLAIN).body(foundErrorMsg + result.getErrorMsg());
    		}
    		log.info(msgtype + result.getMtId());
    		log.info(logMsgService1 + currentCount + end);
    		return ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON).body(result.getJson());
    	} catch (Exception e) {
    		log.severe(e.getMessage());
    		log.info(logMsgService1 + currentCount + exception);
    		e.printStackTrace();
    		return ResponseEntity.status(500).contentType(MediaType.TEXT_PLAIN).body("Internal Server Error: Found input validation issues in logs.\n" + e.getMessage());
    	}
    }

//    @PostMapping(value = "/mt2xml", consumes = MediaType.TEXT_PLAIN_VALUE)
//    public ResponseEntity<String> mt2xml(@RequestBody String swiftMessage, HttpServletRequest request) {
//    	logCaptureHandler.clearLogs();
//    	int currentCount = apiMt2XmlCallCount.incrementAndGet();
//    	log.info(logMsgService1 + currentCount + start);
//    	logClientIp(request);
//
//    	try {
//    		ParsedResult result = parseAndValidate(swiftMessage);
//    		if (result.hasError()) {
//    			return ResponseEntity.status(500).contentType(MediaType.TEXT_PLAIN).body(foundErrorMsg + result.getErrorMsg());
//    		}
//    		log.info(msgtype + result.getMtId());
//    		log.info(logMsgService1 + currentCount + end);
//    		log.info(result.getJson());
//    		// Assuming `convertJsonToXml(String json)` is your logic for conversion
//    		String xmlOutput = null;
//    		System.out.println("########### TEST mt2xml 1 ########### : ");
//    		VMC_MTMXConsole console = new VMC_MTMXConsole();
////    		convertMT102JsonToPACS008Xml
////    		convertMT103JsonToPACS008Xml
////    		convertMT200JsonToPACS009Xml
////    		convertMT103RETNJsonTopacs004Xml
////    		convertMT202RETNJsonToPACS004Xml
////    		convertMT205RETNJsonToPACS004Xml
//    		if(result.getMtId()!=null && result.getMtVariant()==null) { //COV/RETN/REMIT/STP are possible getMtVariant()
//    			if(result.getMtId().equals("102")) {
//    				xmlOutput = console.convertMT102JsonToPACS008Xml(result.getJson());	// Convert JSON to XML
//    				return ResponseEntity.ok().body(xmlOutput);
//    			}
//    			if(result.getMtId().equals("103")) {
//    				xmlOutput = console.convertMT103JsonToPACS008Xml(result.getJson());	// Convert JSON to XML
//    				return ResponseEntity.ok().body(xmlOutput);
//    			}
//    			if(result.getMtId().equals("200")) {
//    				xmlOutput = console.convertMT200JsonToPACS009Xml(result.getJson());	// Convert JSON to XML
//    				return ResponseEntity.ok().body(xmlOutput);
//    			}
//    		}
//    		else if(result.getMtId()!=null && result.getMtVariant()!=null) { 
//    			if(result.getMtId().equals("103") && result.getMtVariant().equals("RETN") ) {
//    				xmlOutput = console.convertMT103RETNJsonTopacs004Xml(result.getJson());	// Convert JSON to XML
//        			return ResponseEntity.ok().body(xmlOutput);
//        		}
//    			if(result.getMtId().equals("202") && result.getMtVariant().equals("RETN") ) {
//    				xmlOutput = console.convertMT202RETNJsonToPACS004Xml(result.getJson());	// Convert JSON to XML
//        			return ResponseEntity.ok().body(xmlOutput);
//        		}
//    			if(result.getMtId().equals("205") && result.getMtVariant().equals("RETN") ) {
//    				xmlOutput = console.convertMT205RETNJsonToPACS004Xml(result.getJson());	// Convert JSON to XML
//        			return ResponseEntity.ok().body(xmlOutput);
//        		}
//    		}
//    		System.out.println("########### TEST mt2xml 4 ########### : ");
//    		return ResponseEntity.status(200).contentType(MediaType.APPLICATION_XML).body(xmlOutput);
//    	} catch (Exception e) {
//    		log.severe(e.getMessage());
//    		log.info(logMsgService1 + currentCount + exception);
//    		e.printStackTrace();
//    		return ResponseEntity.status(500).contentType(MediaType.TEXT_PLAIN).body("Internal Server Error: Found input validation issues in logs.\n" + e.getMessage());
//    	}
//    }
    
//    @PostMapping(value = "/mt2json", consumes = MediaType.TEXT_PLAIN_VALUE)
//    public ResponseEntity<String> mt2json(@RequestBody String swiftMessage, HttpServletRequest request) {
//        // Parse and convert SWIFT MT message to JSON message
//    	logCaptureHandler.clearLogs();
//    	int currentCount = apiMt2jsonCallCount.incrementAndGet();
//    	log.info(logMsgService1 + currentCount + start);
//    	logClientIp(request);
//        String jsonResponse = null;
//        try {
//			//jsonResponse = MessageToJsonExample.jsonTest(swiftMessage);
//			SwiftParser parser = null;
//			RegexValidation.validateAllBlock(swiftMessage);
//			String errorMsg = isErrorExist(parser);
//			//System.out.println("####################### TEST1:");
//			if(errorMsg!=null && !errorMsg.isEmpty())
//			{
//				return ResponseEntity.status(500).contentType(MediaType.TEXT_PLAIN).body(foundErrorMsg + errorMsg);
//	        }
//			else {
//				parser = new SwiftParser(swiftMessage);
//				//System.out.println("####################### TEST2:");
//				if(parser!=null) {
//		            SwiftMessage sm = parser.message();
//		            if(sm!=null) {
//		            	//System.out.println("####################### TEST3:");
//		            	AbstractMT amtToJson = sm.toMT();
//			            if(amtToJson!=null) {
//			            	jsonResponse = amtToJson.toJson();
//			            	if(amtToJson.getMtId()!=null) {
//			            		log.info(msgtype + amtToJson.getMtId().id());
//					        }
//			            }
//		            }
//		            //System.out.println("####################### TEST4:");
//					// Collect logs and return them in response if errors exist
//		            String errorMsg1 = isErrorExist(parser);
//					if(errorMsg1!=null && !errorMsg1.isEmpty())
//					{
//						//System.out.println("####################### TEST5:");
//						return ResponseEntity.status(500).contentType(MediaType.TEXT_PLAIN).body(foundErrorMsg + errorMsg1);
//			        }
//				}
//			}
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			log.severe(e.getMessage());
//			log.info(logMsgService1 + currentCount + exception);
//			//System.out.println("####################### TEST6:");
//			e.printStackTrace();
//			return ResponseEntity.status(500).contentType(MediaType.TEXT_PLAIN).body("Internal Server Error: Found input validation issues in logs.\n"+e.getMessage());
//		}
//		log.info(logMsgService1 + currentCount + end);
//		//System.out.println("####################### TEST7:");
//		
//		return ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON).body(jsonResponse);
//    }    
    @PostMapping(value = "/json2mt", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> json2mt(@RequestBody String jsonMessage, HttpServletRequest request) {
        // Parse and convert JSON message to SWIFT MT message 
    	logClientIp(request);
        String swiftMTResponse = null;
        int currentCount = apiJson2mtCallCount.incrementAndGet();
        log.info(logMsgService2 + currentCount + start);
		try {
			swiftMTResponse = getJson2Mt(jsonMessage);
			//System.out.println(swiftMTResponse);
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			log.severe(e.getMessage());
			log.info(logMsgService2 + currentCount + exception);
			e.printStackTrace();
		}
		log.info(logMsgService2 + currentCount + end);
		List<String> errorLogs = logCaptureHandler.getWarningAndSevereLogs();
        if (!errorLogs.isEmpty()) {
            String logMessages = String.join("\n", errorLogs);
            return ResponseEntity.status(500).body("json2mt Error : Found issues in logs.\n" + logMessages);
        }
        return ResponseEntity.ok(swiftMTResponse);
    }
    
    public String getJson2Mt(String jsonMessage) {
    	String jsonMsg = JsonFlattener.flattenJson(jsonMessage);
		AbstractMT amtFromJson = AbstractMT.fromJson(jsonMsg);
		SwiftMessage sm = amtFromJson.getSwiftMessage();
		log.info(msgtype + sm.getMtId().id());
		return sm.message();
    }
    
    @PostMapping(value = "/demo/json2mt", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> demojson2mt(@RequestBody String jsonMessage, HttpServletRequest request) {
        // Parse and convert JSON message to SWIFT MT message 
    	logClientIp(request);
        String swiftMTResponse = null;
        int currentCount = apiJson2mtCallCount.incrementAndGet();
        log.info(logMsgService2 + currentCount + start);
		try {
			swiftMTResponse = getJson2Mt(jsonMessage);
			//System.out.println(swiftMTResponse);
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			log.severe(e.getMessage());
			log.info(logMsgService2 + currentCount + exception);
			e.printStackTrace();
		}
		log.info(logMsgService2 + currentCount + end);
		List<String> errorLogs = logCaptureHandler.getWarningAndSevereLogs();
        if (!errorLogs.isEmpty()) {
            String logMessages = String.join("\n", errorLogs);
            return ResponseEntity.status(500).body("json2mt Error : Found issues in logs.\n" + logMessages);
        }
        return ResponseEntity.ok(swiftMTResponse);
    }
    
    @PostMapping(value = "/mt2json2mt", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> mt2json2mt(@RequestBody String swiftMessage) {
        // Parse and convert SWIFT MT message to JSON message
        String swiftMTResponse = null;
        int currentCount = apiMt2json2mtCallCount.incrementAndGet();
        log.info(logMsgService3 + currentCount + start);
		try {
			//jsonResponse = MessageToJsonExample.jsonTest(swiftMessage);
			SwiftParser parser = new SwiftParser(swiftMessage);
            SwiftMessage sm = parser.message();
            log.info(msgtype + sm.getMtId().id());
            AbstractMT amtToJson = sm.toMT();
            String hierarchyJson = amtToJson.toJson();
            String flattenJson = JsonFlattener.flattenJson(hierarchyJson);
			AbstractMT amtFromJson = AbstractMT.fromJson(flattenJson);
			swiftMTResponse = amtFromJson.getSwiftMessage().message();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.severe(e.getMessage());
			log.info(logMsgService3 + currentCount + exception);
			e.printStackTrace();
		}
		log.info(logMsgService3 + currentCount + end);
        return ResponseEntity.ok(swiftMTResponse);
    }
    public String isErrorExist(SwiftParser parser) {
    	String errorMsgs = null;
    	List<String> warnSevereLogs = logCaptureHandler.getWarningAndSevereLogs();
		List<String> errorList = new ArrayList<String>();
        if (parser!=null && !parser.getErrors().isEmpty()) {
        	errorList.addAll(parser.getErrors());
        }
        if (!warnSevereLogs.isEmpty()) {
        	errorList.addAll(warnSevereLogs);
        }
        if (!errorList.isEmpty()) {
        	int i = 0;
        	errorMsgs = new String();
        	for(String error: errorList) {
        		i++;
        		errorMsgs = errorMsgs + "\n \005 ERROR NO-" + i +" : "+error;
        	}
        }
		return errorMsgs;
    }
    
//    @PostMapping(value = "/json2xml"  ,consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
//    public ResponseEntity<String> convertJsonToXml(@RequestBody String jsonMessage) {
//    	try {
//            ObjectMapper mapper = new ObjectMapper();
//            JsonNode rootNode = mapper.readTree(jsonMessage);
//
//            // Accessing applicationHeaderBlock.messageType
//            String messageType = rootNode
//                    .path("applicationHeaderBlock")
//                    .path("messageType")
//                    .asText();
//
//            // Accessing userHeaderBlock.userHeaderBlockFields.field119.validationFlag
//            String validationFlag = rootNode
//                    .path("userHeaderBlock")
//                    .path("userHeaderBlockFields")
//                    .path("field119")
//                    .path("validationFlag")
//                    .asText();
//
//            System.out.println("Message Type: " + messageType);
//            System.out.println("Validation Flag: " + validationFlag);
//            String xmlOutput = null;
//            VMC_MTMXConsole console = new VMC_MTMXConsole();
//            if(messageType != null && validationFlag == null) { //COV/RETN/REMIT/STP are possible getMtVariant()
//    			if(messageType.equals("102")) {
//    				xmlOutput = console.convertMT102JsonToPACS008Xml(jsonMessage);	// Convert JSON to XML
//    				return ResponseEntity.ok().body(xmlOutput);
//    			}
//    			if(messageType.equals("103")) {
//    				xmlOutput = console.convertMT103JsonToPACS008Xml(jsonMessage);	// Convert JSON to XML
//    				return ResponseEntity.ok().body(xmlOutput);
//    			}
//    			if(messageType.equals("200")) {
//    				xmlOutput = console.convertMT200JsonToPACS009Xml(jsonMessage);	// Convert JSON to XML
//    				return ResponseEntity.ok().body(xmlOutput);
//    			}
//    		}
//    		else if(messageType!=null && validationFlag!=null) { 
//    			if(messageType.equals("103") && validationFlag.equals("RETN") ) {
//    				xmlOutput = console.convertMT103RETNJsonTopacs004Xml(jsonMessage);	// Convert JSON to XML
//        			return ResponseEntity.ok().body(xmlOutput);
//        		}
//    			if(messageType.equals("202") && validationFlag.equals("RETN") ) {
//    				xmlOutput = console.convertMT202RETNJsonToPACS004Xml(jsonMessage);	// Convert JSON to XML
//        			return ResponseEntity.ok().body(xmlOutput);
//        		}
//    			if(messageType.equals("205") && validationFlag.equals("RETN") ) {
//    				xmlOutput = console.convertMT205RETNJsonToPACS004Xml(jsonMessage);	// Convert JSON to XML
//        			return ResponseEntity.ok().body(xmlOutput);
//        		}
//    		}
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        try {
//        	VMC_MTMXConsole console = new VMC_MTMXConsole();
//            // Convert JSON to XML
//            String xmlOutput = console.convertMT102JsonToPACS008Xml( jsonMessage);
//            
//            // Log the output for debugging
//            System.out.println("Generated XML Output: " + xmlOutput);
//
//            // Return XML with the correct Content-Type header
//            return ResponseEntity.ok().body(xmlOutput);
//        } catch (Exception e) {
//            // Log and return error message
//            return ResponseEntity.status(500).body("Error converting JSON to XML: " + e.getMessage());
//        }
//    }
    
    @PostMapping(value = "/json2mx", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> convertJsonToXml(@RequestBody String jsonMessage) {
        try {

            String messageType = getMessageType(jsonMessage);
            String validationFlag = getMessageVariant(jsonMessage);
            
            String xmlOutput = convertJsonToXml(jsonMessage, messageType, validationFlag);
            return ResponseEntity.ok().body(xmlOutput);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error converting JSON to XML: " + e.getMessage());
        }
    }
    
    @PostMapping(value = "/demo/json2mx", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> convertDemoJsonToXml(@RequestBody String jsonMessage) {
        try {

            String messageType = getMessageType(jsonMessage);
            String validationFlag = getMessageVariant(jsonMessage);
            
            String xmlOutput = convertDemoJsonToXml(jsonMessage, messageType, validationFlag);
            return ResponseEntity.ok().body(xmlOutput);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error converting JSON to XML: " + e.getMessage());
        }
    }
    
    @PostMapping(value = "/demo/mx2json", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> convertDemoMxToJson(@RequestBody String xmlMessage) {
        try {
        	String response = convertDemoXmlToJson(xmlMessage);
            //JsonObject jsonObject = new JsonObject();
            //jsonObject.addProperty("value", messageType);
            //System.out.println(messageType);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error converting XML to JSON: " + e.getMessage());
        }
    }
    
    @PostMapping(value = "/demo/mx2mt", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> convertDemoXmlToMt(@RequestBody String xmlMessage) {
        try {
        	String jsonResponse = convertDemoXmlToJson(xmlMessage);
        	String mtResponse = getJson2Mt(jsonResponse);
        	return ResponseEntity.ok().body(mtResponse);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error converting XML to MT: " + e.getMessage());
        }
    }
    
    @PostMapping(value = "/mx2mt", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> convertXmlToMt(@RequestBody String xmlMessage) {
        try {
        	String jsonResponse = convertXmlToJson(xmlMessage);
        	String mtResponse = getJson2Mt(jsonResponse);
        	return ResponseEntity.ok().body(mtResponse);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error converting XML to MT: " + e.getMessage());
        }
    }
    //convertXmlToJson
    private String convertXmlToJson(String xmlMessage) throws Exception {
    	return null;
    }
    private String convertDemoXmlToJson(String xmlMessage) throws Exception {
    	
    	String msgDefIdr = getMsgDefIdr(xmlMessage);
        String[] msgTypes = msgDefIdr.split("-");
        String mxType = null;
        String mtType = null;
        String mtSubType = null;
        if(msgTypes.length==3) {
        	mxType = msgTypes[0];
            mtType = msgTypes[1];
            mtSubType = msgTypes[2];
        }
    	//VMC_DemoConsole console = new VMC_DemoConsole();
    	StringInput input = new StringInput(xmlMessage);
        StringOutput output = new StringOutput();
        if (mtType != null && mtSubType.equals("CORE")) {
            switch (mtType) {
                case "103": return convertDemoPACS008XmlToMT103Json(input, output, new MappingMapToMT103_pacs008());
                case "192": return convertDemoCAMT056XmlToMT192Json(input, output, new MappingMapToMT192_camt056());
                case "202": return convertDemoPACS009XmlToMT202Json(input, output, new MappingMapToMT202_pacs009());
            }
        } else if (mtType != null && mtSubType != null && mtSubType.equals("RETN")) {
            switch (mtType) {
	            //case "202": return console.convertMT103RETNJsonTopacs004Xml(jsonMessage);
            }
        } else if (mtType != null && mtSubType != null && mtSubType.equals("COV")) {
            switch (mtType) {
	            case "202": return convertDemoPACS009XmlToMT202COVJson(input, output, new MappingMapToMT202COV_pacs009());
            }
        }
        // Fallback or unknown type
        return null;  // default fallback
    }

    @PostMapping(value = "/mt2mx", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> mt2xml(@RequestBody String swiftMessage, HttpServletRequest request) {
        logCaptureHandler.clearLogs();
        int currentCount = apiMt2XmlCallCount.incrementAndGet();
        log.info(logMsgService1 + currentCount + start);
        logClientIp(request);

        try {
            ParsedResult result = parseAndValidate(swiftMessage);
            if (result.hasError()) {
                return ResponseEntity.status(500).contentType(MediaType.TEXT_PLAIN).body(foundErrorMsg + result.getErrorMsg());
            }

            log.info(msgtype + result.getMtId());
            log.info(logMsgService1 + currentCount + end);
            log.info(result.getJson());

            String xmlOutput = convertJsonToXml(result.getJson(), result.getMtId(), result.getMtVariant());
            System.out.println("############# result.getMtId() ############# : "+ result.getMtId());
            System.out.println("############# result.getMtVariant() ############# : "+ result.getMtVariant());
            System.out.println("############# xmlOutput ############# : "+ xmlOutput);
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(xmlOutput);
        } catch (Exception e) {
            log.severe(e.getMessage());
            log.info(logMsgService1 + currentCount + exception);
            e.printStackTrace();
            return ResponseEntity.status(500).contentType(MediaType.TEXT_PLAIN).body("Internal Server Error: " + e.getMessage());
        }
    }
    
    @PostMapping(value = "/demo/mt2mx", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> demomt2xml(@RequestBody String swiftMessage, HttpServletRequest request) {
        logCaptureHandler.clearLogs();
        int currentCount = apiMt2XmlCallCount.incrementAndGet();
        log.info(logMsgService1 + currentCount + start);
        logClientIp(request);

        try {
            ParsedResult result = parseAndValidate(swiftMessage);
            if (result.hasError()) {
                return ResponseEntity.status(500).contentType(MediaType.TEXT_PLAIN).body(foundErrorMsg + result.getErrorMsg());
            }

            log.info(msgtype + result.getMtId());
            log.info(logMsgService1 + currentCount + end);
            log.info(result.getJson());

            String xmlOutput = convertDemoJsonToXml(result.getJson(), result.getMtId(), result.getMtVariant());
            System.out.println("############# result.getMtId() ############# : "+ result.getMtId());
            System.out.println("############# result.getMtVariant() ############# : "+ result.getMtVariant());
            System.out.println("############# xmlOutput ############# : "+ xmlOutput);
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(xmlOutput);
        } catch (Exception e) {
            log.severe(e.getMessage());
            log.info(logMsgService1 + currentCount + exception);
            e.printStackTrace();
            return ResponseEntity.status(500).contentType(MediaType.TEXT_PLAIN).body("Internal Server Error: " + e.getMessage());
        }
    }
    
//    private String convertJsonToXml(String jsonMessage, String messageType, String validationFlag) throws Exception {
//        VMC_MTMXConsole console = new VMC_MTMXConsole();
//
//        if (messageType != null && validationFlag == null) {
//            switch (messageType) {
//                //case "102": return console.convertMT102JsonToPACS008Xml(jsonMessage);
//                case "103": return console.convertMT103JsonToPACS008Xml(jsonMessage);
//                case "103": return convertMT103JsonToPACS008Xml(input, output, new MappingMapTopacs_008_001_09_msg());
//                case "202": return console.convertMT202JsonToPACS009Xml(jsonMessage);
//                case "202": return convertMT103JsonToPACS008Xml(input, output, new MappingMapTopacs_008_001_09_msg());
//                //case "200": return console.convertMT200JsonToPACS009Xml(jsonMessage);
//            }
//        } else if (messageType != null && validationFlag != null && validationFlag.equals("COV")) {
//            switch (messageType) {
//	            //case "103": return console.convertMT103RETNJsonTopacs004Xml(jsonMessage);
//	            case "202": return console.convertMT202COVJsonToPACS009Xml(jsonMessage);
//	            //case "205": return console.convertMT205RETNJsonToPACS004Xml(jsonMessage);
//            }
//        }
//        // Fallback or unknown type
//        return null;  // default fallback
//    }
    
    //REAL CALL
    private String convertJsonToXml(String jsonMessage, String messageType, String validationFlag) throws Exception {
    	VMC_DemoConsole console = new VMC_DemoConsole();
    	StringInput input = new StringInput(jsonMessage);
        StringOutput output = new StringOutput();
        if (messageType != null && validationFlag == null) {
            switch (messageType) {
                case "103": return convertMT103JsonToPACS008Xml(input, output, new MappingMapToMT103_REMIT_STP_pacs008());
                case "202": return convertMT202JsonToPACS009Xml(input, output, new MappingMapToissettled_apphdr_pacs0092());
            }
        } else if (messageType != null && validationFlag != null && validationFlag.equals("RETN")) {
            switch (messageType) {
	            //case "202": return console.convertMT103RETNJsonTopacs004Xml(jsonMessage);
            }
        } else if (messageType != null && validationFlag != null && validationFlag.equals("COV")) {
            switch (messageType) {
	            case "202": return convertMT202COVJsonToPACS009Xml(input, output, new MappingMapToissettled_apphdr_pacs009());
            }
        }
        // Fallback or unknown type
        return null;  // default fallback
    }
    
    //DEMO CALL
    private String convertDemoJsonToXml(String jsonMessage, String messageType, String validationFlag) throws Exception {
    	VMC_DemoConsole console = new VMC_DemoConsole();
    	StringInput input = new StringInput(jsonMessage);
        StringOutput output = new StringOutput();
        if (messageType != null && validationFlag == null) {
            switch (messageType) {
                case "103": return convertDemoMT103JsonToPACS008Xml(input, output, new MappingMapTopacs_008_001_09_msg());
                case "192": return convertDemoMT192JsonToCAMT056Xml(input, output, new MappingMapTocamt_056_001_10_msg());
                case "202": return convertDemoMT202JsonToPACS009Xml(input, output, new MappingMapTopacs_009_001_09_msg());
            }
        } else if (messageType != null && validationFlag != null && validationFlag.equals("RETN")) {
            switch (messageType) {
	            //case "202": return console.convertMT103RETNJsonTopacs004Xml(jsonMessage);
            }
        } else if (messageType != null && validationFlag != null && validationFlag.equals("COV")) {
            switch (messageType) {
	            case "202": return convertDemoMT202COVJsonToPACS009Xml(input, output, new MappingMapTopacs_009_001_09_msg2());
            }
        }
        // Fallback or unknown type
        return null;  // default fallback
    }
    
    //REAL MT2MX Altova Mapforce call REAL
    private String convertMT103JsonToPACS008Xml(StringInput input,StringOutput output, MappingMapToMT103_REMIT_STP_pacs008 obj) throws Exception {
    	try {
    		obj.run(input,output);
			return output.getString() != null ? output.getString().toString() : null;
		} finally {
			input.close();
			output.close();
		}
    }
    
    private String convertMT202JsonToPACS009Xml(StringInput input,StringOutput output, MappingMapToissettled_apphdr_pacs0092 obj) throws Exception {
    	try {
    		obj.run(input,output);
			return output.getString() != null ? output.getString().toString() : null;
		} finally {
			input.close();
			output.close();
		}
    }
    
    private String convertMT202COVJsonToPACS009Xml(StringInput input,StringOutput output, MappingMapToissettled_apphdr_pacs009 obj) throws Exception {
    	try {
    		obj.run(input,output);
			return output.getString() != null ? output.getString().toString() : null;
		} finally {
			input.close();
			output.close();
		}
    }
    
    //Demo MT2MX Altova Mapforce call DEMO
    private String convertDemoMT103JsonToPACS008Xml(StringInput input,StringOutput output, MappingMapTopacs_008_001_09_msg obj) throws Exception {
    	try {
    		obj.run(input,output);
			return output.getString() != null ? output.getString().toString() : null;
		} finally {
			input.close();
			output.close();
		}
    }
    
    private String convertDemoMT192JsonToCAMT056Xml(StringInput input,StringOutput output, MappingMapTocamt_056_001_10_msg obj) throws Exception {
    	try {
    		obj.run(input,output);
			return output.getString() != null ? output.getString().toString() : null;
		} finally {
			input.close();
			output.close();
		}
    }
    
    private String convertDemoMT202JsonToPACS009Xml(StringInput input,StringOutput output, MappingMapTopacs_009_001_09_msg obj) throws Exception {
    	try {
    		obj.run(input,output);
			return output.getString() != null ? output.getString().toString() : null;
		} finally {
			input.close();
			output.close();
		}
    }
    
    private String convertDemoMT202COVJsonToPACS009Xml(StringInput input,StringOutput output, MappingMapTopacs_009_001_09_msg2 obj) throws Exception {
    	try {
    		obj.run(input,output);
			return output.getString() != null ? output.getString().toString() : null;
		} finally {
			input.close();
			output.close();
		}
    }
    
    //Demo MX2MT Altova Mapforce call DEMO
    private String convertDemoPACS008XmlToMT103Json(StringInput input,StringOutput output, MappingMapToMT103_pacs008 obj) throws Exception {
    	try {
    		obj.run(input,output);
			return output.getString() != null ? output.getString().toString() : null;
		} finally {
			input.close();
			output.close();
		}
    }
    
    private String convertDemoCAMT056XmlToMT192Json(StringInput input,StringOutput output, MappingMapToMT192_camt056 obj) throws Exception {
    	try {
    		obj.run(input,output);
			return output.getString() != null ? output.getString().toString() : null;
		} finally {
			input.close();
			output.close();
		}
    }
    
    private String convertDemoPACS009XmlToMT202Json(StringInput input,StringOutput output, MappingMapToMT202_pacs009 obj) throws Exception {
    	try {
    		obj.run(input,output);
			return output.getString() != null ? output.getString().toString() : null;
		} finally {
			input.close();
			output.close();
		}
    }
    
    private String convertDemoPACS009XmlToMT202COVJson(StringInput input,StringOutput output, MappingMapToMT202COV_pacs009 obj) throws Exception {
    	try {
    		obj.run(input,output);
			return output.getString() != null ? output.getString().toString() : null;
		} finally {
			input.close();
			output.close();
		}
    }
    
    private ParsedResult parseAndValidate(String swiftMessage) throws Exception {
    	RegexValidation.validateAllBlock(swiftMessage);

    	SwiftParser parser = new SwiftParser(swiftMessage);
    	String errorMsg = isErrorExist(parser);
    	if (errorMsg != null && !errorMsg.isEmpty()) {
    		return new ParsedResult(errorMsg);
    	}

    	SwiftMessage sm = parser.message();
    	if (sm == null) return new ParsedResult("SWIFT message parsing returned null.");

    	AbstractMT mt = sm.toMT();
    	if (mt == null) return new ParsedResult("Unable to convert message to MT format.");

    	String jsonMessage = mt.toJson();
    	String messageType = getMessageType(jsonMessage);
        String validationFlag = getMessageVariant(jsonMessage);
    	
    	// Double-check error after conversion
    	String errorMsg2 = isErrorExist(parser);
    	if (errorMsg2 != null && !errorMsg2.isEmpty()) {
    		return new ParsedResult(errorMsg2);
    	}

    	return new ParsedResult(jsonMessage, null, messageType, validationFlag);
    }    
    
    private String getMsgDefIdr(String xmlMessage) throws Exception {
    	System.out.println("MsgDefIdr Method Called");
    	//This method will get the Pacs Type from XML message.
    	String xmlType = null;
    	
    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true); // important for h: prefix
        DocumentBuilder builder = factory.newDocumentBuilder();

        // Parse directly from string
        Document doc = builder.parse(new InputSource(new StringReader(xmlMessage)));

        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xpath = xPathFactory.newXPath();

        // Ignore namespace by using local-name()
        XPathExpression expr = xpath.compile("//*[local-name()='MsgDefIdr']");
        Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);

        if (node != null) {
            System.out.println("MsgDefIdr = " + node.getTextContent());
            xmlType = node.getTextContent();
        } else {
            System.out.println("MsgDefIdr not found");
        }
    	
    	
    	return xmlType;
    }
    
    private String getMessageType(String jsonMessage) throws Exception {
    	String messageType = null;
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(jsonMessage);
    	 // Safely get applicationHeaderBlock.messageType
        JsonNode appHeaderBlock = rootNode.path("applicationHeaderBlock");
        if (appHeaderBlock != null && !appHeaderBlock.isMissingNode()) {
            JsonNode msgTypeNode = appHeaderBlock.path("messageType");
            if (msgTypeNode != null && !msgTypeNode.isMissingNode() && !msgTypeNode.isNull()) {
                messageType = msgTypeNode.asText();
            }
        }
    	return messageType;
    }
    
    private String getMessageVariant(String jsonMessage) throws Exception {
    	String validationFlag = null;
    	ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(jsonMessage);
    	// Safely get userHeaderBlock.userHeaderBlockFields.field119.validationFlag
        JsonNode userHeaderBlock = rootNode.path("userHeaderBlock");
        if (userHeaderBlock != null && !userHeaderBlock.isMissingNode()) {
            JsonNode userFields = userHeaderBlock.path("userHeaderBlockFields");
            if (userFields != null && !userFields.isMissingNode()) {
                JsonNode field119 = userFields.path("field119");
                if (field119 != null && !field119.isMissingNode()) {
                    JsonNode validationFlagNode = field119.path("validationFlag");
                    if (validationFlagNode != null && !validationFlagNode.isMissingNode() && !validationFlagNode.isNull()) {
                        validationFlag = validationFlagNode.asText();
                    }
                }
            }
        }
    	return validationFlag;
    }
    
    @PostMapping("/compare")
    public String compareXmls(@RequestParam("file1") MultipartFile file1,
                              @RequestParam("file2") MultipartFile file2,
                              Model model,
                              HttpSession session) throws Exception {
    	System.out.println("Inside compareXmls ##############");
        Map<String, String> xmlMap1 = XmlDiffService.parseXML(file1.getInputStream());
        Map<String, String> xmlMap2 = XmlDiffService.parseXML(file2.getInputStream());

        List<XmlDiff> diffs = XmlDiffService.compare(xmlMap1, xmlMap2);
        int total = diffs.size();
        System.out.println("############## total: "+total);
        long validCnt = diffs.stream().filter(XmlDiff::isMatch).count();
        System.out.println("############## validCnt: "+validCnt);
        int passPercent = Math.round(((float) validCnt / total) * 100);
        System.out.println("############## passPercent: "+passPercent);
        int errorCount = total - (int) validCnt;
        System.out.println("############## errorCount: "+errorCount);

        // store in session so we can reuse later
        session.setAttribute("diffs", diffs);
        session.setAttribute("file1Name", file1.getOriginalFilename());
        session.setAttribute("file2Name", file2.getOriginalFilename());
        session.setAttribute("errorCount", errorCount);
        session.setAttribute("passPercent", passPercent);
        
        model.addAttribute("diffs", diffs);
        model.addAttribute("file1Name", file1.getOriginalFilename());
        model.addAttribute("file2Name", file2.getOriginalFilename());
        model.addAttribute("errorCount", errorCount);
        model.addAttribute("passPercent", passPercent);
        return "result"; // Thymeleaf template // Looks for result.html inside /templates
    }
    
    //http://localhost:8080/swift/run-tests
    @PostMapping("/run-tests")
    public String runTests(@RequestParam String folderPath, Model model) throws Exception {
        File folder = new File(folderPath);
        File[] files = folder.listFiles((dir, name) -> name.endsWith("_TXT.txt"));

        List<TestResult> results = new ArrayList<>();

        if (files != null) {
            for (File txtFile : files) {
                String baseName = txtFile.getName().replace("_TXT.txt", "");

                // 1. Call mt2json
                String jsonResponse = restTemplate.postForObject("http://localhost:8080/swift/mt2json",
                                                         new FileSystemResource(txtFile),
                                                         String.class);
                System.out.println("########## json #################################################### : "+jsonResponse);
                org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<String> request = new HttpEntity<>(jsonResponse, headers);

                // 2. Call json2mx
                String actualXml = restTemplate.postForObject("http://localhost:8080/swift/json2mx",
                											  request,
                                                              String.class);
                System.out.println("########### actualXml ################################################### : "+actualXml);   
                // Save ACTUAL xml
                File actualFile = new File(folder, baseName + "_ACTUAL_XML.xml");
                Files.writeString(actualFile.toPath(), actualXml);

                // 3. Compare with EXPECTED
                File expectedFile = new File(folder, baseName + "_EXPECTED_XML.xml");
                System.out.println("########### EXPECTED_XML ################################################### : "+baseName+"_EXPECTED_XML.xml");
                Map<String, String> xmlMap1 = XmlDiffService.parseXML(new FileInputStream(actualFile));
                Map<String, String> xmlMap2 = XmlDiffService.parseXML(new FileInputStream(expectedFile));
                List<XmlDiff> diffs = XmlDiffService.compare(xmlMap1, xmlMap2);

                int total = diffs.size();
                long validCnt = diffs.stream().filter(XmlDiff::isMatch).count();
                int passPercent = Math.round(((float) validCnt / total) * 100);
                int errorCount = total - (int) validCnt;
                
                // Build PDF filename
                String pdfName = actualFile.getName().replace("_ACTUAL_XML.xml", "_REPORT.pdf");
                File pdfFile = new File(folder, pdfName);

                try (OutputStream out = new FileOutputStream(pdfFile)) {
                    generatePdfReport(out, diffs, actualFile.getName(), expectedFile.getName(), passPercent, errorCount);
                }

                TestResult tr = new TestResult(baseName, diffs, passPercent, errorCount);
                results.add(tr);
            }
        }

        model.addAttribute("results", results);
        return "multi-result";  // new Thymeleaf page
    }

    
//    @GetMapping("/download-pdf")
//    public void downloadPdf(HttpServletResponse response, HttpSession session) throws Exception {
//        @SuppressWarnings("unchecked")
//        List<XmlDiff> diffs = (List<XmlDiff>) session.getAttribute("diffs");
//        String file1Name = (String) session.getAttribute("file1Name");
//        String file2Name = (String) session.getAttribute("file2Name");
//        String nameWithoutExt = file2Name.replaceFirst("[.][^.]+$", "");
//        Integer errorCount = (Integer) session.getAttribute("errorCount");
//        Integer passPercent = (Integer) session.getAttribute("passPercent");
//        
//        //System.out.println("Test 1");   
//        if (diffs == null) {
//            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No comparison data found. Please run comparison first.");
//            return;
//        }
//        //System.out.println("Test 2"); 
//        response.setContentType("application/pdf");
//        response.setHeader("Content-Disposition", "attachment; filename="+nameWithoutExt+".pdf");
//        //System.out.println("Test 3");
//        try (OutputStream out = response.getOutputStream()) {
//            com.itextpdf.text.Document document = new com.itextpdf.text.Document();
//            PdfWriter.getInstance(document, out);
//            document.open();
//            
//            //System.out.println("Test 4");
//            document.add(new Paragraph("XML Comparison Report"));
//            document.add(new Paragraph("Generated on: " + new java.util.Date()));
//            document.add(new Paragraph("Pass Percentage: "+passPercent));
//            document.add(new Paragraph("Error Count: "+ errorCount));
//            document.add(new Paragraph(" "));
//
//            //System.out.println("Test 5");
//            PdfPTable table = new PdfPTable(3);
//            table.setWidthPercentage(100);
//            table.addCell("XPath");
//            table.addCell(file1Name);
//            table.addCell(file2Name);
//
//            //System.out.println("Test 6");
//            for (XmlDiff diff : diffs) {
//                PdfPCell xpathCell = new PdfPCell(new Phrase(diff.getXpath()));
//                PdfPCell xml1Cell = new PdfPCell(new Phrase(diff.getXml1Value()));
//                PdfPCell xml2Cell = new PdfPCell(new Phrase(diff.getXml2Value()));
//
//                //System.out.println("Test 7");
//                if (diff.isMatch()) {
//                	// Softer green
//                	BaseColor softGreen = new BaseColor(144, 238, 144); // light green (like #90EE90)
//                    xpathCell.setBackgroundColor(softGreen);
//                    xml1Cell.setBackgroundColor(softGreen);
//                    xml2Cell.setBackgroundColor(softGreen);
//                } else {
//                	// Softer red (pinkish)
//                	BaseColor softRed = new BaseColor(255, 182, 193); // light pink (#FFB6C1)
//                    xpathCell.setBackgroundColor(softRed);
//                    xml1Cell.setBackgroundColor(softRed);
//                    xml2Cell.setBackgroundColor(softRed);
//                }
//
//                //System.out.println("Test 8");
//                table.addCell(xpathCell);
//                table.addCell(xml1Cell);
//                table.addCell(xml2Cell);
//            }
//            //System.out.println("Test 9");
//            document.add(table);
//            document.close();
//            //System.out.println("Test 10");
//        }
//    }
    
    @GetMapping("/download-pdf")
    public void downloadPdf(HttpServletResponse response, HttpSession session) throws Exception {
        @SuppressWarnings("unchecked")
        List<XmlDiff> diffs = (List<XmlDiff>) session.getAttribute("diffs");
        String file1Name = (String) session.getAttribute("file1Name");
        String file2Name = (String) session.getAttribute("file2Name");
        String nameWithoutExt = file2Name.replaceFirst("[.][^.]+$", "");
        Integer errorCount = (Integer) session.getAttribute("errorCount");
        Integer passPercent = (Integer) session.getAttribute("passPercent");

        if (diffs == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No comparison data found. Please run comparison first.");
            return;
        }

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=" + nameWithoutExt + ".pdf");

        try (OutputStream out = response.getOutputStream()) {
            generatePdfReport(out, diffs, file1Name, file2Name, passPercent, errorCount);
        }
    }

    
    private void generatePdfReport(
            OutputStream out,
            List<XmlDiff> diffs,
            String file1Name,
            String file2Name,
            Integer passPercent,
            Integer errorCount
    ) throws Exception {
        com.itextpdf.text.Document document = new com.itextpdf.text.Document();
        PdfWriter.getInstance(document, out);
        document.open();

        // Header info
        document.add(new Paragraph("XML Comparison Report"));
        document.add(new Paragraph("Generated on: " + new java.util.Date()));
        document.add(new Paragraph("Pass Percentage: " + passPercent));
        document.add(new Paragraph("Error Count: " + errorCount));
        document.add(new Paragraph(" "));

        // Table
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.addCell("XPath");
        table.addCell(file1Name);
        table.addCell(file2Name);

        for (XmlDiff diff : diffs) {
            PdfPCell xpathCell = new PdfPCell(new Phrase(diff.getXpath()));
            PdfPCell xml1Cell = new PdfPCell(new Phrase(diff.getXml1Value()));
            PdfPCell xml2Cell = new PdfPCell(new Phrase(diff.getXml2Value()));

            if (diff.isMatch()) {
                // Softer green
                BaseColor softGreen = new BaseColor(144, 238, 144); // light green
                xpathCell.setBackgroundColor(softGreen);
                xml1Cell.setBackgroundColor(softGreen);
                xml2Cell.setBackgroundColor(softGreen);
            } else {
                // Softer red
                BaseColor softRed = new BaseColor(255, 182, 193); // light pink
                xpathCell.setBackgroundColor(softRed);
                xml1Cell.setBackgroundColor(softRed);
                xml2Cell.setBackgroundColor(softRed);
            }

            table.addCell(xpathCell);
            table.addCell(xml1Cell);
            table.addCell(xml2Cell);
        }

        document.add(table);
        document.close();
    }


    
//    @GetMapping("/swift/download-pdf")
//    public void downloadPdf(HttpServletResponse response) throws Exception {
//        response.setContentType("application/pdf");
//        response.setHeader("Content-Disposition", "attachment; filename=XML_Comparison.pdf");
//
//        try (OutputStream out = response.getOutputStream()) {
//            com.itextpdf.text.Document document = new com.itextpdf.text.Document();
//            PdfWriter.getInstance(document, out);
//            document.open();
//
//            document.add(new Paragraph("XML Comparison Report"));
//            document.add(new Paragraph("Generated on: " + new java.util.Date()));
//            document.add(new Paragraph(" "));
//
//            // Table with 3 columns
//            PdfPTable table = new PdfPTable(3);
//            table.setWidthPercentage(100);
//            table.addCell("XPath");
//            table.addCell("XML1 Value");
//            table.addCell("XML2 Value");
//
//            for (XmlDiff diff : diffs) {  // <-- make sure you pass your diff list here
//                PdfPCell xpathCell = new PdfPCell(new Phrase(diff.getXpath()));
//                PdfPCell xml1Cell = new PdfPCell(new Phrase(diff.getXml1Value()));
//                PdfPCell xml2Cell = new PdfPCell(new Phrase(diff.getXml2Value()));
//
//                if (diff.isMatch()) {
//                    xpathCell.setBackgroundColor(BaseColor.GREEN);
//                    xml1Cell.setBackgroundColor(BaseColor.GREEN);
//                    xml2Cell.setBackgroundColor(BaseColor.GREEN);
//                } else {
//                    xpathCell.setBackgroundColor(BaseColor.RED);
//                    xml1Cell.setBackgroundColor(BaseColor.RED);
//                    xml2Cell.setBackgroundColor(BaseColor.RED);
//                }
//
//                table.addCell(xpathCell);
//                table.addCell(xml1Cell);
//                table.addCell(xml2Cell);
//            }
//
//            document.add(table);
//            document.close();
//        }
//    }
    
}
   

 



