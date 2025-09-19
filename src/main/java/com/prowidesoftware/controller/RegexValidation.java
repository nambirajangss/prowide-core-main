package com.prowidesoftware.controller;

import com.prowidesoftware.swift.model.mt.MtSequenceEnum;
import org.apache.commons.lang3.EnumUtils;

import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexValidation {
    private static final Logger log = Logger.getLogger(RegexValidation.class.getName());
    final static String block1Flag = "\\{1:";
    final static String block1 = "\\{1:(?<ApplicationId>\\w)(?<ServiceId>\\d{2})(?<LTAddress>[A-Z0-9]{11,12})(?<SessionNumber>\\d{6})(?<SequenceNumber>\\d{4})\\}";
    final static String block2Flag = "\\{2:";
    final static String block2I = "\\{2:I(?<MessageType>\\d{3})(?<DestinationAddress>[A-Z0-9]{12})(?<Priority>[A-Z])\\}";
	final static String block2O = "\\{2:O(?<MessageType>\\d{3})(?<InputTime>\\d{4})(?<SendersDate>\\d{6})(?<LTAddress>[A-Z0-9]{12})(?<SessionNumber>\\d{6})(?<SequenceNumber>\\d{4})(?<OutputDate>\\d{6})(?<OutputTime>\\d{4})(?<Priority>[A-Z])\\}"; 
	final static String block3Flag = "\\{3:";
	final static String block3Flag1 = "\\{3";
	final static String block3 = "\\{3:(\\{\\d{3}:[^}]+\\})+\\}";
	final static String block3Dup = "\\{(\\d{3}):([^\\}]+)\\}";
	final static String block4Flag = "\\{4:";
	//final static String block4 = "";
	final static String block4 = "(?s)\\{4:\\s*(.*?)\\s*-\\}";
	final static String block5Flag = "\\{5:";
	final static String block5 = "";
	final static String blockS = "";
	final static String nonBraceContent = "(?:(?<=^)|(?<=\\}))[ \\t]*((?!:[0-9]{2}[A-Z]?:)[^{}\\r\\n]+(?:\\s+(?!:[0-9]{2}[A-Z]?:)[^{}]+)*)[ \\t]*(?=\\{|$)"; //}test@#{ Validation to ignore this content in between Blocks
	//Duplicate Fix done
	//Order by (Shuffled order is working fine)
	//In between text done
	
	public static boolean validateBlock(String input, String inBlock) {
		final Pattern pattern = Pattern.compile(inBlock);
	    final Matcher matcher = pattern.matcher(input);
	    //return matcher.matches(); //true if, and only if, the entire region sequence matches this matcher's pattern
	    return matcher.find(); //true if, and only if, a subsequence of the input sequence matches this matcher's pattern
	}
	
	//}test@#{ Validation to ignore this content in between Blocks
	public static void validateNonBraceContent(String input, String inBlock) {
		final Pattern pattern = Pattern.compile(inBlock);
	    final Matcher matcher = pattern.matcher(input);
	    List<String> noValidFields = new ArrayList<>();
	    while (matcher.find()) {
	    	if(matcher.group(0)!=null && !matcher.group(0).isEmpty()) {
	    		String trimStr = matcher.group(0).trim();
	    		if(!trimStr.isEmpty()) {
	    			System.out.println("####################### trimStr: "+trimStr);
	    			noValidFields.add("\t\t"+trimStr);
	    		}
	    	}
	    }
	    if(!noValidFields.isEmpty()) {
        	String noValidStr = "\n\t====== Invalid content in between Blocks ====== \n";
        	noValidStr = noValidStr + String.join("\n", noValidFields);
        	log.warning(noValidStr);
        }
	}	
	
	public static void validateBlock4Loop(String input, String inBlock) {
		final Pattern pattern = Pattern.compile(inBlock);
	    final Matcher matcher = pattern.matcher(input);
        //boolean isvalidBlock4 = true;
	    if (matcher.find()) {
            for (int i = 0; i < matcher.groupCount(); i++) {
                String[] lines = matcher.group(i).split("\n");

                List<String> validFields = new ArrayList<>();
                List<String> errors = new ArrayList<>();

                Pattern validFieldPattern = Pattern.compile("^:(\\d{2}[A-Z]?):(.*)");
                Pattern ruleValidation = Pattern.compile("^:\\d{2}[A-Z]?:\\/\\/CH\\s*$|^:\\d{2}[A-Z]?:\\/\\/(?!CH)[A-Za-z]{1}.*$|^:\\d{2}[A-Z]?:/C/\\s*$|^:\\d{2}[A-Z]?:/C\\s*$|^:\\d{2}[A-Z]?:/D/\\s*$|^:\\d{2}[A-Z]?:/D\\s*$|^:\\d{2}[A-Z]?:/\\s*$|^:\\d{2}[A-Z]?://\\s*$|^:\\d{2}[A-Z]?:\\s*$");
                
                for (int k = 0; k < lines.length; k++) {
                    String line = lines[k].trim();

                    Matcher matcher2 = validFieldPattern.matcher(line);
                    if (matcher2.find()) {
                        validFields.add("\t\t✔ Valid field: " + line);
                    } else if (line.matches("^(\\d{1,2}[A-Za-z]?:.*|\\d{1,2}:.*)")) {
                        errors.add("\t\t⚠ Missing colon at Block-4 Field tag >\t" + line);
                    } else if (line.matches("^:.*") || line.matches("^:\\d{1,2}[A-Za-z]?:?.*")) {
                        errors.add("\t\t❌ Malformed field tag at Block-4 >\t" + line);
                    } else {
                        //errors.add("⚠ Possibly continuation or orphan text at line " + (k + 1) + ": " + line);
                    }
                }
                // Output results
                //System.out.println("=== Valid Fields ===");
                //validFields.forEach(System.out::println);
                //System.out.println("\n=== Errors === : Proper field tag format (:XX: or :XXA:) like (:20: or :52B:)");
                //errors.forEach(System.out::println);
                
                if(!errors.isEmpty()) {
                	String errorStr = "\n\t====== Block-4 Errors ====== \n\tProper field tag format (:XX: or :XXA:) like (:20: or :52B:) \n";
                	errorStr = errorStr + String.join("\n", errors);
                	log.severe(errorStr);
                	//isvalidBlock4 = false;
                	if(!validFields.isEmpty()) {
                    	String validStr = "\n\t====== Block-4 Valid Fields ====== \n";
                    	validStr = validStr + String.join("\n", validFields);
                    	log.warning(validStr);
    	            }
                }
            }
        }
	    //return isvalidBlock4; 
	}
	
/*	public static void validateBlock4RuleBased(String input, String inBlock) {
		final Pattern pattern = Pattern.compile(inBlock);
	    final Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            for (int i = 0; i < matcher.groupCount(); i++) {
                String[] lines = matcher.group(i).split("\n");
                List<String> invalidFields = new ArrayList<>();
                Pattern getField = Pattern.compile("(?<=:)\\d{2}[A-Z]?(?=:)"); //Will match only 53B
                Pattern ctr15Validation = Pattern.compile("^:\\d{2}[A-Z]?:\\/\\/CH\\s*$|^:\\d{2}[A-Z]?:\\/\\/(?!CH)[A-Za-z]{1}.*$|^:\\d{2}[A-Z]?:/C/\\s*$|^:\\d{2}[A-Z]?:/C\\s*$|^:\\d{2}[A-Z]?:/D/\\s*$|^:\\d{2}[A-Z]?:/D\\s*$|^:\\d{2}[A-Z]?:/\\s*$|^:\\d{2}[A-Z]?://\\s*$|^:\\d{2}[A-Z]?:\\s*$");
                Set<String> ctr15Fields = new HashSet<String>();
                if (EnumUtils.isValidEnum(MtSequenceEnum.class, "RULESCTR15")) {
                    MtSequenceEnum mtRepeatEnum = MtSequenceEnum.valueOf("RULESCTR15");
                    ctr15Fields = mtRepeatEnum.sequences();
        		}
                if (!ctr15Fields.isEmpty()) {
                	for (int k = 0; k < lines.length; k++) {
                        String line = lines[k].trim();
                        Matcher fieldMatcher = getField.matcher(line);
                        if (fieldMatcher.find() && ctr15Fields.contains(fieldMatcher.group(0)) ) {
                        	Matcher ruleMatcher = ctr15Validation.matcher(line);
                            if (ruleMatcher.find()) {
                            	invalidFields.add("\t\t✔ InValid fields: " + line);
                            }
                        }
                	}
                	if(!invalidFields.isEmpty()) {
                    	String validStr = "\n\t====== Block-4 Found InValid Fields in following list: 52D,53A,53B,53D,54A,54D,55A,55D,56A,56C,56D,57A,57D,58A ====== \n";
                    	validStr = validStr + String.join("\n", invalidFields);
                    	validStr = validStr + "\n\n";
                    	String ctr15Msg = "\t\tSummary of Invalid Patterns to Detect and Correct:\n" +
                    			"\t\tCase\tDescription\t\tDetection Approach\n" +
                    			"\t\t1\t:53B://CH\t\tCHIPS ID marker without actual ID\n" +
                    			"\t\t2\t:53B://IN\t\tOnly //CH is allowed, not //XX\n" +
                    			"\t\t3\t:53B:/C/\t\t/C/ without account\n" +
                    			"\t\t4\t:53B:/D/\t\t/D/ without account\n" +
                    			"\t\t5\t:53B:/C\t\t/C without account and without second slash\n" +
                    			"\t\t6\t:53B:/D\t\t/D without account and without second slash\n" +
                    			"\t\t7\t:53B:/\t\tLone slash, no account data\n" ;
                    	validStr = validStr + ctr15Msg;
                    	log.warning(validStr);
    	            }
                }        	    
            }
        } 
	}	*/
	
	public static void validateBlock4RuleBased(String input, String inBlock) {
	    // Compile the provided block pattern (e.g., to match Block 4 from the MT message)
	    final Pattern pattern = Pattern.compile(inBlock);
	    final Matcher matcher = pattern.matcher(input);

	    // If block is found in the input
	    if (matcher.find()) {
	        // Iterate through all matched groups (usually only one Block 4, but supports more)
	        for (int i = 0; i < matcher.groupCount(); i++) {
	            // Split matched block content line by line
	            String[] lines = matcher.group(i).split("\n");
	            List<String> ctr15InvalidFields = new ArrayList<>();
	            List<String> ctr9InvalidFields = new ArrayList<>();

	            // Regex to extract field tag (e.g., 53B) from lines like ":53B:"
	            Pattern getField = Pattern.compile("(?<=:)\\d{2}[A-Z]?(?=:)");

	            // Regex to detect invalid CTR15 patterns in account lines
	            Pattern ctr15Validation = Pattern.compile(
	                "^:\\d{2}[A-Z]?:\\/\\/CH\\s*$" + // Case 1: :53B://CH (no CH UID)
	                "|^:\\d{2}[A-Z]?:\\/\\/(?!CH)[A-Za-z]{1}.*$" + // Case 2: :53B://IN (only //CH is valid)
	                "|^:\\d{2}[A-Z]?:/C/\\s*$" + // Case 3: :53B:/C/ (no account)
	                "|^:\\d{2}[A-Z]?:/C\\s*$" + // Case 5: :53B:/C
	                "|^:\\d{2}[A-Z]?:/D/\\s*$" + // Case 4: :53B:/D/
	                "|^:\\d{2}[A-Z]?:/D\\s*$" + // Case 6: :53B:/D
	                "|^:\\d{2}[A-Z]?:/\\s*$" + // Case 7: :53B:/
	                "|^:\\d{2}[A-Z]?://\\s*$" + // Extra: :53B:// (no CH, no UID)
	                "|^:\\d{2}[A-Z]?:\\s*$"     // Extra: :53B: (empty value)
	            );
	            
	            // Regex to detect invalid CTR9 patterns in account lines
	            Pattern ctr9Validation = Pattern.compile(
	                "^:\\d{2}[A-Z]?:\\/\\/CH\\s*$" + // Case 1: :50A://CH (no CH UID)
	                "|^:\\d{2}[A-Z]?:/\\s*$" +  // Case 2: :50A:/
	                "|^:\\d{2}[A-Z]?://\\s*$" + // Case 3: :50A:// (no CH, no UID)
	                "|^:\\d{2}[A-Z]?:\\s*$"     // Case 4: :50A: (empty value)
	            );

	            // Initialize allowed CTR15 field tags (e.g., 52D,53A,53B,53D,54A,54D,55A,55D,56A,56C,56D,57A,57D,58A)
	            Set<String> ctr15Fields = new HashSet<>();
	            if (EnumUtils.isValidEnum(MtSequenceEnum.class, "RULESCTR15")) {
	                MtSequenceEnum mtRepeatEnum = MtSequenceEnum.valueOf("RULESCTR15");
	                ctr15Fields = mtRepeatEnum.sequences(); // Get valid field list from enum
	            }
	            
	            // Initialize allowed CTR9 field tags (e.g., 50A,50K,50F,59,59A,59F)
	            Set<String> ctr9Fields = new HashSet<>();
	            if (EnumUtils.isValidEnum(MtSequenceEnum.class, "RULESCTR9")) {
	                MtSequenceEnum mtRepeatEnum = MtSequenceEnum.valueOf("RULESCTR9");
	                ctr9Fields = mtRepeatEnum.sequences(); // Get valid field list from enum
	            }
	            
                for (int k = 0; k < lines.length; k++) {
                    String line = lines[k].trim(); // Clean line
                    Matcher fieldMatcher = getField.matcher(line); // Extract field tag
                    if (fieldMatcher.find()) {
	                    // Check If CTR15 field tags are present for validation && Check if this line has a valid CTR15 field tag
	                    if (!ctr15Fields.isEmpty() && ctr15Fields.contains(fieldMatcher.group(0))) {
	                        Matcher ruleMatcher = ctr15Validation.matcher(line);
	                        // If this field line matches any of the invalid CTR15 patterns
	                        if (ruleMatcher.find()) {
	                            ctr15InvalidFields.add("\t\t❌ InValid fields: " + line); // Record the violation
	                        }
	                    }
	                    // Check If CTR9 field tags are present for validation && Check if this line has a valid CTR9 field tag
	                    if (!ctr9Fields.isEmpty() && ctr9Fields.contains(fieldMatcher.group(0))) {
	                        Matcher ruleMatcher = ctr9Validation.matcher(line);
	                        // If this field line matches any of the invalid CTR9 patterns
	                        if (ruleMatcher.find()) {
	                            ctr9InvalidFields.add("\t\t❌ InValid fields: " + line); // Record the violation
	                        }
	                    }
                    }
                }

                // If any invalid CTR15 fields were found, log them with summary info
                if (!ctr15InvalidFields.isEmpty()) {
                    // Log header showing invalid fields in allowed CTR15 field set
                    String validStr = "\n\t====== Block-4 Found InValid Fields as per <T11013>,<T11008> in following list: " +
                        "52D,53A,53B,53D,54A,54D,55A,55D,56A,56C,56D,57A,57D,58A ====== \n";
                    validStr += String.join("\n", ctr15InvalidFields) + "\n\n";

                    // Append human-readable summary of error detection logic
            		String ctr15Msg = "\t\tSummary of <T11013>,<T11008> Invalid Patterns to Detect and Correct:\n" +
            			"\t\tCase\tDescription\t\tDetection Approach\n" +
            			"\t\t1\t\t:53B://CH\t\t//CH <ERROR-T11013> No account after //CH\n" +
            			"\t\t2\t\t:53B://IN\t\tOnly //CH is allowed, not //XX\n" +
            			"\t\t3\t\t:53B:/C/\t\t/C/ <ERROR-T11008> No account after /C/\n" +
            			"\t\t4\t\t:53B:/D/\t\t/D/ <ERROR-T11008> No account after /D/\n" +
            			"\t\t5\t\t:53B:/C\t\t\t/C <ERROR-T11008> No account after /C\n" +
            			"\t\t6\t\t:53B:/D\t\t\t/D <ERROR-T11008> No account after /D\n" +
            			"\t\t7\t\t:53B:/\t\t\tLone slash, no account data\n";

                    validStr += ctr15Msg;

                    // Finally log the CTR15 whole report
                    log.warning(validStr);
                }
                // If any invalid CTR9 fields were found, log them with summary info
                if (!ctr9InvalidFields.isEmpty()) {
                    // Log header showing invalid fields in allowed CTR9 field set
                    String validStr = "\n\t====== Block-4 Found InValid Fields as per <T11013>, in following list: " +
                        "50A,50F,50K,59,59A,59F ====== \n";
                    validStr += String.join("\n", ctr9InvalidFields) + "\n\n";

                    // Append human-readable summary of error detection logic
            		String ctr9Msg = "\t\tSummary of <T11013> Invalid Patterns to Detect and Correct:\n" +
            			"\t\tCase\tDescription\t\tDetection Approach\n" +
            			"\t\t1\t\t:50A://CH\t\t//CH <ERROR-T11013> No account after //CH\n" +
            			"\t\t2\t\t:50A://\t\t\t// without account\n" +
            			"\t\t3\t\t:50A:/\t\t\t/ Lone slash, no account data\n" +
            			"\t\t4\t\t:50A:\t\t\twithout account\n";

                    validStr += ctr9Msg;

                    // Finally log the CTR9 whole report
                    log.warning(validStr);
                }
	        }
	    }
	}
	
	public static boolean isDuplicateBlock(String input, String inBlock) {
		final Pattern pattern = Pattern.compile(inBlock);
	    final Matcher matcher = pattern.matcher(input);
	    int count = 0;
        while (matcher.find()) {
            count++;
        }
        boolean duplicate = false;
        if (count > 1) {
            //System.out.println("❌ Duplicate {3: block found: " + count + " times");
        	duplicate = true;
        }
        //else {
            //System.out.println("✅ {3: block is valid");
        //}
        return duplicate;
    }
	
	public static void checkDuplicateBlock3(String input, String inBlock) {
		final Pattern pattern = Pattern.compile(inBlock);
        final Matcher matcher = pattern.matcher(input);
        HashMap<String, String> keyvalue = new HashMap<String, String>();
        List<String> duplicateFields = new ArrayList<>();
        boolean duplicate = false;
        while(matcher.find()) {
        	if(!keyvalue.containsKey(matcher.group(1))) {
        		keyvalue.put(matcher.group(1), matcher.group(2));
        	}
        	else {
        		duplicate = true;
        		duplicateFields.add("\t\t❌ Duplicate Field/Tag >\t"+matcher.group(0));
        	}
        }
        //duplicateFields.forEach(System.out::println);
        if(!duplicateFields.isEmpty()) {
        	String duplicateStr = "\n\t====== Block-3 Found Duplicate Fields ====== \n";
        	duplicateStr = duplicateStr + String.join("\n", duplicateFields);
        	log.warning(duplicateStr);
        }
    }
	
	public static void validateAllBlock(String input) {
		if(isDuplicateBlock(input, block1Flag)){
			log.severe("Validation Error: Duplicate occurrence of mandatory Block 1 (Basic Header) detected. Only one instance of {1:} is allowed.");
		}
		if(!validateBlock(input, block1)) {
			String b1 = "\t{1:F01BANKBEBBAXXX0000000000}\n" +
					              "\t\tApplicationId: F\n" +
					              "\t\tServiceId: 01\n" +
					              "\t\tLTAddress: BANKBEBBAXXX\n" +
					              "\t\tSessionNumber: 000000\n" +
					              "\t\tSequenceNumber: 0000";
			log.warning("Block-1 (Basic Header) Mandatory feed does not match the below expected sample format: \n"+b1);
		}
		if(isDuplicateBlock(input, block2Flag)){
			log.severe("Validation Error: Duplicate occurrence of mandatory Block 2 (Application Header) detected. Only one instance of {2:} is allowed.");
		}
		boolean isBlock2OValid = validateBlock(input, block2O);
		boolean isBlock2IValid = validateBlock(input, block2I);
		if (!isBlock2IValid && !isBlock2OValid) {
		    // Both blocks are invalid, log block2I sample
		    String b2i = "\t{2:I103BANKDEFFXXXXN}\n" +
				                 "\t\tMessageType: 103\n" +
				                 "\t\tDestinationAddress: BANKDEFFXXXX\n" +
				                 "\t\tPriority: N";
		    String b2o = "\t{2:O1031124250107COBADEFFXXXX14072817002501071424N}\n" +
				                 "\t\tMessageType: 103\n" +
				                 "\t\tInputTime: 1124\n" +
				                 "\t\tSendersDate: 250107\n" +
				                 "\t\tLTAddress: COBADEFFXXXX\n" +
				                 "\t\tSessionNumber: 140728\n" +
				                 "\t\tSequenceNumber: 1700\n" +
				                 "\t\tOutputDate: 250107\n" +
				                 "\t\tOutputTime: 1424\n" +
				                 "\t\tPriority: N";
		    log.warning("Block-2 (Application Header) Either Inbound (or) Outbound Mandatory feed does not match the below expected sample formats: \n" + b2i +"\n"+b2o);
		}
		if(isDuplicateBlock(input, block3Flag)){
			log.severe("Validation Error: Duplicate occurrence of optional Block 3 (User Header) detected. Only one instance of {3:} is allowed.");
		}
		if(validateBlock(input, block3Flag)||validateBlock(input, block3Flag1)) {
			boolean isValidBlock = true;
			if(!validateBlock(input, block3)) {
				isValidBlock = false;
				String b3 = "\t{3:{113:PHOB}{108:CUSTOMREF01}{119:STP}{111:ABCD}{115:RTE123}{121:TXNREF99}}\n" +
							"\t\t\t(or)\n" +
		                    "\t{3:{113:ROMF}{108:MT103REF123}{119:REMIT}}\n" +
		                    "\t\t\t(or)\n" +
		                    "\t{3:{108:MT103REF123}}";
				log.warning("Block-3 (User Header) Optional feed does not match the below expected sample format: \n" + b3);
			}
			if(isValidBlock) {
				checkDuplicateBlock3(input, block3Dup);
			}
		}
		if(isDuplicateBlock(input, block4Flag)){
			log.severe("Validation Error: Duplicate occurrence of Mandatory Block 4 (Text Block) detected. Only one instance of {4:} is allowed.");
		}
		if(!validateBlock(input, block4)) {
			String block4valid = "\t{4:\n"
							    + "\t:20:REFERENCE12345\n"
							    + "\t:50A:/123456789\n"
							    + "\t:52B:/C/852963741\n"
							    + "\tBANK NAME\n"
							    + "\tLONDON, GB\n"
							    + "\t:52C:/159753852\n"
							    + "\tBANK NAME\n"
							    + "\tDUBAI, AE\n"
							    + "\t-}";
			log.warning("Block-4 (Text Block) is Mandatory and relevant Fields like :20: or :23: is also Mandatory, input does not match the below expected sample format: \n"+block4valid);
		}
		validateBlock4Loop(input, block4);
		validateBlock4RuleBased(input, block4);
		validateNonBraceContent(input, nonBraceContent);
		if(isDuplicateBlock(input, block5Flag)){
			log.severe("Validation Error: Duplicate occurrence of Optional Block 5 (Trailer Block) detected. Only one instance of {5:} is allowed.");
		}
		if(!validateBlock(input, block5)) {
			log.warning("Block-5 feed does not match the expected format");
		}
		if(!validateBlock(input, blockS)) {
			log.warning("Block-S feed does not match the expected format");
		}
	}
}
