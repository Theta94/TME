package tme.model;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

// Implementation of singleton pattern
public final class XMLPreferences {
    private static File file = null;
    
    private XMLPreferences() { }
    
    // Create the file for the first time
    public static boolean createInstance() {
        // If file was already istantiated
        if (file != null)
            return false;
        
        // If file exists
        file = new File("preferences/preferences.xml");
        if (file.exists())
            return true;
        
        // If file dosen't exists
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            
            Element rootElement = doc.createElement("preferences");
            doc.appendChild(rootElement);
            
            Element connectionElement = doc.createElement("connection");
            rootElement.appendChild(connectionElement);
            
            Element uriElement = doc.createElement("uri");
            connectionElement.appendChild(uriElement);
            
            Element collectionElement = doc.createElement("collection");
            connectionElement.appendChild(collectionElement);
            
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);
        } catch (ParserConfigurationException | TransformerException ex) {
            return false;
        }
        
        return true;
    }
    
    // Set connection node inside the file with the given parameters
    public static void setConnectionNode(String URI, String collection) {
        if (file == null)
            return;
        
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            
            Element root = doc.getDocumentElement();
            NodeList rootChildrenNodes = root.getChildNodes();
            
            for (int i = 0; i < rootChildrenNodes.getLength(); i++) {
                Node currentNode = rootChildrenNodes.item(i);
                if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element currentElement = (Element)currentNode;
                    if (currentElement.getNodeName().equals("connection")) {
                        NodeList connectionChilds = currentElement.getChildNodes();
                        for (int j = 0; j < connectionChilds.getLength(); j++) {
                            Node connectionNode = connectionChilds.item(j);
                            if (connectionNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element connectionElement = (Element)connectionNode;
                                if (connectionElement.getNodeName().equals("uri"))
                                    connectionElement.setTextContent(URI);
                                else if (connectionElement.getNodeName().equals("collection"))
                                    connectionElement.setTextContent(collection);
                            }
                        }
                    }
                }
            }
            
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);
        } catch (ParserConfigurationException | SAXException | IOException | TransformerException ex) {

        }
    }
    
    // Get connection nodes inside the file
    public static String[] getConnectionNode() {
        if (file == null)
            return null;
        
        String[] ret = new String[2];
        
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file); 
            
            Element root = doc.getDocumentElement();
            NodeList rootChildrenNodes = root.getChildNodes();
            
            for (int i = 0; i < rootChildrenNodes.getLength(); i++) {
                Node currentNode = rootChildrenNodes.item(i);
                if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element currentElement = (Element)currentNode;
                    if (currentElement.getNodeName().equals("connection")) {
                        NodeList connectionChilds = currentElement.getChildNodes();
                        for (int j = 0; j < connectionChilds.getLength(); j++) {
                            Node connectionNode = connectionChilds.item(j);
                            if (connectionNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element connectionElement = (Element)connectionNode;
                                if (connectionElement.getNodeName().equals("uri"))
                                    ret[0] = connectionElement.getTextContent();
                                else if (connectionElement.getNodeName().equals("collection"))
                                    ret[1] = connectionElement.getTextContent();
                            }
                        }
                    }
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            
        }
        
        return ret;
    }
    
    public static File getFile() {
        return file;
    }
}
