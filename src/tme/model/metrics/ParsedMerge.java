package tme.model.metrics;

import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class ParsedMerge {
    private String fileName;
    private String type;
    private int version;
    private int test;
    private int unchanged;
    private int deleted;
    private int added;
    private int changed;
    private Node rootNode;
    
    public ParsedMerge(String fileName, int version, int test, Node rootNode, String type) {
        this.fileName = fileName;
        this.version = version;
        this.test = test;
        this.rootNode = rootNode;
        this.type = type;
        unchanged = 0;
        deleted = 0;
        added = 0;
        changed = 0;
    }
    
    private void parseCoperturaNode(Node node, String type) {
        // Check if the input node is an element
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            // The cast allows you to access to this node's informations
            Element element = (Element)node;
            // Check if this element is the right one ('copertura')
            if (element.getNodeName().equals("copertura")) {
                // Take all the childrens of this node
                NodeList coperturaChildrenNodes = node.getChildNodes();
                // Oterate all over the childrens
                for (int i = 0; i < coperturaChildrenNodes.getLength(); i++) {
                    // Take the current node
                    Node currentNode = coperturaChildrenNodes.item(i);
                    // Check if this node is an element
                    if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                        // Check the informations of this node
                        Element currentElement = (Element)currentNode;
                        // Check if this element is a counter
                        if (currentElement.getNodeName().equals("counter") && currentElement.getAttribute("type").equals(this.type.toUpperCase())) {
                            if (type.equals("Unchanged"))
                                unchanged = unchanged + Integer.parseInt(currentElement.getAttribute("covered"));
                            if (type.equals("Changed"))
                                changed = changed + Integer.parseInt(currentElement.getAttribute("covered"));
                            if (type.equals("Deleted"))
                                deleted = deleted + Integer.parseInt(currentElement.getAttribute("covered"));
                            if (type.equals("Added"))
                                added = added + Integer.parseInt(currentElement.getAttribute("covered"));
                        }
                    }
                }
            }
        }
    }
    
    private void parseMetodoNode(Node node) {
        String type = null;
        // Check if the input node is an element
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            // The cast allows you to access to this node's informations
            Element element = (Element)node;
            // Check if this element is the right one ('Metodo')
            if (element.getNodeName().equals("metodo")) {
                // Take all the childrens of this node ('copertura', 'movimentazione')
                NodeList metodoChildrenNodes = node.getChildNodes();
                // Iterate all over the childrens
                for (int i = 0; i < metodoChildrenNodes.getLength(); i++) {
                    // Take the current node
                    Node currentNode = metodoChildrenNodes.item(i);
                    // Check if this node is an element
                    if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                        // Check the informations of this node
                        Element currentElement = (Element)currentNode;
                        // Check if this element is a movimentazione
                        if (currentElement.getNodeName().equals("movimentazione"))
                            type = currentElement.getAttribute("status");
                        else if (currentElement.getNodeName().equals("copertura"))
                            // Parse the copertura
                            parseCoperturaNode(currentNode, type);
                    }
                }
            }
        }
    }
    
    public void parseXMLMerge() {
        if (rootNode == null)
            throw new RuntimeException("You must specify a node to open.");
        
        // Access to the root node
        Node root = rootNode.getFirstChild();
        // Take all the childrens of this node ('metodo')
        NodeList rootChildrenNodes = root.getChildNodes();
        // Iterate all over the childrens
        for (int i = 0; i < rootChildrenNodes.getLength(); i++) {
            // Take the current node
            Node currentNode = rootChildrenNodes.item(i);
            // Check if this node is an element
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                // Check the informations of this node
                Element currentElement = (Element)currentNode;
                // Check if this element is a metodo
                if (currentElement.getNodeName().equals("metodo"))
                    // Parse the method
                    parseMetodoNode(currentNode);
            }
        }
    }
    
    public static Node createMetricsNode(Set<ParsedMerge> parsedList, String project, int version, String type) {
        try {
            // Create the DOM structure
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();
            
            // Create the root node and append it to the DOM
            Element root = doc.createElement("Metrics");
            root.setAttribute("name_project", project);
            doc.appendChild(root);
            
            Element program = doc.createElement("program");
            program.setAttribute("version", Integer.toString(version));
            program.setAttribute("type", type);
            root.appendChild(program);
            for (ParsedMerge pm : parsedList) {
                Element parameters = doc.createElement("parameters");
                parameters.setAttribute("name", pm.getFileName());
                parameters.setAttribute("num_test", Integer.toString(pm.getTest()));
                parameters.setAttribute("C", Integer.toString(pm.getChanged()));
                parameters.setAttribute("U", Integer.toString(pm.getUnchanged()));
                parameters.setAttribute("D", Integer.toString(pm.getDeleted()));
                parameters.setAttribute("A", Integer.toString(pm.getAdded()));
                program.appendChild(parameters);
            }
            
            return doc.getFirstChild();
        } catch (ParserConfigurationException ex) {
            return null;
        }
    }
    
    public void print() {
        System.out.println(fileName + " " + type + " " + "v" + version + " " + test + " c: " + changed + " u: " + unchanged + " d: " + deleted + " added: " + added);
    }
    
    public int getAdded() {
        return added;
    }
    
    public int getDeleted() {
        return deleted;
    }
    
    public int getUnchanged() {
        return unchanged;
    }
    
    public int getChanged() {
        return changed;
    }
    
    public int getVersion() {
        return version;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public int getTest() {
        return test;
    }
}