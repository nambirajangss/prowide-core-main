package com.gss.vmc.mxmt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.gss.vmc.jaxb.pacs00800109.FIToFICustomerCreditTransferV09;
import com.gss.vmc.jaxb.pacs00800109.Message;
import com.gss.vmc.jaxb.pacs00900109.FinancialInstitutionCreditTransferV09;
import com.gss.vmc.mxmt.rule.integrator.RuleEnginePacs008;
import jakarta.xml.bind.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Main class to orchestrate the MX to MT conversion process.
 * This class ties together validation, parsing, rule application, formatting, and JSON building.
 */
public class MxToMtConverter {

    private final XmlValidator xmlValidator;
    private final MxParser mxParser;
    private final RuleEnginePacs008 ruleEnginePacs008;
    private final FieldFormatter fieldFormatter;
    private final JsonBuilder jsonBuilder;
    String pacs008xsd = "D:\\Project\\VMC\\src\\main\\java\\com\\prowidesoftware\\controller\\pacs.008.001.09.xsd";
    String pacs009xsd = "D:\\Project\\VMC\\src\\main\\java\\com\\prowidesoftware\\controller\\pacs.009.001.09.xsd";
    
    public MxToMtConverter() {
        this.xmlValidator = new XmlValidator();
        this.mxParser = new MxParser();
        this.fieldFormatter = new FieldFormatter();
        this.ruleEnginePacs008 = new RuleEnginePacs008(fieldFormatter);
        this.jsonBuilder = new JsonBuilder();
    }
    
    public static Object unmarshalXML(String xmlFilePath, JAXBContext ctx, String xsdPath) throws Exception {
        File xmlFile = new File(xmlFilePath);

        // Create unmarshaller
        Unmarshaller unmarshaller = ctx.createUnmarshaller();

        // Load and set schema for validation
        SchemaFactory sf = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = sf.newSchema(new File(xsdPath));
        unmarshaller.setSchema(schema);

        // Optional: custom validation handler
        unmarshaller.setEventHandler(new ValidationEventHandler() {
			@Override
			public boolean handleEvent(ValidationEvent event) {
				System.out.println("Validation Error: " + event.getMessage());
                return false; // stop on first error
			}
        });

        @SuppressWarnings("unchecked")
        JAXBElement<Object> jaxbElement = (JAXBElement<Object>) unmarshaller.unmarshal(xmlFile);

        return jaxbElement.getValue();
    }

    
    public static Object unmarshalXML(String xmlFilePath,JAXBContext ctx) throws Exception {
    	File xmlFile = new File(xmlFilePath);        
    	//JAXBContext ctx = JAXBContext.newInstance("com.gss.vmc.jaxb.pacs00800109");
        //JAXBContext ctx = JAXBContext.newInstance(Message.class);
        Unmarshaller unmarshaller = ctx.createUnmarshaller();
        @SuppressWarnings("unchecked")
		JAXBElement<Object> jaxbElement = (JAXBElement<Object>) unmarshaller.unmarshal(xmlFile);
        return jaxbElement.getValue();
    }
    
    public static String identifyMessageType(String xmlFilePath) throws Exception {
        // Parse XML to check document element
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc = dbf.newDocumentBuilder().parse(new File(xmlFilePath));
        String docElementName = doc.getDocumentElement().getLocalName();
        String docNamespace = doc.getDocumentElement().getNamespaceURI();
        //System.out.println("docElementName: "+docElementName);
        //System.out.println("docNamespace: "+docNamespace);
        if ("urn:iso:std:iso:20022:tech:xsd:pacs.008.001.09".equals(docNamespace) && "Message".equals(docElementName)) {
            // Check child element name
            org.w3c.dom.NodeList nodes = doc.getElementsByTagNameNS("urn:iso:std:iso:20022:tech:xsd:pacs.008.001.09", "*");
            for (int i = 0; i < nodes.getLength(); i++) {
                String nodeName = nodes.item(i).getLocalName();
                if ("FIToFICstmrCdtTrf".equals(nodeName)) {
                    return "pacs.008.001.09";
				}
            }
        } else if ("urn:iso:std:iso:20022:tech:xsd:pacs.009.001.09".equals(docNamespace) && "Message".equals(docElementName)) {
            // Check child element name
            org.w3c.dom.NodeList nodes = doc.getElementsByTagNameNS("urn:iso:std:iso:20022:tech:xsd:pacs.009.001.09", "*");
            for (int i = 0; i < nodes.getLength(); i++) {
                String nodeName = nodes.item(i).getLocalName();
                if ("FICdtTrf".equals(nodeName)) {
                    return "pacs.009.001.09";
                }
            }
        }
        return "Unknown message type";
    }

    /**
     * Converts an MX XML file to MT JSON format.
     *
     * @param xmlFilePath Path to the input XML file.
     * @param xsdFilePath Path to the XSD schema file.
     * @param outputJsonPath Path to save the output JSON file.
     * @throws Exception If validation, parsing, or conversion fails.
     */
    public void convert(String xmlFilePath, String xsdFilePath, String outputJsonPath) throws Exception {
        
    	// Step 1: Parse XML and Identify the PACS is 08 or 09.?
        String messageType = identifyMessageType(xmlFilePath);
        if(messageType.equals("pacs.008.001.09")) {
        	xmlValidator.validate(xmlFilePath, pacs008xsd);			// Validate XML against XSD
        	JAXBContext ctx = JAXBContext.newInstance(Message.class);
        	Message message = (Message) unmarshalXML(xmlFilePath,ctx);	// Parse XML to JAXB Model
        	JsonObject mtJsonMessage = ruleEnginePacs008.applyRules(message);
        	jsonBuilder.buildJson(mtJsonMessage, outputJsonPath);
        	System.out.println("P8 Message ID: " + message.getAppHdr().getMsgDefIdr());
            System.out.println("P8 Debtor Name: " + message.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(0).getDbtr().getNm());
        } else if(messageType.equals("pacs.009.001.09")) {
        	xmlValidator.validate(xmlFilePath, pacs009xsd);
        	JAXBContext ctx = JAXBContext.newInstance(com.gss.vmc.jaxb.pacs00900109.Message.class);
        	com.gss.vmc.jaxb.pacs00900109.Message message = (com.gss.vmc.jaxb.pacs00900109.Message) unmarshalXML(xmlFilePath,ctx);	
        	System.out.println("P9 Message ID: " + message.getAppHdr().getMsgDefIdr());
            System.out.println("P9 Debtor Name: " + message.getFICdtTrf().getCdtTrfTxInf().get(0).getDbtr().getFinInstnId().getBICFI());
        }
        
        // Step 2: Parse XML and Identify the PACS is 08 or 09.?
        //Message message = unmarshalXML(xmlFilePath);
        // Access the content tree
        //System.out.println("Message ID: " + message.getAppHdr().getMsgDefIdr());
        //System.out.println("Debtor Name: " + message.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(0).getDbtr().getNm());
        //BusinessApplicationHeaderV03 appHdr = parseFindPacsHead(xmlFilePath);
        
        // Step 2: Parse XML and extract elements
        //FIToFICustomerCreditTransferV09 document = mxParser.parseJaxb(xmlFilePath);
        //Map<String, String> extractedElements = mxParser.extractElements(document);

        // Step 3: Apply transformation rules to build MtMessage POJO
        //JsonObject mtJsonMessage = ruleEngine.applyRules(document,mxParser);

        // Step 4: Build JSON from POJO
        //jsonBuilder.buildJson(mtJsonMessage, outputJsonPath);
    }
    
//    public BusinessApplicationHeaderV03 parseFindPacsHead(String xmlFilePath) throws Exception {
//        File xmlFile = new File(xmlFilePath);
//
//        // Parse as DOM
//        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//        //dbf.setNamespaceAware(true);
//        DocumentBuilder db = dbf.newDocumentBuilder();
//        org.w3c.dom.Document doc = db.parse(xmlFile);
//        doc.getDocumentElement().normalize();
//
////        Node frNode = doc.getElementsByTagNameNS(
////        	    "urn:iso:std:iso:20022:tech:xsd:head.001.001.03",
////        	    "AppHdr"
////        	).item(0);
////        	System.out.println("Fr Node = " + frNode);
////        
////        //While Debug helper: print all element names + namespaces
////        NodeList all = doc.getElementsByTagName("*");
////        for (int i = 0; i < all.getLength(); i++) {
////            Element el = (Element) all.item(i);
////            System.out.println("LocalName=" + el.getLocalName() +
////                               ", Prefix=" + el.getPrefix() +
////                               ", NamespaceURI=" + el.getNamespaceURI());
////        }
//        
//        // Find the subtree of head001Node
////        Node head001Node = doc.getElementsByTagNameNS(
////            "urn:iso:std:iso:20022:tech:xsd:head.001.001.03",
////            "AppHdr"
////        ).item(0);
//        //Node head001Node = doc.getElementsByTagName("AppHdr").item(0);
////        Node head001Node = doc.getElementsByTagNameNS(
////        	    "urn:issettled",   // default namespace
////        	    "AppHdr"           // local name
////        	).item(0);
////
////        System.out.println("AppHdr Node = " + head001Node);
//        
//        Node head001Node = doc.getElementsByTagName("AppHdr").item(0);
//        Element  el = (Element) head001Node;
//        System.out.println("AppHdr Node = " + el.getTextContent());
//        if(head001Node!=null) {
//        	JAXBContext ctx = JAXBContext.newInstance(BusinessApplicationHeaderV03.class);
//            Unmarshaller unmarshaller = ctx.createUnmarshaller();
//
//            // Unmarshal starting from the sub-node
//            JAXBElement<BusinessApplicationHeaderV03> head001Root =
//                unmarshaller.unmarshal(head001Node, BusinessApplicationHeaderV03.class);
//
//            return head001Root.getValue();
//        }
//        
//        return null;
//    }
//    
    public Object parseFindPacsBody(String xmlFilePath) throws Exception {
        File xmlFile = new File(xmlFilePath);

        // Parse as DOM
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        org.w3c.dom.Document doc = db.parse(xmlFile);

        // Find the subtree of pacs008
        Node pacs008Node = doc.getElementsByTagNameNS(
            "urn:iso:std:iso:20022:tech:xsd:pacs.008.001.09",
            "FIToFICstmrCdtTrf"
        ).item(0);
        
        if(pacs008Node!=null) {
        	JAXBContext ctx = JAXBContext.newInstance(FIToFICustomerCreditTransferV09.class);
            Unmarshaller unmarshaller = ctx.createUnmarshaller();

            // Unmarshal starting from the sub-node
            JAXBElement<FIToFICustomerCreditTransferV09> pacs008Root =
                unmarshaller.unmarshal(pacs008Node, FIToFICustomerCreditTransferV09.class);

            return pacs008Root.getValue();
        }
        
        // Find the subtree of pacs009
        Node pacs009Node = doc.getElementsByTagNameNS(
            "urn:iso:std:iso:20022:tech:xsd:pacs.009.001.09",
            "FICdtTrf"
        ).item(0);
        
        if(pacs009Node!=null) {
        	JAXBContext ctx = JAXBContext.newInstance(FinancialInstitutionCreditTransferV09.class);
            Unmarshaller unmarshaller = ctx.createUnmarshaller();

            // Unmarshal starting from the sub-node
            JAXBElement<FinancialInstitutionCreditTransferV09> pacs009Root =
                unmarshaller.unmarshal(pacs009Node, FinancialInstitutionCreditTransferV09.class);

            return pacs009Root.getValue();
        }
        
        return null;
    }

    public static void main(String[] args) {
//        if (args.length != 3) {
//            System.err.println("Usage: MxToMtConverter <xmlFile> <xsdFile> <outputJson>");
//            System.exit(1);
//        }
        try {
        	String a = "D:\\Project\\VMC\\src\\main\\java\\com\\prowidesoftware\\controller\\pacs.008.001.09-sample.xml";
        	String b = "D:\\Project\\VMC\\src\\main\\java\\com\\prowidesoftware\\controller\\pacs.008.001.09.xsd";
        	String c = "D:\\Project\\VMC\\src\\main\\java\\com\\prowidesoftware\\controller\\mt103.json";
        	
            new MxToMtConverter().convert(a, b, c);
            System.out.println("Conversion completed successfully.");
        } catch (Exception e) {
            System.err.println("Conversion failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

// For unit tests, use JUnit5
// Example: TestMxToMtConverter.java with @Test methods for validation, parsing, rules, etc.
// Use sample files from attachments for input/output assertions.