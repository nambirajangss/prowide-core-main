
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
import com.prowidesoftware.swift.model.mt.mt1xx.MT103;
import com.prowidesoftware.swift.model.mt.mt5xx.MT547;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class MTParseMultiFile {
	
	private static final Logger log = Logger.getLogger(MTParseMultiFile.class.getName());
	static {
        try {
            // Create a FileHandler to write logs to a file
            FileHandler fileHandler = new FileHandler("MTParseMultiFile.log", true);
            fileHandler.setFormatter(new SimpleFormatter()); // Format logs
            //fileHandler.setLevel(Level.ALL);  // Ensure it captures all levels
            log.addHandler(fileHandler);
            log.setUseParentHandlers(false); // Disable default console logging
            // Set this logger to INFO level (disables FINEST, FINER, FINE)
            log.setLevel(Level.INFO);
            
            // Create a ConsoleHandler to show logs in console
			/*
			 * ConsoleHandler consoleHandler = new ConsoleHandler();
			 * consoleHandler.setFormatter(new SimpleFormatter());
			 * consoleHandler.setLevel(Level.ALL); // Ensure it prints all log levels
			 * log.addHandler(consoleHandler);
			 */
            
            // Redirect System.out and System.err to the logger
            PrintStream logStream = new PrintStream(new FileOutputStream("MTParseMultiFile.log", true));
            System.setOut(logStream);
            System.setErr(logStream);
            
            // Set global logging to capture third-party logs
            Logger rootLogger = Logger.getLogger("");
            rootLogger.addHandler(fileHandler);
            rootLogger.setLevel(Level.INFO);
//            for (Handler handler : rootLogger.getHandlers()) {
//                handler.setLevel(Level.INFO);
//            }
//            rootLogger.setLevel(Level.INFO);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
    	
	   String folderPath = "C:\\Users\\gss\\Softwares\\MTMX Project\\prowide-core-main\\prowide-core-main\\src\\generated\\java\\com\\prowidesoftware\\swift\\model\\mt\\mt1xx\\data\\"; // Change this to your folder path

       try {
           // Get all files in the folder
           Files.list(Paths.get(folderPath))
               .filter(Files::isRegularFile)  // Ensure it's a file, not a directory
               .forEach(filePath -> processFile(filePath.toString())); // Process each file
       } catch (Exception e) {
    	   log.severe("Error reading files from directory: " + e.getMessage());
    	   e.printStackTrace();
       }     
    }
    
    private static void processFile(String filePath) {
    	File file = null;
    	try
        {
        	file = new File(filePath);
	    	System.out.println("##################### Processing File Started ##################### :" + file.getName());
            String mtfin = getMTFinMsg(filePath);
            File jsonWriter = getJsonWriter(file); 
            if(mtfin!=null && jsonWriter!=null)
            	parseMTMessageToJson(mtfin,jsonWriter);
            System.out.println("##################### Finished Processing File ##################### : " + file.getName());
        } catch (Exception e) {
        	log.severe("Error while processing processFile :" + file.getName() + ": " + e.getMessage());
        	e.printStackTrace();
        }
    }
    private static String getMTFinMsg(String filePath) {
    	try
        {
    		// Read file using Files.readAllLines()
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            String mtfin = String.join("\n", lines);
            //System.out.println("processing msg: " + mtfin);
            return mtfin;
        }
    	catch (Exception e) {
    		log.severe("Error while processing getMTFinMsg: " + e.getMessage());
    		e.printStackTrace();
        }
		return filePath;
		
    }
    
    private static File getJsonWriter(File file) {
    	File jsonFile = null;
    	try
        {
	    	String filename = file.getName().substring(0, 5)+"-Json";
	        String jsonFileName = filename + ".json";  // Log file name same as file name
	        // File logFile = new File(file.getParent(), logFileName); 
	        // Define the Logs directory inside the parent folder
	        File jsonDir = new File(file.getParent(), "Json");
	        // Ensure the Logs directory exists, create if not
	        if (!jsonDir.exists()) {
	        	jsonDir.mkdirs(); // Create Logs directory if it doesn't exist
	        }
	        // Create the log file inside the Logs directory
	        jsonFile = new File(jsonDir, jsonFileName);
        } catch (Exception e) {
        	log.severe("Error while processing getJsonWriter: " + e.getMessage());
        	e.printStackTrace();
        }
		return jsonFile;    	
    }
    
    private static void parseMTMessageToJson(String mtfin, File jsonFile) {
		try {
			SwiftParser parser = new SwiftParser(mtfin);
	        SwiftMessage mt = parser.message();
			AbstractMT amtToJson = mt.toMT();
	        String json = amtToJson.toJson();
	        try (BufferedWriter jsonWriter = new BufferedWriter(new FileWriter(jsonFile))) {
				jsonWriter.write(json);  // JSON content
				log.info("MT FIN Message converted to JSON and FILE Created in this file path: "+jsonFile.getParent());
				// log.info(jsonFile.getParent());
			}
		} catch (Exception e) {
			log.severe("Error while processing parseMTMessageToJson: " + e.getMessage());
			e.printStackTrace();
		}
    }

}
