package com.xmlpath;

import antlr.StringUtils;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class XmlDiffService {

	public static Map<String, String> parseXML(InputStream inputStream) throws Exception {
        Map<String, String> map = new LinkedHashMap<>();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setIgnoringElementContentWhitespace(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(inputStream);
        doc.getDocumentElement().normalize();
        traverse(doc.getDocumentElement(), "", map);
        return map;
    }

//    private static void traverse(Node node, String path, Map<String, String> map) {
//        if (node.getNodeType() == Node.ELEMENT_NODE) {
//            String newPath = path + "/" + node.getNodeName();
//
//            if (node.hasAttributes()) {
//                NamedNodeMap attrs = node.getAttributes();
//                for (int i = 0; i < attrs.getLength(); i++) {
//                    Node attr = attrs.item(i);
//                    if (!attr.getNodeName().contains("xmlns") && !attr.getNodeName().contains("xsi")) {
//                        map.put(newPath + "/@" + attr.getNodeName(), attr.getNodeValue());
//                    }
//                }
//            }
//
//            if (node.getChildNodes().getLength() == 1 &&
//                node.getFirstChild().getNodeType() == Node.TEXT_NODE) {
//                map.put(newPath, node.getTextContent().trim());
//            } else {
//                NodeList children = node.getChildNodes();
//                for (int i = 0; i < children.getLength(); i++) {
//                    traverse(children.item(i), newPath, map);
//                }
//            }
//        }
//    }
    
    private static void addToMapOrDuplicate(String key, String value, Map<String, String> map) {
    	key = key.replaceAll(".*:", "");
    	if (map.containsKey(key)) {
    		map.put(key+" <DUPLICATE>", value);
    	} else {
    		map.put(key, value);
    	}
    }
    
    private static void traverse(Node node, String path, Map<String, String> map) {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
        	String nodeName = node.getNodeName();
        	nodeName = nodeName.replaceAll(".*:", ""); //if Node like iscct:GrpHdr, then Remove iscct: alone and return GrpHdr.
            String newPath = path + "/" + nodeName;

            // Handle attributes
            if (node.hasAttributes()) {
                NamedNodeMap attrs = node.getAttributes();
                for (int i = 0; i < attrs.getLength(); i++) {
                    Node attr = attrs.item(i);
                    if (!attr.getNodeName().contains("xmlns") && !attr.getNodeName().contains("xsi")) {
                        //map.put(newPath + "/@" + attr.getNodeName(), attr.getNodeValue());
                        addToMapOrDuplicate(newPath + "/@" + attr.getNodeName(),attr.getNodeValue(),map);
                    }
                }
            }

            // Case 1: Node has a single text child
            if (node.getChildNodes().getLength() == 1 &&
                node.getFirstChild().getNodeType() == Node.TEXT_NODE) {

                String text = node.getTextContent().trim();
                //map.put(newPath, text);
                addToMapOrDuplicate(newPath,text,map);

            } else {
                // Check for empty element (<Tag/> or <Tag></Tag>)
                if (node.getChildNodes().getLength() == 0 ||
                    (node.getChildNodes().getLength() == 1 &&
                     node.getFirstChild().getNodeType() == Node.TEXT_NODE &&
                     node.getTextContent().trim().isEmpty())) {

                    // Put empty marker
                    //map.put(newPath, "");  // or "<EMPTY>" if you want to mark explicitly
                    addToMapOrDuplicate(newPath+" <EMPTY>","",map);
                }

                // Recurse children
                NodeList children = node.getChildNodes();
                for (int i = 0; i < children.getLength(); i++) {
                    traverse(children.item(i), newPath, map);
                }
            }
        }
    }
    
    


    public static List<XmlDiff> compare(Map<String, String> xml1, Map<String, String> xml2) {
        List<XmlDiff> diffs = new ArrayList<>();
        Set<String> allKeys = new TreeSet<>();
        allKeys.addAll(xml1.keySet());
        allKeys.addAll(xml2.keySet());
        int errCnt=0;
        int validCnt=0;
        for (String path : allKeys) {
            String val1 = xml1.getOrDefault(path, "");
            String val2 = xml2.getOrDefault(path, "");
            XmlDiff diff = new XmlDiff();
            diff.setXpath(path);
            diff.setXml1Value(val1);
            diff.setXml2Value(val2);
            if((val1.isEmpty() && val1.isEmpty() ) || !val1.equals(val2)){
            	errCnt++;
            	diff.setMatch(false);
            } else {
            	validCnt++;
            	diff.setMatch(val1.equals(val2));
            }
            
            //diff.setMatch(val1.equals(val2));
            diffs.add(diff);
        }
        //System.out.println("Inside validCnt ##############"+validCnt);
        float total = errCnt + validCnt;
        //System.out.println("Inside total ##############"+total);
        float percent = (validCnt/total)*100;
        //System.out.println("Inside percent ##############"+percent);
        //System.out.println("Inside errCnt ##############"+errCnt);
        //model.addAttribute("errorCount", errCnt);
        //model.addAttribute("passPercent", percent);
        return diffs;
    }
}
