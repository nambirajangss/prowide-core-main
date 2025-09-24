package com.gss.vmc.mtmx.rule.integrator;

import com.gss.vmc.mxmt.CXR17ClearingIdentifier; // Reuse if applicable
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.gss.vmc.MtJsonMessage;
import com.gss.vmc.jaxb.pacs00800109.*;
import com.gss.vmc.json.MTMasterFieldsJson;
import com.gss.vmc.json.SequenceA;
import com.prowidesoftware.swift.model.field.Field55A;
import com.prowidesoftware.swift.model.field.Field70;

import java.util.ArrayList;
import java.util.Map;

/**
 * Applies MT to MX mapping rules, including custom logic like remittance information.
 * Mirrors RuleEnginePacs008 but in reverse: MT JSON POJO -> MX JAXB model.
 * Uses pseudocode from equivalent reverse rules (e.g., MT_To_MXRemittanceInformation.txt if available).
 */
public class RuleEngineMtToMxPacs008 {

    /**
     * Applies transformation rules to the parsed MT JSON POJO and builds an MX Message JAXB model.
     *
     * @param mtJsonMessage Parsed MtJsonMessage.
     * @return Populated Message (JAXB).
     */
    public Message applyRules(MTMasterFieldsJson mtJsonMessage) {
        Message mxMessage = new Message();
        FIToFICustomerCreditTransferV09 fiToFi = new FIToFICustomerCreditTransferV09();
        mxMessage.setFIToFICstmrCdtTrf(fiToFi);
        
        // Apply list of rules below (mirror MX to MT rules in reverse)
        applyMtToMxField70(mtJsonMessage, fiToFi);
        applyMtToMxField55A(mtJsonMessage, fiToFi);
        // applyMtToMxCXR2(mtJsonMessage, fiToFi); // If additional rules
        
        return mxMessage;
    }
    
    public FIToFICustomerCreditTransferV09 applyBodyRules(MTMasterFieldsJson mtJsonMessage) {
        FIToFICustomerCreditTransferV09 fiToFi = new FIToFICustomerCreditTransferV09();
        
        // Example: Map group header
        GroupHeader93 grpHdr = new GroupHeader93();
        grpHdr.setMsgId(mtJsonMessage.getApplicationHeaderBlock().getMessageType() + "-Converted"); // Example mapping
        fiToFi.setGrpHdr(grpHdr);
        
        // Example: Map credit transfer info
        CreditTransferTransaction43 txInf = new CreditTransferTransaction43();
        PaymentIdentification13 pmtId = new PaymentIdentification13();
        pmtId.setEndToEndId("NOTPROVIDED"); // Default or from MT
        txInf.setPmtId(pmtId);
        
        ActiveOrHistoricCurrencyAndAmount instdAmt = new ActiveOrHistoricCurrencyAndAmount();
        instdAmt.setCcy("USD"); // From MT
        //instdAmt.setValue("100.00"); // From MT
        txInf.setInstdAmt(instdAmt);
        
        fiToFi.getCdtTrfTxInf().add(txInf);
        
        return fiToFi;
    }
    
    private void applyMtToMxField70(MTMasterFieldsJson mtJsonMessage, FIToFICustomerCreditTransferV09 fiToFi) {
        // Extract from MT JSON (e.g., field70 in sequenceA)
        SequenceA sequenceA = mtJsonMessage.getTextBlock().getTextBlockFields().getSequenceA();
        if (sequenceA.getField70()!=null) {
            //Field70 f70 = new Gson().fromJson(sequenceA.getField70(), Field70.class); // Or custom deserialization
            
            // Map to MX (e.g., Debtor Name or Remittance Info)
            CreditTransferTransaction43 txInf = fiToFi.getCdtTrfTxInf().isEmpty() ? new CreditTransferTransaction43() : fiToFi.getCdtTrfTxInf().get(0);
            PartyIdentification135 dbtr = new PartyIdentification135();
            dbtr.setNm(sequenceA.getField70().getName()); // Reverse mapping example
            txInf.setDbtr(dbtr);
            
            if (fiToFi.getCdtTrfTxInf().isEmpty()) {
                fiToFi.getCdtTrfTxInf().add(txInf);
            }
        }
    }
    
    private void applyMtToMxField55A(MTMasterFieldsJson mtJsonMessage, FIToFICustomerCreditTransferV09 fiToFi) {
        // Extract from MT JSON (e.g., field55A in sequenceA)
    	SequenceA sequenceA = mtJsonMessage.getTextBlock().getTextBlockFields().getSequenceA();
        if (sequenceA.getField55A()!=null) {
            //Field55A f55A = new Gson().fromJson(sequenceA.get("field55A"), Field55A.class);
            
            // Map to MX (e.g., Third Reimbursement Agent)
            CreditTransferTransaction43 txInf = fiToFi.getCdtTrfTxInf().isEmpty() ? new CreditTransferTransaction43() : fiToFi.getCdtTrfTxInf().get(0);
            //Party44Choice thirdReimbstnAgt = new Party44Choice();
            CashAccount38 ca38 = new CashAccount38();
            // Set BICFI or other from f55A.getIdentifierCode()
            // thirdReimbstnAgt.set... (map accordingly)
            txInf.setIntrmyAgt3Acct(ca38);
            
            if (fiToFi.getCdtTrfTxInf().isEmpty()) {
                fiToFi.getCdtTrfTxInf().add(txInf);
            }
        }
    }
    
    /**
     * Implements the reverse of MX_To_MTRemittanceInformation (e.g., MT_To_MXRemittanceInformation).
     * Converts MT field 70 to MX remittance info.
     *
     * @param mtJsonMessage Parsed MT message.
     * @return Mapped remittance structure for MX.
     */
    private Object applyRemittanceRule(MtJsonMessage mtJsonMessage) {
        // Implement reverse pseudocode logic here
        // Extract MT70 full string
        // Apply reverse cases: Split into UltimateCreditor, Purpose, EndToEndId, etc.
        // Return JAXB RemittanceInformation structure
        return null; // Stub
    }
}