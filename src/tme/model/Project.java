package tme.model;

import java.util.Comparator;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.xml.transform.OutputKeys;
import org.w3c.dom.Node;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;

// Implementation of singleton pattern
public class Project {
    private static String name = null;
    private static Map<String, Set<String>> reportVersionToTest = null;
    private static Map<String, Set<String>> mergeVersionToTest = null;
    private static Set<String> metricsSet = null;
    private static Set<String> churnSet = null;
    private static Set<String> prioritizationSet = null;
    private static Set<String> coverageSet = null;
    private static Collection reportCollection = null;
    private static Collection churnCollection = null;
    private static Collection mergeCollection = null;
    private static Collection metricsCollection = null;
    private static Collection prioritizationCollection = null;
    private static Collection coverageCollection = null;
    private static Collection compactReportCollection = null;
    // Constant
    public static final String REPORT_FOLDER_NAME = "report";
    
    private Project() { }
    
    public static void initProject(String name) {
        if (Project.name == null) {
            Project.name = name;
            
            Comparator<String> myStringComparator = new Comparator<String>() {
                @Override
                public int compare(String a, String b) {
                    return (a != null && b != null ? a.compareTo(b) : -1);
                }
            };
            Comparator<String> myVersionComparator = new Comparator<String>() {
                @Override
                public int compare(String a, String b) {
                    if (a != null && b != null) {
                        String verA = a.substring(0, a.indexOf(' '));
                        String verB = b.substring(0, b.indexOf(' '));
                        return Integer.compare(Integer.valueOf(verA.substring(1, verA.length())), Integer.valueOf(verB.substring(1, verB.length())));
                    }
                    return -1;
                }
            };
            
            reportVersionToTest = new TreeMap<>(myStringComparator);
            mergeVersionToTest = new TreeMap<>(myStringComparator);
            churnSet = new TreeSet<>(myVersionComparator);
            metricsSet = new HashSet<>();
            prioritizationSet = new HashSet<>();
            coverageSet = new HashSet<>();
            
            try {
                metricsCollection = DatabaseManager.getCollection(DBConnector.getURI() + DBConnector.getCollection().getName() + "/" + name + "/output/Metrics");
                prioritizationCollection = DatabaseManager.getCollection(DBConnector.getURI() + DBConnector.getCollection().getName() + "/" + name + "/output/Prioritization");
                coverageCollection = DatabaseManager.getCollection(DBConnector.getURI() + DBConnector.getCollection().getName() + "/" + name + "/output/Coverage");
                compactReportCollection = DatabaseManager.getCollection(DBConnector.getURI() + DBConnector.getCollection().getName() + "/" + name + "/input/report_compact");
            } catch (XMLDBException e) {
                
            }
        }
    }
    
    public static void buildMapReportVersionToTest() {
        if (name == null)
            return;
        
        try {
            // Open the right collection
            reportCollection = DatabaseManager.getCollection(DBConnector.getURI() + DBConnector.getCollection().getName() + "/" + name + "/input/" + REPORT_FOLDER_NAME);
            reportCollection.setProperty(OutputKeys.INDENT, "yes");
            // Recover all the files
            String[] reports = reportCollection.listResources();
            // Create the comparator for the set
            Comparator<String> testComparator = new Comparator<String>() {
                @Override
                public int compare(String a, String b) {
                    return Integer.compare(Integer.valueOf(a.substring(5)), Integer.valueOf(b.substring(5)));
                }
            };
            // Iterate all over the files
            for (String s : reports) {
                // Insert it into the map
                addReportToMap(s);
            }
            
        } catch (XMLDBException e) {

        }
    }
    
    public static void buildMapMergeVersionToTest() {
        if (name == null)
            return;
        
        try {
            // Open the right collection
            mergeCollection = DatabaseManager.getCollection(DBConnector.getURI() + DBConnector.getCollection().getName() + "/" + name + "/input/merge");
            mergeCollection.setProperty(OutputKeys.INDENT, "yes");
            // Recover all the files
            String[] merges = mergeCollection.listResources();
            // Create the comparator for the set
            Comparator<String> testComparator = new Comparator<String>() {
                @Override
                public int compare(String a, String b) {
                    return Integer.compare(Integer.valueOf(a.substring(5)), Integer.valueOf(b.substring(5)));
                }
            };
            
            // Iterate all over the files
            for (String s : merges) {
                // Insert it into the map
                addMergeToMap(s);
            }
            
        } catch (XMLDBException e) {
            
        }
    }
    
    private static void addMergeToMap(String merge) {
        // Create the comparator for the set
            Comparator<String> testComparator = new Comparator<String>() {
                @Override
                public int compare(String a, String b) {
                    return Integer.compare(Integer.valueOf(a.substring(5)), Integer.valueOf(b.substring(5)));
                }
            };
        // Check which version is it
        String version = extractVersion(merge);
        // Check if it exists in the map
        if (mergeVersionToTest.get(version) == null)
            mergeVersionToTest.put(version, new TreeSet<>(testComparator));
        // Create the new name of the report
        String newName = normalizeName(merge);
        // Insert it into the map
        mergeVersionToTest.get(version).add(newName);
    }
    
    private static void deleteReportFromMap(String version, String name) {
        if (reportVersionToTest.get(version) != null) {
            reportVersionToTest.get(version).remove(name);
        }
    }
    
    private static void deleteMergeFromMap(String version, String name) {
        if (mergeVersionToTest.get(version) != null) {
            mergeVersionToTest.get(version).remove(name);
        }
    }
    
    private static void deleteChurnFromSet(String versions) {
        if (churnSet.contains(versions))
            churnSet.remove(versions);
    }
    
    private static void addReportToMap(String report) {
        // Create the comparator for the set
            Comparator<String> testComparator = new Comparator<String>() {
                @Override
                public int compare(String a, String b) {
                    return Integer.compare(Integer.valueOf(a.substring(5)), Integer.valueOf(b.substring(5)));
                }
            };
        // Check which version is it
        String version = extractVersion(report);
        // Check if it exists in the map
        if (reportVersionToTest.get(version) == null)
            reportVersionToTest.put(version, new TreeSet<>(testComparator));
        // Create the new name of the report
        String newName = normalizeName(report);
        // Insert it into the map
        reportVersionToTest.get(version).add(newName);
    }
    
    public static void buildChurnSet() {
        if (name == null)
            return;
        
        try {
            // Open the right collection
            churnCollection = DatabaseManager.getCollection(DBConnector.getURI() + DBConnector.getCollection().getName() + "/" + name + "/input/churn");
            churnCollection.setProperty(OutputKeys.INDENT, "yes");
            // Recover all the files
            String[] churn = churnCollection.listResources();
            // Iterate all over the files
            for (String s : churn) {
                // Normalize the name
                String normalized = normalizeChurnName(s);
                // Insert it into the set
                churnSet.add(normalized);
            }
            
        } catch (XMLDBException e) {
            
        }
    }
    
    public static void buildMetricsSet() {
        if (name == null)
            return;
        
        try {
            metricsCollection.setProperty(OutputKeys.INDENT, "yes");
            String[] metrics = metricsCollection.listResources();
            for (String s : metrics) {
                String normalized = normalizeMetricsName(s);
                metricsSet.add(normalized);
            }
        } catch (XMLDBException e) {
            
        }
    }
    
    public static void buildCoverageSet() {
        if (name == null)
            return;
        
        try {
            coverageCollection.setProperty(OutputKeys.INDENT, "yes");
            String[] coverages = coverageCollection.listResources();
            for (String s : coverages) {
                String normalized = normalizeMetricsName(s);
                coverageSet.add(normalized);
            }
        } catch (XMLDBException e) {
            
        }
    }
    
    public static void buildPrioritizationSet() {
        if (name == null)
            return;
        
        try {
            prioritizationCollection.setProperty(OutputKeys.INDENT, "yes");
            String[] prio = prioritizationCollection.listResources();
            for (String s : prio) {
                String normalized = normalizeMetricsName(s);
                prioritizationSet.add(normalized);
            }
        } catch (XMLDBException e) {
            
        }
    }
    
    private static String normalizeMetricsName(String name) {
        return name.replace('_', ' ').substring(0, name.length()-4);
    }
    
    public static XMLResource getReportResource(String version, String name) {
        try {
            return (XMLResource)reportCollection.getResource(buildOldReportName(version, name));
        } catch (XMLDBException e) {
            return null;
        }
    }
    
    private static String buildOldReportName(String version, String name) {
        String oldName = "Test_";
        int i = 5;
        char[] nameChars = name.toCharArray();
        
        while (i < nameChars.length) {
            oldName = oldName + nameChars[i];
            i++;
        }
        
        oldName = oldName + "_" + version + ".xml";
        
        return oldName;
    }
    
    public static XMLResource getMergeResource(String version, String name, int mov) {
        String oldName = "Test_";
        int i = 5;
        char[] nameChars = name.toCharArray();
        
        while (i < nameChars.length) {
            oldName = oldName + nameChars[i];
            i++;
        }
        
        if (mov == 0)
            oldName = oldName + "_" + version + "_MergingChurnBack.xml";
        else
            oldName = oldName + "_" + version + "_MergingChurn.xml";
        
        try {
            return (XMLResource)mergeCollection.getResource(oldName);
        } catch (XMLDBException e) {
            return null;
        }
    }
    
    public static XMLResource getChurnResource(String versions) {
        try {
            return (XMLResource)churnCollection.getResource(buildOldChurnName(versions));
        } catch (XMLDBException e) {
            return null;
        }
    }
    
    public static XMLResource getMetricsResource(String prior) {
        try {
            return (XMLResource)metricsCollection.getResource(prior);
        } catch (XMLDBException e) {
            return null;
        }
    }
    
    public static XMLResource getPrioritizationResource(String prior) {
        try {
            return (XMLResource)prioritizationCollection.getResource(prior);
        } catch (XMLDBException e) {
            return null;
        }
    }
    
    public static XMLResource getCoverageResource(String cov) {
        try {
            return (XMLResource)coverageCollection.getResource(cov);
        } catch (XMLDBException e) {
            return null;
        }
    }
    
    private static String buildOldChurnName(String versions) {
        String oldName = "";
        int i = 0;
        char[] chars = versions.toCharArray();
        
        while (chars[i] != ' ') {
            oldName = oldName + chars[i];
            i++;
        }
        
        oldName = oldName + "_to_";
        
        i = i + 4;
        while (i < chars.length) {
            oldName = oldName + chars[i];
            i++;
        }
        
        return oldName + ".xml";
    }
    
    public static void addReportXMLResource(Node node, String name) {
        try {
            XMLResource res = (XMLResource)reportCollection.createResource(name, XMLResource.RESOURCE_TYPE);
            res.setContentAsDOM(node);
            reportCollection.storeResource(res);
            addReportToMap(name);
        } catch (XMLDBException e) {
            System.err.println("XML:DB Exception occured " + e.getMessage());
        }
    }
    
    public static void addMetricsXMLResource(Node node, String name) {
        try {
            XMLResource res = (XMLResource)metricsCollection.createResource(name, XMLResource.RESOURCE_TYPE);
            res.setContentAsDOM(node);
            metricsCollection.storeResource(res);
        } catch (XMLDBException e) {
            
        }
    }
    
    public static void addPrioritizationXMLResource(Node node, String name) {
        try {
            XMLResource res = (XMLResource)prioritizationCollection.createResource(name, XMLResource.RESOURCE_TYPE);
            res.setContentAsDOM(node);
            prioritizationCollection.storeResource(res);
        } catch (XMLDBException e) {
            
        }
    }
    
    public static void deleteMetricsXMLResource(String prio) {
        try {
            metricsCollection.removeResource(metricsCollection.getResource(prio));
        } catch (XMLDBException e) {
            
        }
    }
    
    public static void deletePrioritizationXMLResource(String prio) {
        try {
            prioritizationCollection.removeResource(prioritizationCollection.getResource(prio));
        } catch (XMLDBException e) {
            
        }
    }
    
    public static void deleteReportXMLResource(String version, String name) {
        try {
            reportCollection.removeResource(reportCollection.getResource(buildOldReportName(version, name)));
            deleteReportFromMap(version, name);
        } catch (XMLDBException e) {

        }
    }
    
    public static void deleteMergeXMLResource(String version, String name, int verTo) {
        try {
            mergeCollection.removeResource(mergeCollection.getResource(buildOldMergeName(version, name, verTo)));
            deleteMergeFromMap(version, name);
        } catch (XMLDBException e) {

        }
    }
    
    public static void deleteChurnXMLResource(String versions) {
        try {
            churnCollection.removeResource(churnCollection.getResource(buildOldChurnName(versions)));
            deleteChurnFromSet(versions);
        } catch (XMLDBException e) {

        }
    }
    
    public static void addChurnXMLResource(Node node, String name) {
        try {
            XMLResource res = (XMLResource)churnCollection.createResource(name, XMLResource.RESOURCE_TYPE);
            res.setContentAsDOM(node);
            churnCollection.storeResource(res);
            String normalized = normalizeChurnName(name);
            churnSet.add(normalized);
        } catch (XMLDBException e) {
            System.err.println("XML:DB Exception occured " + e.getMessage());
        }
    }
    
    public static void addMergeXMLResource(Node node, String version, String test, int movTo) {
        try {
            
            String name = buildOldMergeName(version, test, movTo);
            
            XMLResource res = (XMLResource)mergeCollection.createResource(name, XMLResource.RESOURCE_TYPE);
            res.setContentAsDOM(node);
            mergeCollection.storeResource(res);
            
            addMergeToMap(name);
            
        } catch (XMLDBException e) {
            System.err.println("XML:DB Exception occured " + e.getMessage());
        }
    }
    
    private static String buildOldMergeName(String version, String name, int movTo) {
        String oldName = "Test_";
        int i = 5;
        char[] nameChars = name.toCharArray();
        
        while (i < nameChars.length) {
            oldName = oldName + nameChars[i];
            i++;
        }
        
        oldName = oldName + "_" + version;
        
        if (movTo == 1)
            oldName = oldName + "_MergingChurn.xml";
        else
            oldName = oldName + "_MergingChurnBack.xml";
        
        return oldName;
    }
    
    private static String normalizeName(String name) {
        String normalized = "Test ";
        int counter = 0;
        char[] chars = name.toCharArray();
        
        for (int i = 0; i < chars.length && counter < 2; i++) {
            if (Character.isDigit(chars[i]))
                normalized = normalized + Character.toString(chars[i]);
            else if (chars[i] == '_')
                counter++;
        }
        
        return normalized;
    }
    
    public static int extractMovFromMetric(String metric) {
        char[] chars = metric.toCharArray();
        int cont = 0;
        String version = "";
        
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == ' ') {
                cont++;
                i++;
            }
            if (cont == 2)
                version = version + chars[i];
        }
        
        return (version.equals("Backward") ? 0 : 1);
    }
    
    public static String extractTypeFromMetric(String metric) {
        char[] chars = metric.toCharArray();
        int cont = 0;
        String type = "";
        
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == ' ')
                cont++;
            if (cont == 1 && chars[i] != ' ') {
                type = type + chars[i];
            }
        }
        
        return type;
    }
    
    private static String extractVersion(String name) {
        String extracted = "";
        int counter = 0, i = 0;
        char[] chars = name.toCharArray();
        
        for (i = 0; i < chars.length && counter < 2; i++) {
            if (chars[i] == '_')
                counter++;
        }
        
        extracted = extracted + Character.toString(chars[i]) + Character.toString(chars[i+1]); 

        return extracted;
    }
    
    private static String normalizeChurnName(String name) {
        String normalized = "";
        int counter = 0, i = 0, j = 0;
        char[] chars = name.toCharArray();
        
        while (chars[i] != '_') {
            normalized = normalized + Character.toString(chars[i]);
            i++;
        }
        
        normalized = normalized + " -> ";
        counter = 0;
        
        for (j = i+1; j < chars.length && counter < 1; j++)
            if (chars[j] == '_')
                counter++;
        
        while (chars[j] != '.') {
            normalized = normalized + Character.toString(chars[j]);
            j++;
        }
        
        return normalized;
    }
    
    public static String getName() {
        return name;
    }
    
    public static Map<String, Set<String>> getReportVersionToTest() {
        return reportVersionToTest;
    }
    
    public static Map<String, Set<String>> getMergeVersionToTest() {
        return mergeVersionToTest;
    }
    
    public static Set<String> getChurnSet() {
        return churnSet;
    }
    
    public static Set<String> getMetricsSet() {
        return metricsSet;
    }
    
    public static Set<String> getPrioritizationSet() {
        return prioritizationSet;
    }
    
    public static Set<String> getCoverageSet() {
        return coverageSet;
    }
    
    public static void cleanProject() {
        name = null;
        reportVersionToTest = null;
        mergeVersionToTest = null;
        churnSet = null;
        metricsSet = null;
        prioritizationSet = null;
        coverageSet = null;
        
        try {
            reportCollection.close();
            churnCollection.close();
            mergeCollection.close();
            metricsCollection.close();
            prioritizationCollection.close();
            coverageCollection.close();
            compactReportCollection.close();
        } catch (NullPointerException | XMLDBException e) {
            
        }
        
        reportCollection = null;
        churnCollection = null;
        mergeCollection = null;
        metricsCollection = null;
        prioritizationCollection = null;
        coverageCollection = null;
        compactReportCollection = null;
    }
    
    public static Collection getReportCollection() {
        return reportCollection;
    }
    
    public static Collection getCoverageCollection() {
        return coverageCollection;
    }
    
    public static Collection getCompactReportCollection() {
        return compactReportCollection;
    }
}