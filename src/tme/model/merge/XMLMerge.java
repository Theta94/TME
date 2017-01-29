package tme.model.merge;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Node;

/**
 * This class is designed to generate a merge file between a churnTool and JaCoCo reports. 
 * It uses the classes ParsedChurn and ParsedReport.
 * 
 * @see ParsedChurn
 * @see ParsedReport
 * @author Maurizio Cimino
 */
public final class XMLMerge {
    private final ParsedReport report;
    private final ParsedChurn churn;
    
    /**
     * It creates an XMLMerge object that will display the merge on the specified <code>fileName</code> path.
     * 
     * @param report the ParsedReport object representing the JaCoCo report
     * @param churn the ParsedChurn object representing the churnTool report
     * @param fileName the path to open the XML file to parse.
     */
    public XMLMerge(ParsedReport report, ParsedChurn churn) {
        this.report = report;
        this.churn = churn;
    }
    
    /**
     * This method join inside a new map the methods inside ReportMethod object and ChurnMethod object.
     * Two methods are equals if:
     *     - They stay inside the same package
     *     - They stay inside the same class
     *     - They have the same name
     *     - They have the same signature
     * 
     * @return the map that creates the corrispondence from JaCoCo report's methods and churnTool report's methods.
     */
    Map<ReportMethod, Set<ChurnMethod>> joinReportToChurn() {
        Map<ReportMethod, Set<ChurnMethod>> reportToChurn = new HashMap<>();
        
        // Iterate all over the packages
        for (ReportPackage rp : report.getPackageToClass().keySet())
            // Iterate all over the classes
            for (ReportClass rc : report.getPackageToClass().get(rp))
                // Iterate all over the methods inside this class
                for (ReportMethod rm : report.getClassToMethod().get(rc))
                    // Iterate all over the methods inside the churn
                    for (ChurnMethod cm : churn.getMethods()) {
                        // Check if this method is the right one to join      
                        if (cm.getName().equals(rm.getName()) && // Check the method's names
                            cm.getPackage().replace(".", "/").equals(rp.getName()) && // Check the packages's names
                            cm._getClass().equals(rc.getName().substring(rc.getName().lastIndexOf("/") + 1)) && // Check the classes's names
                            checkSignatures(rm.getSignature(), cm.getSignature())) { // Check the method's signatures
                            // Check if this report method already exists
                            if (reportToChurn.get(rm) == null)
                                // Allocate the set containing the methods in the churn
                                reportToChurn.put(rm, new HashSet<ChurnMethod>());
                            // Add this methods inside the map to create the corrispondence
                            reportToChurn.get(rm).add(cm);
                        }
                    }
        
        return reportToChurn;
    }
    
    /**
     * That is a necessary condition, but it is not a sufficient condition, to check if two methods are the same.
     * 
     * @param reportSignature the string containing the JaCoCo method's signature
     * @param churnSignature the string containing the churnTool methd's signature
     * @return true if the two signatures are the same, false otherwise.
     */
    static boolean checkSignatures(String reportSignature, String churnSignature) {
        // Split the signature string with ';' character
        String[] appReportSignature = reportSignature.split(";");
        String[] appChurnSignature = churnSignature.split(";");

        // The JaCoCo signature must be splitted more, this is the support matrix.
        String SJ[][] = new String[appReportSignature.length][];
        int k = 0;

        // For each string, split it more with '/' character and insert the result inside the main array
        for (int i = 0; i < appReportSignature.length; i++) {
          SJ[i] = appReportSignature[i].split("/");
          appReportSignature[i] = SJ[i][SJ[i].length-1];
        }

        // If the two result's arrays haven't the same length, return false
        if (appChurnSignature.length != appReportSignature.length)
          return false;

        // Check each 'parameter' inside the signature and return false if one is different
        while (k < appChurnSignature.length) {
            if (!appChurnSignature[k].equals(appReportSignature[k]))
                return false;
            k++;
        }

        return true;
    }
    
    /**
     * It creates a 'Metodo' element node.
     * 
     * @param doc contains needed methods.
     * @param jacocoSignature the signature of JaCoCo report.
     * @param churnSignature the signature of churnTool report.
     * @param methodName the name of the method.
     * @param packageName the name of the package.
     * @param className the name of the class.
     * @return an element representing a 'metodo' node.
     */
    private Element createElementMetodo(Document doc, String jacocoSignature, String churnSignature, String methodName, 
                                        String packageName, String className) {
        // Create the element
        Element metodo = doc.createElement("metodo");
        
        // Create the attributes and append them to metodo element
        metodo.setAttribute("joinCond", "true");
        metodo.setAttribute("segnaturaJacoco", jacocoSignature);
        metodo.setAttribute("segnaturaChur", churnSignature);
        metodo.setAttribute("nome", methodName);
        metodo.setAttribute("package", packageName);
        metodo.setAttribute("classe", className);
        
        // Return the element
        return metodo;
    }
    
    /**
     * It creates 'Movimentazione' element node.
     * 
     * @param doc contains needed methods.
     * @param cm the ChurnMethod object with stored data.
     * @return an element representing a 'Movimentazione' node.
     */
    private Element createElementMovimentazione(Document doc, ChurnMethod cm) {
        // Create the element
        Element movimentazione = doc.createElement("movimentazione");
        
        // Create the attributes and append them to movimentazione element
        movimentazione.setAttribute("lineeDiCodice", Float.toString(cm.getLinesOfCode()));
        movimentazione.setAttribute("lineeAggiunte", Integer.toString(cm.getAddedLines()));
        movimentazione.setAttribute("lineeCancellate", Integer.toString(cm.getDeletedLines()));
        movimentazione.setAttribute("lineeCambiate", Integer.toString(cm.getChangedLines()));
        movimentazione.setAttribute("complessità", Float.toString(cm.getComplexity()));
        movimentazione.setAttribute("status", cm.getStatus());
        movimentazione.setAttribute("segnatura", cm.getKey());
        
        // Return the element
        return movimentazione;
    }
    
    /**
     * It creates 'Counter' element node.
     * 
     * @param doc contains needed methods.
     * @param type the type attribute
     * @param missed the missed attribute
     * @param covered the covered attribute
     * @return an element representing a 'Counter' node.
     */
    private Element createElementCounter(Document doc, String type, int missed, int covered) {
        // Create the element
        Element counter = doc.createElement("counter");
        
        // Create the attributes and append them to counter element
        counter.setAttribute("missed", Integer.toString(missed));
        counter.setAttribute("covered", Integer.toString(covered));
        counter.setAttribute("type", type);
        
        // Return the element
        return counter;
    }
    
    /**
     * It creates 'Copertura' element node.
     * 
     * @param doc contains needed methods
     * @param rp the ReportPackage object with stored data.
     * @param rc the ReportClass object with stored data.
     * @param rm the ReportMethod object with stored data.
     * @return an element representing a 'Copertura' node.
     */
    private Element createElementCopertura(Document doc, ReportPackage rp, ReportClass rc, ReportMethod rm) {
        // Create the element
        Element copertura = doc.createElement("copertura");
        
        // Iterate all over the counters of the method
        for (Counter c : rm.getCounters()) {
            // Create the 'counter' element
            Element counter = createElementCounter(doc, c.getType().name(), c.getMissed(), c.getCovered());
            // Append the element to 'copertura' element
            copertura.appendChild(counter);
        }
        
        // Create the element CoperturaAggregataClasseAppartenenza
        Element coperturaAggregataClasseAppartenenza = doc.createElement("coperturaAggregataClasseAppartenenza");
        // Append the element to 'copertura' element
        copertura.appendChild(coperturaAggregataClasseAppartenenza);
        // Iterate all over the counters of the class
        for (Counter c : rc.getCounters()) {
            // Create the 'counter' element
            Element counter = createElementCounter(doc, c.getType().name(), c.getMissed(), c.getCovered());
            // Append the element to 'coperturaAggregataClasseAppartenenza' element
            coperturaAggregataClasseAppartenenza.appendChild(counter);
        }
        
        // Create the element CoperturaAggregataPackageAppartenenza
        Element coperturaAggregataPackageAppartenenza = doc.createElement("coperturaAggregataPackageAppartenenza");
        // Append the element to 'copertura' element
        copertura.appendChild(coperturaAggregataPackageAppartenenza);
        // Iterate all over the counters of the package
        for (Counter c : rp.getCounters()) {
            // Create the 'counter' element
            Element counter = createElementCounter(doc, c.getType().name(), c.getMissed(), c.getCovered());
            // Append the element to 'coperturaAggregataPackageAppartenenza' element
            coperturaAggregataPackageAppartenenza.appendChild(counter);
        }
        
        // Return the element
        return copertura;
    }
    
    /**
     * It creates an XML file containing the merge of a JaCoCo report file and churnTool report file. 
     */
    public Node createXMLMergeFile() {
        try {
            // Create the DOM structure
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();
            
            // Create the corrispondence from reported methods and churn methods
            Map<ReportMethod, Set<ChurnMethod>> reportToChurn = joinReportToChurn();
            
            // Create the root node and append it to the DOM
            Element root = doc.createElement("root_MERGING");
            doc.appendChild(root);
            
            // Iterate all over the packages
            for (ReportPackage rp : report.getPackageToClass().keySet())
                // Iterate all over the classes inside this package
                for (ReportClass rc : report.getPackageToClass().get(rp))
                    // Iterate all over the methods inside this class
                    for (ReportMethod rm : report.getClassToMethod().get(rc))
                        // Check if this method is available in the churn report too
                        if (reportToChurn.get(rm) != null) {
                            // For any method inside his set
                            for (ChurnMethod cm : reportToChurn.get(rm)) {
                                // Create the 'metodo' element
                                Element metodo = createElementMetodo(doc, rm.getSignature(), cm.getSignature(), rm.getName(), rp.getName(), rc.getName());
                                // Append the element to the root element
                                root.appendChild(metodo);
                                // Create the 'movimentazione' element
                                Element movimentazione = createElementMovimentazione(doc, cm);
                                // Append the element to the 'metodo' element
                                metodo.appendChild(movimentazione);
                                // Create the 'copertura' element
                                Element copertura = createElementCopertura(doc, rp, rc, rm);
                                // Append the element to the 'metodo' element
                                metodo.appendChild(copertura);
                            }
                        }
            
            return doc.getFirstChild();
            
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(XMLMerge.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    /**
     * It parses and saves the attribute 'key' for each method.
     * 
     * @param path contains the path of the file to parse
     * @return a set containing all the 'key' attributes into the file
     */
    private static List<String> testingParsedXMLMerge(String path) {
        List<String> keys = new ArrayList<>();
        
        try {
            // Open the XML merge file
            File merge = new File(path);
            if (!merge.exists())
                throw new RuntimeException("Can't find the specified XML file.");
            
            // Parse the file and create the DOM
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(merge); 
            
            // Access to the root node
            Element root = doc.getDocumentElement();
            // Take all the childrens of this node ('Metodo')
            NodeList rootChildrenNodes = root.getChildNodes();
            // Iterate all over the childrens
            for (int i = 0; i < rootChildrenNodes.getLength(); i++) {
                // Take the current node
                Node currentNode = rootChildrenNodes.item(i);
                // Check if this node is an element
                if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                    // Check the informations of this node
                    Element currentElement = (Element)currentNode;
                    // Check if this element is a 'metodo'
                    if (currentElement.getNodeName().equals("metodo")) {
                        // Take all the childrens of this node ('Movimentazione', 'Copertura')
                        NodeList currentNodeList = currentElement.getChildNodes();
                        // Iterate all over the childrens
                        for (int j = 0; j < currentNodeList.getLength(); j++) {
                            // Take the current node
                            Node currentNodeNew = currentNodeList.item(j);
                            // Check if this node is an element
                            if (currentNodeNew.getNodeType() == Node.ELEMENT_NODE) {
                                // Check the informations of this node
                                Element currentElementNew = (Element)currentNodeNew;
                                // Check if this element is a 'movimentazione'
                                if (currentElementNew.getNodeName().equals("movimentazione"))
                                    // Add this method to the list
                                    keys.add(currentElementNew.getAttribute("segnatura"));
                             }
                        }
                    }
                }
            } 
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(XMLMerge.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return keys;
    }
    
    /**
     * It checks if two merge are the same, and, if not, it prints which methods are missing
     * inside the <code>pathMerge2</code>.
     * 
     * @param pathMerge1 the path to open the first XML merge file to check.
     * @param pathMerge2 the path to open the second XML merge file to check.
     * @return true if the merges are the same, false otherwise.
     */
    static boolean checkMerges(String pathMerge1, String pathMerge2) {
        List<String> keysMerge1 = XMLMerge.testingParsedXMLMerge(pathMerge1);
        List<String> keysMerge2 = XMLMerge.testingParsedXMLMerge(pathMerge2);
        
        for (String s : keysMerge1)
            if (!keysMerge2.contains(s))
                System.out.println(s);
        
        return keysMerge1.size() == keysMerge2.size();
    }
}

/*class Test {
    
    public static void main(String[] args) {
        ParsedReport pr = new ParsedReport("C:\\Users\\Maurizio\\Desktop\\Test_10_Ant_v3.xml");
        pr.parseXMLReport();
        //System.out.println(pr);
        
        ParsedChurn pc = new ParsedChurn("C:\\Users\\Maurizio\\Desktop\\ant_v2_to_v3.xml");
        pc.parseXMLChurn();
        //System.out.println(pc);
        
       XMLMerge merge = new XMLMerge(pr, pc, "C:\\Users\\Maurizio\\Desktop\\test_3_merge.xml");
       merge.createXMLMergeFile();
       System.out.println(XMLMerge.checkMerges("C:\\Users\\Maurizio\\Desktop\\test_3_merge.xml", "C:\\Users\\Maurizio\\Desktop\\Test_10_Ant_v3_MergingChurnBack.xml"));
    }
}*/