package com.gss.vmc;

import com.gss.vmc.jaxb.pacs00800109.CreditTransferTransaction43;
import com.gss.vmc.jaxb.pacs00800109.FIToFICustomerCreditTransferV09;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * Parses MX XML files and extracts relevant elements using XPath.
 * Assumes namespace-aware parsing for pacs.008.001.09.
 */
public class MxParser {

    private final XPath xpath;

    public MxParser() {
        XPathFactory xPathFactory = XPathFactory.newInstance();
        this.xpath = xPathFactory.newXPath();
        // Set namespace context if needed, e.g., for pacs.008
        // xpath.setNamespaceContext(...);
    }

    /**
     * Parses the XML file into a DOM Document.
     *
     * @param xmlFilePath Path to the XML file.
     * @return Parsed Document.
     * @throws ParserConfigurationException If parser setup fails.
     * @throws IOException If file reading fails.
     * @throws SAXException If parsing fails.
     */
//    public Document parse(String xmlFilePath) throws ParserConfigurationException, IOException, SAXException {
//        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//        factory.setNamespaceAware(true); // Namespace-aware for MX messages
//        DocumentBuilder builder = factory.newDocumentBuilder();
//        return builder.parse(new File(xmlFilePath));
//    }
    
    /**
     * Parses the XML file into a DOM Document.
     *
     * @param xmlFilePath Path to the XML file.
     * @return Parsed Document.
     * @throws JAXBException 
     * @throws ParserConfigurationException If parser setup fails.
     * @throws IOException If file reading fails.
     * @throws SAXException If parsing fails.
     */
    public FIToFICustomerCreditTransferV09 parseJaxb(String xmlFilePath) throws JAXBException {
        File xmlFile = new File(xmlFilePath);
        JAXBContext ctx = JAXBContext.newInstance(FIToFICustomerCreditTransferV09.class);
        Unmarshaller unmarshaller = ctx.createUnmarshaller();
        FIToFICustomerCreditTransferV09 root = (FIToFICustomerCreditTransferV09) unmarshaller.unmarshal(xmlFile);
        
        return root;
    }
    
    public Object parsePartialJaxb(String xmlFilePath) throws Exception {
        File xmlFile = new File(xmlFilePath);

        // Parse as DOM
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        org.w3c.dom.Document doc = db.parse(xmlFile);

        // Find the subtree you care about
        Node targetNode = doc.getElementsByTagNameNS(
            "urn:iso:std:iso:20022:tech:xsd:pacs.008.001.09",
            "FIToFICstmrCdtTrf"
        ).item(0);

        JAXBContext ctx = JAXBContext.newInstance(FIToFICustomerCreditTransferV09.class);
        Unmarshaller unmarshaller = ctx.createUnmarshaller();

        // Unmarshal starting from the sub-node
        JAXBElement<FIToFICustomerCreditTransferV09> root =
            unmarshaller.unmarshal(targetNode, FIToFICustomerCreditTransferV09.class);

        return root.getValue();
    }



    /**
     * Extracts specific elements from the parsed Document using XPath.
     * This method should be extended to extract all relevant pacs.008 fields.
     *
     * @param document Parsed XML Document.
     * @return Map of element paths to values.
     * @throws XPathExpressionException If XPath evaluation fails.
     */
//    public Map<String, String> extractCXR17Elements(Document document) throws XPathExpressionException {
//        Map<String, String> elements = new HashMap<>();
//        // Example extractions based on pacs.008 structure
//        elements.put("MsgId", (String) xpath.evaluate("//FIToFICstmrCdtTrf/GrpHdr/MsgId", document, XPathConstants.STRING));
//        elements.put("CreDtTm", (String) xpath.evaluate("//FIToFICstmrCdtTrf/GrpHdr/CreDtTm", document, XPathConstants.STRING));
//        elements.put("EndToEndId", (String) xpath.evaluate("//FIToFICstmrCdtTrf/CdtTrfTxInf/PmtId/EndToEndId", document, XPathConstants.STRING));
//        elements.put("InstdAmt", (String) xpath.evaluate("//FIToFICstmrCdtTrf/CdtTrfTxInf/InstdAmt", document, XPathConstants.STRING));
//        elements.put("Dbtr/Nm", (String) xpath.evaluate("//FIToFICstmrCdtTrf/CdtTrfTxInf/Dbtr/Nm", document, XPathConstants.STRING));
//        // Add more extractions as needed, including RemittanceInformation, Purpose, etc.
//        // For complex structures, use more advanced XPath or JAXB unmarshalling.
//        
//        
//        
//        return elements;
//    }
    
    public Map<String, String> extractCXR17Elements(FIToFICustomerCreditTransferV09 root) {
        Map<String, String> elements = new HashMap<>();

        // Group Header
        if (root.getGrpHdr() != null) {
            elements.put("MsgId", root.getGrpHdr().getMsgId());
            //elements.put("CreDtTm", root.getFIToFICstmrCdtTrf().getGrpHdr().getCreDtTm());
        }

        // Take the first transaction (or loop if multiple)
        if (root.getCdtTrfTxInf() != null && !root.getCdtTrfTxInf().isEmpty()) {
        	CreditTransferTransaction43 tx = root.getCdtTrfTxInf().get(0);

            if (tx.getPmtId() != null) {
                elements.put("EndToEndId", tx.getPmtId().getEndToEndId());
            }
            if (tx.getInstdAmt() != null) {
                //elements.put("InstdAmt", tx.getInstdAmt().getValue());  // Amount
            }
            if (tx.getDbtr() != null) {
                elements.put("Dbtr/Nm", tx.getDbtr().getNm());
            }
        }

        return elements;
    }

}
