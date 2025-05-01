package com.example.pnrgov.parser;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import javax.xml.transform.stream.StreamSource;

import org.milyn.Smooks;
import org.milyn.SmooksException;
import org.milyn.container.ExecutionContext;
import org.milyn.payload.JavaResult;
import org.milyn.payload.StringResult;
import org.xml.sax.SAXException;

import com.example.pnrgov.model.PnrGov;

/**
 * Parser for EDIFACT PNRGOV messages using Smooks
 */
public class PnrGovParser {

    private final Smooks smooks;

    public PnrGovParser() throws IOException, SAXException {
        // Initialize Smooks with the configuration file
        smooks = new Smooks("smooks-config.xml");
    }

    /**
     * Parse EDIFACT PNRGOV message to Java object
     * 
     * @param edifactMessage The EDIFACT message as string
     * @return PnrGov object representing the message
     * @throws SmooksException If parsing fails
     */
    public PnrGov parse(String edifactMessage) throws SmooksException {
        ExecutionContext executionContext = smooks.createExecutionContext();
        JavaResult result = new JavaResult();
        
        // Filter the message to the result
        smooks.filterSource(executionContext, new StreamSource(new StringReader(edifactMessage)), result);
        
        // Return the bound PnrGov object
        return (PnrGov) result.getBean("pnrGov");
    }
    
    /**
     * Transform EDIFACT PNRGOV message to XML
     * 
     * @param edifactMessage The EDIFACT message as string
     * @return XML representation of the message
     * @throws SmooksException If transformation fails
     */
    public String transformToXml(String edifactMessage) throws SmooksException {
        ExecutionContext executionContext = smooks.createExecutionContext();
        StringResult result = new StringResult();
        
        // Filter the message to the result
        smooks.filterSource(executionContext, new StreamSource(new StringReader(edifactMessage)), result);
        
        // Return the XML
        return result.getResult();
    }
    
    /**
     * Parse PNRGOV from a file
     * 
     * @param filePath Path to the EDIFACT file
     * @return PnrGov object representing the message
     * @throws IOException If file cannot be read
     * @throws SmooksException If parsing fails
     */
    public PnrGov parseFromFile(String filePath) throws IOException, SmooksException {
        try (FileInputStream fis = new FileInputStream(filePath);
             InputStreamReader reader = new InputStreamReader(fis, StandardCharsets.UTF_8)) {
            
            ExecutionContext executionContext = smooks.createExecutionContext();
            JavaResult result = new JavaResult();
            
            // Filter the message to the result
            smooks.filterSource(executionContext, new StreamSource(reader), result);
            
            // Return the bound PnrGov object
            return (PnrGov) result.getBean("pnrGov");
        }
    }
    
    /**
     * Close resources
     */
    public void close() {
        if (smooks != null) {
            smooks.close();
        }
    }
    
    /**
     * Example usage
     */
    public static void main(String[] args) {
        try {
            PnrGovParser parser = new PnrGovParser();
            
            // Example 1: Parse from string
            String edifactMessage = readExampleMessage();
            PnrGov pnrGov = parser.parse(edifactMessage);
            System.out.println("Parsed PNRGOV message: " + pnrGov);
            
            // Example 2: Transform to XML
            String xml = parser.transformToXml(edifactMessage);
            System.out.println("XML representation:\n" + xml);
            
            // Example 3: Parse from file
            PnrGov pnrGovFromFile = parser.parseFromFile("src/main/resources/pnrgov-example.edi");
            System.out.println("Parsed PNRGOV from file: " + pnrGovFromFile);
            
            parser.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static String readExampleMessage() {
        // This would typically read from a file or other source
        return "UNA:+.? '\n" +
               "UNB+IATB:1+AIRLINE+GOVERNMENT+230413:1145+1234567'\n" +
               "UNG+PNRGOV+AIRLINE+GOVERNMENT+230413:1145+1234567+UN+D:13B'\n" +
               "UNH+1+PNRGOV:13:1:IA+01'\n" +
               "MSG+:745'\n" +
               "ORG+1A:ABC123'\n" +
               "TVL+230413:1420+LHR+JFK+AA+123'\n" +
               "EQN+1'\n" +
               "SRC'\n" +
               "RCI+1A:ABCDEF'\n" +
               "DAT+700:230413'\n" +
               "FTI+ACH:12345678'\n" +
               "TIF+SMITH+JOHN:MR++1'\n" +
               "DOC+P:110:111+US+AB1234567+USA+230101+M+240228+SMITH+JOHN'\n" +
               "DTI+1:230413'\n" +
               "DTI+189:230413:1321'\n" +
               "FTI+SSR:DOCS AA HK1 P USA AB1234567 USA 770101 M 240228 SMITH JOHN'\n" +
               "IFT+4:8+TICKET NUMBER 12345678901234'\n" +
               "TDI+1+Y:7'\n" +
               "FDA+23+F:1:A'\n" +
               "UNT+19+1'\n" +
               "UNE+1+1234567'\n" +
               "UNZ+1+1234567'";
    }
}
