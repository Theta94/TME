package tme.model.metrics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class Metric {
    private int unchanged;
    private int deleted;
    private int added;
    private int changed;
    private int test;
    private float alfa;
    String name;
    
    public Metric(int u, int d, int a, int c, int t, float al, String na) {
        unchanged = u;
        deleted = d;
        added = a;
        changed = c;
        test = t;
        alfa = al;
        name = na;
    }
    
    public int getUnchanged() {
        return unchanged;
    }
    
    public int getDeleted() {
        return deleted;
    }
    
    public int getAdded() {
        return added;
    }
    
    public int getChanged() {
        return changed;
    }
    
    public int getTest() {
        return test;
    }
    
    public float getAlfa() {
        return alfa;
    }
    
    public String getName() {
        return name;
    }
}

class StandardForwardComparator implements Comparator<Metric> {
    @Override
    public int compare(Metric a, Metric b) {
        if (a.getChanged()+a.getDeleted()+a.getUnchanged() <= b.getChanged()+b.getDeleted()+b.getUnchanged())
            return 1;
        else
            return -1;
    }
}

class StandardBackwardComparator implements Comparator<Metric> {
    @Override
    public int compare(Metric a, Metric b) {
        if (a.getChanged()+a.getAdded()+a.getUnchanged() <= b.getChanged()+b.getAdded()+b.getUnchanged())
            return 1;
        else
            return -1;
    }
}

class GeneralForwardComparator implements Comparator<Metric> {
    @Override
    public int compare(Metric a, Metric b) {
        if ((a.getUnchanged())/((float)a.getChanged()+a.getUnchanged()+a.getDeleted()) <= (b.getUnchanged())/((float)b.getChanged()+b.getUnchanged()+b.getDeleted()))
            return 1;
        else 
            return -1;
    }
}

class GeneralBackwardComparator implements Comparator<Metric> {
    @Override
    public int compare(Metric a, Metric b) {
        if ((a.getUnchanged())/((float)a.getChanged()+a.getUnchanged()+a.getAdded()) <= (b.getUnchanged())/((float)b.getChanged()+b.getUnchanged()+b.getAdded()))
            return 1;
        else 
            return -1;
    }
}

class SpecificForwardComparator implements Comparator<Metric> {
    @Override
    public int compare(Metric a, Metric b) {
        if ((a.getDeleted()+a.getChanged())/((float)a.getChanged()+a.getUnchanged()+a.getDeleted()) <= (b.getDeleted()+b.getChanged())/((float)b.getChanged()+b.getUnchanged()+b.getDeleted()))
            return 1;
        else 
            return -1;
    }
}

class SpecificBackwardComparator implements Comparator<Metric> {
    @Override
    public int compare(Metric a, Metric b) {
        if ((a.getAdded()+a.getChanged())/((float)a.getChanged()+a.getUnchanged()+a.getAdded()) <= (b.getAdded()+b.getChanged())/((float)b.getChanged()+b.getUnchanged()+b.getAdded()))
            return 1;
        else 
            return -1;
    }
}

class LexForwardComparator implements Comparator<Metric> {
    @Override
    public int compare(Metric a, Metric b) {
        if (Math.abs((a.getChanged()+a.getDeleted())-(b.getChanged()+b.getDeleted())) < a.getAlfa() && a.getUnchanged() < b.getUnchanged() 
            || (b.getChanged()+b.getDeleted())-(a.getChanged()+a.getDeleted()) >= a.getAlfa())
            return 1;
        else 
            return -1;
    }
}

class LexBackwardComparator implements Comparator<Metric> {
    @Override
    public int compare(Metric a, Metric b) {
        if (Math.abs((a.getChanged()+a.getAdded())-(b.getChanged()+b.getAdded())) < a.getAlfa() && a.getUnchanged() < b.getUnchanged() 
            || (b.getChanged()+b.getAdded())-(a.getChanged()+a.getAdded()) >= a.getAlfa())
            return 1;
        else 
            return -1;
    }
}

public class ParsedMetric {
    private String fileName;
    private String type;
    private String sort;
    private String projName;
    public int mov;
    private int version;
    private float alfa;
    private Node rootNode;
    ArrayList<Metric> metricsSet;
    
    public ParsedMetric(String type, String sort, int version, float alfa, Node rootNode, int mov, String projName) {
        this.type = type;
        this.sort = sort;
        this.version = version;
        this.alfa = alfa;
        this.rootNode = rootNode;
        this.projName = projName;
        
        metricsSet = new ArrayList<>();
    }
    
    public Node parseXMLMetrics() {
        if (rootNode == null)
            throw new RuntimeException("You must specify a node to open.");
        
        // Access to the root node
        Node root = rootNode.getFirstChild();
        // Take all the childrens of this node ('Metrics')
        NodeList rootChildrenNodes = root.getChildNodes();
        // Iterate all over the childrens
        for (int i = 0; i < rootChildrenNodes.getLength(); i++) {
            // Take the current node
            Node programNode = rootChildrenNodes.item(i);
            // Check if this node is an element
            if (programNode.getNodeType() == Node.ELEMENT_NODE) {
                // Check the informations of this node
                Element programElement = (Element)programNode;
                // Check if this element is a metodo
                if (programElement.getNodeName().equals("program")) {
                    NodeList programChildrenNodes = programNode.getChildNodes();
                    for (int j = 0; j < programChildrenNodes.getLength(); j++) {
                        Node parameterNode = programChildrenNodes.item(j);
                        if (parameterNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element parameterElement = (Element)parameterNode;
                            if (parameterElement.getNodeName().equals("parameters")) {
                                metricsSet.add(
                                    new Metric(
                                        Integer.parseInt(parameterElement.getAttribute("U")),
                                        Integer.parseInt(parameterElement.getAttribute("D")),
                                        Integer.parseInt(parameterElement.getAttribute("A")),
                                        Integer.parseInt(parameterElement.getAttribute("C")),
                                        Integer.parseInt(parameterElement.getAttribute("num_test")),
                                        this.alfa,
                                        parameterElement.getAttribute("name")
                                    )
                                );
                            }
                        }
                    }
                }
            }
        }
        
        try {
            // Create the DOM structure
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();
            
            // Create the root node and append it to the DOM
            Element root2 = doc.createElement("Prioritization");
            root2.setAttribute("name_project", this.projName);
            doc.appendChild(root2);
            
            Element program2 = doc.createElement("program");
            program2.setAttribute("version", Integer.toString(this.version));
            program2.setAttribute("type", this.type);
            program2.setAttribute("sort", this.sort);
            if (this.sort.equals("Lex"))
                program2.setAttribute("alfa", Float.toString(this.alfa));
            root2.appendChild(program2);
            
            if (sort.equals("Standard") && mov == 1)
                Collections.sort(metricsSet, new StandardForwardComparator());
            else if (sort.equals("Standard") && mov == 0)
                Collections.sort(metricsSet, new StandardBackwardComparator());
            else if (sort.equals("General") && mov == 1)
                Collections.sort(metricsSet, new GeneralForwardComparator());
            else if (sort.equals("General") && mov == 0)
                Collections.sort(metricsSet, new GeneralBackwardComparator());
            else if (sort.equals("Specific") && mov == 1)
                Collections.sort(metricsSet, new SpecificForwardComparator());
            else if (sort.equals("Specific") && mov == 0)
                Collections.sort(metricsSet, new SpecificBackwardComparator());
            else if (sort.equals("Lex") && mov == 1)
                Collections.sort(metricsSet, new LexForwardComparator());
            else
                Collections.sort(metricsSet, new LexBackwardComparator());
            
            for (Metric m : metricsSet) {
                Element parameters2 = doc.createElement("parameters");
                parameters2.setAttribute("name", m.getName());
                parameters2.setAttribute("num_test", Integer.toString(m.getTest()));
                program2.appendChild(parameters2);
            }
            
            return doc.getFirstChild();
        } catch (ParserConfigurationException ex) {
            return null;
        }
    }
    
    public static ArrayList<Integer> getListFromPrio(Node prio) {
        ArrayList<Integer> tests = new ArrayList<>();
        tests.add(-1); // Start from 1
        
        Node root = prio.getFirstChild();
        NodeList rootChildrenNodes = root.getChildNodes();
        Node program = rootChildrenNodes.item(1);
        NodeList programChildrenNodes = program.getChildNodes();
        
        for (int i = 0; i < programChildrenNodes.getLength(); i++) {
            Node currentNode = programChildrenNodes.item(i);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                Element currentElement = (Element)currentNode;
                if (currentElement.getNodeName().equals("parameters"))
                    tests.add(Integer.parseInt(currentElement.getAttribute("num_test")));
            }
        }
        
        return tests;
    }
}