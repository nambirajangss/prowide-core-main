package com.xmlpath;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.*;

import javax.xml.parsers.*;
import java.io.*;
import java.util.*;

public class XMLDiffToExcel {

    public static void main(String[] args) throws Exception {
        String file1 = "\\\\192.168.2.203\\Study Materials\\MT-MX\\1-TESTING\\MT103\\MT_103_MSG0_EXPECTED_XML.xml";
        String file2 = "\\\\192.168.2.203\\Study Materials\\MT-MX\\1-TESTING\\MT103\\MT_103_MSG0_FINAL_XML.xml";

        File f = new File(file2);
        String fileName = f.getName();
        String nameWithoutExt = fileName.replaceFirst("[.][^.]+$", "");
        System.out.println("File name: " + nameWithoutExt);
        
        Map<String, String> xmlMap1 = parseXML(file1);
        Map<String, String> xmlMap2 = parseXML(file2);

        // Create Excel workbook
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("XML Comparison");

        // Header
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("XPath");
        header.createCell(1).setCellValue("XML1 Value");
        header.createCell(2).setCellValue("XML2 Value");

        // Styles for highlighting
        CellStyle greenStyle = workbook.createCellStyle();
        greenStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        greenStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle redStyle = workbook.createCellStyle();
        redStyle.setFillForegroundColor(IndexedColors.ROSE.getIndex());
        redStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Paths to skip
        List<String> skipPaths = Arrays.asList(
            "/Message/@xmlns",
            "/Message/@xmlns:head",
            "/Message/@xmlns:iscct",
            "/Message/@xmlns:xsi",
            "/Message/@xsi:schemaLocation",
            "/Message/AppHdr/@xmlns"
        );

        int rowIdx = 1;
        Set<String> allKeys = new TreeSet<>();
        allKeys.addAll(xmlMap1.keySet());
        allKeys.addAll(xmlMap2.keySet());

        for (String path : allKeys) {
            // Skip unwanted xmlns/xsi paths
            if (skipPaths.stream().anyMatch(path::equals)) {
                continue;
            }

            String val1 = xmlMap1.getOrDefault(path, "");
            String val2 = xmlMap2.getOrDefault(path, "");

            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(path);

            Cell cell1 = row.createCell(1);
            Cell cell2 = row.createCell(2);
            cell1.setCellValue(val1);
            cell2.setCellValue(val2);

            // Apply styles
            if (!val1.isEmpty() && !val2.isEmpty()) {
                if (val1.equals(val2)) {
                    cell1.setCellStyle(greenStyle);
                    cell2.setCellStyle(greenStyle);
                } else {
                    cell1.setCellStyle(redStyle);
                    cell2.setCellStyle(redStyle);
                }
            } else if((val1.isEmpty() && val2.isEmpty()) || (val1.isEmpty() || val2.isEmpty()) ){
            	cell1.setCellStyle(redStyle);
                cell2.setCellStyle(redStyle);
            }
        }

        // Save Excel
        try (FileOutputStream fos = new FileOutputStream(nameWithoutExt+".xlsx")) {
            workbook.write(fos);
        }
        workbook.close();

        System.out.println("✅ Excel file created: "+nameWithoutExt+".xlsx");
    }

    private static Map<String, String> parseXML(String filePath) throws Exception {
        Map<String, String> map = new LinkedHashMap<>();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setIgnoringElementContentWhitespace(true);

        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new File(filePath));
        doc.getDocumentElement().normalize();

        traverse(doc.getDocumentElement(), "", map);
        return map;
    }

    private static void traverse(Node node, String path, Map<String, String> map) {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            String newPath = path + "/" + node.getNodeName();

            if (node.hasAttributes()) {
                NamedNodeMap attrs = node.getAttributes();
                for (int i = 0; i < attrs.getLength(); i++) {
                    Node attr = attrs.item(i);
                    map.put(newPath + "/@" + attr.getNodeName(), attr.getNodeValue());
                }
            }

            if (node.getChildNodes().getLength() == 1 &&
                node.getFirstChild().getNodeType() == Node.TEXT_NODE) {
                map.put(newPath, node.getTextContent().trim());
            } else {
                NodeList children = node.getChildNodes();
                for (int i = 0; i < children.getLength(); i++) {
                    traverse(children.item(i), newPath, map);
                }
            }
        }
    }
}
