package com.gss.vmc.mxmt;

import org.xml.sax.SAXException;
import java.io.File;
import java.io.IOException;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

/**
 * Validates XML files against an XSD schema.
 */
public class XmlValidator {

    /**
     * Validates the given XML file against the provided XSD schema.
     *
     * @param xmlFilePath Path to the XML file.
     * @param xsdFilePath Path to the XSD schema file.
     * @throws SAXException If validation fails.
     * @throws IOException If file reading fails.
     */
    public void validate(String xmlFilePath, String xsdFilePath) throws SAXException, IOException {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(new File(xsdFilePath));
        Validator validator = schema.newValidator();
        Source xmlSource = new StreamSource(new File(xmlFilePath));
        validator.validate(xmlSource);
    }
}