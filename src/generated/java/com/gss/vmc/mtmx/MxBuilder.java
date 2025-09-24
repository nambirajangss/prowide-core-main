package com.gss.vmc.mtmx;

import com.gss.vmc.jaxb.pacs00800109.FIToFICustomerCreditTransferV09;
import com.gss.vmc.jaxb.pacs00800109.Message;
import com.gss.vmc.jaxb.pacs00900109.FinancialInstitutionCreditTransferV09;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;

import java.io.File;

/**
 * Builds MX XML from JAXB models using JAXB Marshaller.
 */
class MxBuilder {

    /**
     * Converts JAXB model to XML and writes to file.
     *
     * @param mxModel JAXB object (e.g., Message or FIToFICustomerCreditTransferV09).
     * @param outputPath Path to output XML file.
     * @param xsdFilePath Path to XSD for optional schema association (not enforced here).
     * @throws Exception If marshalling fails.
     */
    public void buildXml(Object mxModel, String outputPath, String xsdFilePath) throws Exception {
        JAXBContext ctx;
        if (mxModel instanceof Message) {
            ctx = JAXBContext.newInstance(Message.class);
        } else if (mxModel instanceof FIToFICustomerCreditTransferV09) {
            ctx = JAXBContext.newInstance(FIToFICustomerCreditTransferV09.class);
        } else if (mxModel instanceof FinancialInstitutionCreditTransferV09) {
            ctx = JAXBContext.newInstance(FinancialInstitutionCreditTransferV09.class);
        } else {
            throw new IllegalArgumentException("Unsupported MX model type.");
        }
        
        Marshaller marshaller = ctx.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        // Optionally associate schema: marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, ...);
        
        marshaller.marshal(mxModel, new File(outputPath));
    }
}