
/*
 * Copyright 2006-2021 Prowide
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
package com.prowidesoftware.controller;

import com.prowidesoftware.swift.io.parser.SwiftParser;
import com.prowidesoftware.swift.model.SwiftMessage;
import com.prowidesoftware.swift.model.field.Field15O;
import com.prowidesoftware.swift.model.field.Field77J;
import com.prowidesoftware.swift.model.field.Narrative;
import com.prowidesoftware.swift.model.field.NarrativeResolver;
import com.prowidesoftware.swift.model.mt.AbstractMT;
import com.prowidesoftware.swift.model.mt.AbstractMTAdapter;
import com.prowidesoftware.swift.model.mt.mt1xx.MT102;
import com.prowidesoftware.swift.model.mt.mt1xx.MT103;
import com.prowidesoftware.swift.model.mt.mt2xx.MT201;
import com.prowidesoftware.swift.model.mt.mt5xx.MT547;
import org.springframework.http.ResponseEntity;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MTParse {
	private static final LogCaptureHandler logCaptureHandler = new LogCaptureHandler();
    public static void main(String[] args) throws Exception {
        
    	String filePath = "C:\\Users\\gss\\Softwares\\MTMX Project\\MTConverter\\prowide-core-main\\src\\test\\java\\com\\prowidesoftware\\swift\\model\\mt\\DATA\\MT102.txt"; 
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            // Convert list to a single formatted string
            String mtfin = String.join("\n", lines);
            RegexValidation.validateAllBlock(mtfin);
            SwiftParser parser = new SwiftParser(mtfin);
            SwiftMessage mt = parser.message();
            AbstractMT amtToJson = mt.toMT();
            
         // Collect logs and return them in response if errors exist
            List<String> warnSevereLogs = logCaptureHandler.getWarningAndSevereLogs();
			List<String> errorList = new ArrayList<String>();
            if (!parser.getErrors().isEmpty()) {
            	errorList.addAll(parser.getErrors());
            }
            if (!warnSevereLogs.isEmpty()) {
            	errorList.addAll(warnSevereLogs);
            }
            if (!errorList.isEmpty()) {
            	String errorMsgs = "";
            	int i = 0;
            	for(String error: errorList) {
            		i++;
            		errorMsgs = errorMsgs + "\n ERROR NO-" + i +" : "+error;
            	}
            	System.out.println("####### Please revalidate the malformed Block structure and unrecognized Field in the MT message:\n"+ errorMsgs);
            }
            //amtToJson.getFields();
            //amtToJson.xml();
            //MT201 mt201 = (MT201) mt.toMT();
            //System.out.println("####### MT getLoop1List ######## : "+mt201.getLoop1List());
                        
            String json = amtToJson.toJson();
            System.out.println("####### MTS RAW MESSAGE TO MT STRUCTURED JSON ######## : "+json);
           // if(AbstractMTAdapter.isAPITest) {
            	//JsonFlattener jf = new JsonFlattener();
            //	json = JsonFlattener.flattenJson(json);
            //}
            //System.out.println("####### MT STRUCTURED JSON TO MT FLATTEN JSON ######## : "+json);
            //AbstractMT amtFromJson = AbstractMT.fromJson(json);
            //System.out.println("####### JSON TO MT RAW MESSAGE ######## : "+amtFromJson.getSwiftMessage().message());
            //System.out.println(amtFromJson.getSwiftMessage().message());
            
            //System.out.println("String mt103fin = \"" + mt103fin.replace("\n", "\\n\" + \n\"") + "\";");
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

}
