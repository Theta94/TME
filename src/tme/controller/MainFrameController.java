package tme.controller;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.dom.DOMSource;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.CompiledExpression;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XQueryService;
import tme.model.DBConnector;

import tme.model.XMLPreferences;
import tme.model.metrics.ParsedMerge;
import tme.model.Project;
import tme.model.XQueryDB;
import tme.model.metrics.ParsedMetric;
import tme.view.MainFrame;

// Handle MainForm events
public final class MainFrameController {
    private final MainFrame mainFrame;  
    
    public MainFrameController(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }
    
    public void start() {
        // Initialize mainFrame components
        mainFrame.getNewProjectItem().setEnabled(false);
        mainFrame.getOpenProjectItem().setEnabled(false);
        mainFrame.getCloseProjectItem().setEnabled(false);
        mainFrame.getTabbedPane().setVisible(false);
        
        mainFrame.getMetricsTypeCombo().addItem("Instruction");
        mainFrame.getMetricsTypeCombo().addItem("Line");
        mainFrame.getMetricsTypeCombo().addItem("Method");
        mainFrame.getMetricsTypeCombo().addItem("Complexity");
        
        mainFrame.getAlfaParam().setEnabled(false);
        
        mainFrame.getSortCombo().addItem("Standard");
        mainFrame.getSortCombo().addItem("General");
        mainFrame.getSortCombo().addItem("Specific");
        mainFrame.getSortCombo().addItem("Lex");
        
        mainFrame.getCovType().addItem("Instruction");
        mainFrame.getCovType().addItem("Line");
        mainFrame.getCovType().addItem("Method");
        mainFrame.getCovType().addItem("Complexity");
        
        mainFrame.getCovMov().addItem("Forward");
        mainFrame.getCovMov().addItem("Backward");
        
        // Show mainFrame
        mainFrame.setVisible(true);
        
        // Events management
        mainFrame.addWindowListener(mainFrameOpenedFirstTimeEvent());
        mainFrame.getReportVersionList().addListSelectionListener(mainFrameElementListSelectedEvent());
        mainFrame.getTabbedPane().addChangeListener(mainFrameTabReportSelectedEvent());
        mainFrame.getCloseProjectItem().addActionListener(mainFrameClickCloseProjectItemEvent());
        mainFrame.getReportTestList().addListSelectionListener(mainFrameTestListSelectedEvent());
        mainFrame.getChurnList().addListSelectionListener(mainFrameChurnListSelectedEvent());
        mainFrame.getAddReportBtn().addActionListener(mainFrameClickAddReportBtnEvent());
        mainFrame.getAddChurnBtn().addActionListener(mainFrameClickAddChurnBtnEvent());
        mainFrame.getTabbedPane().addChangeListener(mainFrameTabChurnSelectedEvent());
        mainFrame.getMergeVersionList().addListSelectionListener(mainFrameMergeElementListSelectedEvent());
        mainFrame.getMergeTestList().addListSelectionListener(mainFrameMergeTestListSelectedEvent());
        mainFrame.getForwardRadioBtn().addActionListener(mainFrameClickRadioBtnEvent());
        mainFrame.getBackwardRadioBtn().addActionListener(mainFrameClickRadioBtnEvent());
        mainFrame.getDelReportBtn().addActionListener(mainFrameClickDelReportBtnEvent());
        mainFrame.getDelChurnBtn().addActionListener(mainFrameClickDelChurnBtnEvent());
        mainFrame.getDelMergeBtn().addActionListener(mainFrameClickDelMergeBtnEvent());
        mainFrame.getTabbedPane().addChangeListener(mainFrameTabMetricsSelectedEvent());
        mainFrame.getMetricsCalculateSelBtn().addActionListener(mainFrameClickMetricsCalculateSelEevent());
        mainFrame.getMetricsCalculateAllBtn().addActionListener(mainFrameClickMetricsCalculateAllEevent());
        mainFrame.getMetricsCalculateMissBtn().addActionListener(mainFrameClickMetricsCalculateMissingEevent());
        mainFrame.getSortCombo().addActionListener(mainFrameSelectSortEvent());
        mainFrame.getTabbedPane().addChangeListener(mainFrameTabPrioritizationSelectedEvent());
        mainFrame.getPrioCalculateSelBtn().addActionListener(mainFrameClickPrioritizationCalculateSelectedEevent());
        mainFrame.getPrioList().addListSelectionListener(mainFramePrioListSelectedEvent());
        mainFrame.getMetricsList().addListSelectionListener(mainFrameMetricsListSelectedEvent());
        mainFrame.getDistBtn().addActionListener(mainFrameClickCalculateBtnEvent());
        mainFrame.getTabbedPane().addChangeListener(mainFrameTabCoverageSelectedEvent());
        mainFrame.getCovBtn().addActionListener(mainFrameClickCovBtnEvent());
        mainFrame.getCompRepBtn().addActionListener(mainFrameClickCompRepBtnEvent());
        mainFrame.getCoverageList().addListSelectionListener(mainFrameCoverageElementListSelectedEvent());
    }
    
    
    private ListSelectionListener mainFrameCoverageElementListSelectedEvent() {
        return new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                coverageDisplaySelectedCoverage(mainFrame.getCoverageList().getSelectedValue());
            }
        };
    }
    
    public ActionListener mainFrameClickCovBtnEvent() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                progressCoverage().execute();
            }
        };
    }
    
    private SwingWorker<Void,Void> progressCoverage() {
        return new SwingWorker<Void,Void>() {
            @Override
            public Void doInBackground() {
                mainFrame.getCovProgressBar().setValue(0);
                mainFrame.getCovProgressBar().setIndeterminate(true);
                mainFrame.getCovProgressBar().setString("Covering...");
                calculateCoverage();
                return null;
            }
            
            @Override
            public void done() {
                mainFrame.getCovProgressBar().setIndeterminate(false);
                mainFrame.getCovProgressBar().setString("Done");
            }
        }; 
    }
    
    private void calculateCoverage() {
        String version = mainFrame.getCovVersion().getSelectedItem().toString();
        String type = mainFrame.getCovType().getSelectedItem().toString();
        String mov = mainFrame.getCovMov().getSelectedItem().toString();
        String fileName, sorts[] = {"Standard", "General", "Specific", "Lex"};
        XMLResource res[] = new XMLResource[4];
        boolean flag = false;

        for (int i = 0; i < 4; i++) {
            fileName = version + "_" + sorts[i] + "_" + type + "_" + mov + ".xml";
            res[i] = Project.getPrioritizationResource(fileName);
        }

        for (int i = 0; i < 4; i++)
            if (res[i] == null)
                flag = true;

        if (flag)
            JOptionPane.showMessageDialog(null, "The selected prioritization is missing on some metrics.", "Error", JOptionPane.ERROR_MESSAGE);
        else {
            try {
                XQueryService xqs = (XQueryService)DatabaseManager.getCollection(DBConnector.getURI() + DBConnector.getCollection().getName()).getService("XQueryService", "1.0");
                xqs.setProperty(OutputKeys.INDENT, "yes");
                xqs.setProperty(OutputKeys.ENCODING, "UTF-8");

                xqs.declareVariable("project", Project.getName());
                xqs.declareVariable("version", version);
                xqs.declareVariable("type", type);
                xqs.declareVariable("movimentation", mov);
                CompiledExpression compiled = xqs.compile(XQueryDB.calculate_prioritization_coverage);
                xqs.execute(compiled);

                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(null, "Coverage has been calculated!", "Done", JOptionPane.INFORMATION_MESSAGE);
                Project.getCoverageSet().add(version + " " + type + " " + mov);
            } catch (XMLDBException exc) {
                JOptionPane.showMessageDialog(null, "An error occured. Try to extract the compact reports before the coverage reports.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public ActionListener mainFrameClickCompRepBtnEvent() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    XQueryService xqs = (XQueryService)DatabaseManager.getCollection(DBConnector.getURI() + DBConnector.getCollection().getName()).getService("XQueryService", "1.0");
                    xqs.setProperty(OutputKeys.INDENT, "yes");
                    xqs.setProperty(OutputKeys.ENCODING, "UTF-8");

                    xqs.declareVariable("project", Project.getName());
                    CompiledExpression compiled = xqs.compile(XQueryDB.calculate_report_compact);
                    xqs.execute(compiled);
                    
                    Toolkit.getDefaultToolkit().beep();
                    JOptionPane.showMessageDialog(null, "Compact reports have been extracted!", "Done", JOptionPane.INFORMATION_MESSAGE);
                    mainFrame.getCompRepBtn().setEnabled(false);
                } catch (XMLDBException ex) {
                    JOptionPane.showMessageDialog(null, "It was impossible to extract the compact reports.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
    }
    
    public ActionListener mainFrameClickCalculateBtnEvent() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                if (mainFrame.getPrioList().getSelectedValuesList().size() != 2) {
                    JOptionPane.showMessageDialog(null, "You must select two prioritizations.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                int j = 0;
                XMLResource[] res = new XMLResource[2];
                for (String s : mainFrame.getPrioList().getSelectedValuesList()) {
                    res[j] = Project.getPrioritizationResource(s.replace(' ', '_') + ".xml");
                    j++;
                }
                
                try {
                    ArrayList<Integer> tests1 = ParsedMetric.getListFromPrio(res[0].getContentAsDOM());
                    ArrayList<Integer> tests2 = ParsedMetric.getListFromPrio(res[1].getContentAsDOM());

                    double sum = 0;
                    for (int i = 1; i < tests1.size(); i++)
                        sum = sum + Math.abs(i - tests2.indexOf(tests1.get(i)));
                    sum = sum * ((2)/(Math.pow(tests1.size(), 2)));

                    JOptionPane.showMessageDialog(null, "The distance between the two priorizations is " + sum, "Done", JOptionPane.INFORMATION_MESSAGE);
                } catch (XMLDBException exc) {
                    
                }
            }
        };
    }
    
    public ActionListener mainFrameSelectSortEvent() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mainFrame.getSortCombo().getSelectedItem().equals("Lex"))
                    mainFrame.getAlfaParam().setEnabled(true);
                else {
                    mainFrame.getAlfaParam().setText("");
                    mainFrame.getAlfaParam().setEnabled(false);
                }
            }
        };
    }
    
    private ChangeListener mainFrameTabMetricsSelectedEvent() {
        return new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (mainFrame.getTabbedPane().getSelectedIndex() == 3) {
                    // Take the map
                    Map<String, Set<String>> mergeVersionToTest = Project.getMergeVersionToTest();
                    // Create the version model
                    DefaultListModel<String> mergeModel = new DefaultListModel<>();
                    
                    for (String version : mergeVersionToTest.keySet())
                        mergeModel.addElement(version);
                    
                    mainFrame.getMergeListMetrics().setModel(mergeModel);
                    mainFrame.getMergeListMetrics().setSelectedIndex(0);
                    
                    Set<String> metricsSet = Project.getMetricsSet();
                    DefaultListModel<String> metricsModel = new DefaultListModel<>();
                    DefaultListModel<String> prioMetricsModel = new DefaultListModel<>();
                    
                    for (String version : metricsSet) {
                        metricsModel.addElement(version);
                        prioMetricsModel.addElement(version);
                    }
                    
                    mainFrame.getMetricsList().setModel(metricsModel);
                    mainFrame.getMetricsList().setSelectedIndex(0);
                    
                    mainFrame.getPrioListMetrics().setModel(prioMetricsModel);
                    mainFrame.getPrioListMetrics().setSelectedIndex(0);
                }
            }
        };
    }
    
    private ChangeListener mainFrameTabPrioritizationSelectedEvent() {
        return new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (mainFrame.getTabbedPane().getSelectedIndex() == 4) {
                    Set<String> metricsSet = Project.getMetricsSet();
                    Set<String> prioritizationSet = Project.getPrioritizationSet();
                    DefaultListModel<String> metricsModel = new DefaultListModel<>();
                    DefaultListModel<String> prioModel = new DefaultListModel<>();
                    
                    for (String version : metricsSet)
                        metricsModel.addElement(version);
                    
                    for (String prio : prioritizationSet)
                        prioModel.addElement(prio);
                    
                    mainFrame.getPrioListMetrics().setModel(metricsModel);
                    mainFrame.getPrioListMetrics().setSelectedIndex(0);
                    
                    mainFrame.getPrioList().setModel(prioModel);
                    mainFrame.getPrioList().setSelectedIndex(0);
                }
            }
        };
    }
    
    private ChangeListener mainFrameTabReportSelectedEvent() {
         return new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (mainFrame.getTabbedPane().getSelectedIndex() == 0) {
                    try {
                        if (Project.getCompactReportCollection().listResources().length > 0)
                            mainFrame.getCompRepBtn().setEnabled(false);
                        else
                            mainFrame.getCompRepBtn().setEnabled(true);
                    } catch (XMLDBException ex) {
                        
                    }
                }
            }
        };
    }
    
    private ChangeListener mainFrameTabCoverageSelectedEvent() {
        return new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (mainFrame.getTabbedPane().getSelectedIndex() == 5) {
                    Set<String> coverageSet = Project.getCoverageSet();
                    DefaultListModel<String> coverageModel = new DefaultListModel<>();
                    
                    for (String cov : coverageSet)
                        coverageModel.addElement(cov);
                    
                    mainFrame.getCoverageList().setModel(coverageModel);
                    mainFrame.getCoverageList().setSelectedIndex(0);
                    
                    try {
                        if (Project.getCompactReportCollection().listResources().length > 0)
                            mainFrame.getCovBtn().setEnabled(true);
                        else
                            mainFrame.getCovBtn().setEnabled(false);
                    } catch (XMLDBException ex) {
                        
                    }
                }
            }
        };
    }
    
    private ActionListener mainFrameClickMetricsCalculateSelEevent() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                progressSelectedMetrics().execute();
            }
        };
    }
    
    private ActionListener mainFrameClickMetricsCalculateAllEevent() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                progressAllMetrics().execute();
            }
        };
    }
    
    private ActionListener mainFrameClickMetricsCalculateMissingEevent() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                progressMissingMetrics().execute();
            }
        };
    }
    
    private ActionListener mainFrameClickPrioritizationCalculateSelectedEevent() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                progressSelectedPrioritization().execute();
            }
        };
    }
    
    private SwingWorker<Void,Void> progressAllMetrics() {
        return new SwingWorker<Void,Void>() {
            @Override
            public Void doInBackground() {
                mainFrame.getMetricsProgressBar().setValue(0);
                mainFrame.getMetricsProgressBar().setIndeterminate(true);
                mainFrame.getMetricsProgressBar().setString("Extracting metrics...");
                extractAllMetrics();
                return null;
            }
            
            @Override
            public void done() {
                mainFrame.getMetricsProgressBar().setIndeterminate(false);
                mainFrame.getMetricsProgressBar().setString("Done");
            }
        }; 
    }
    
    private SwingWorker<Void,Void> progressSelectedMetrics() {
        return new SwingWorker<Void,Void>() {
            @Override
            public Void doInBackground() {
                mainFrame.getMetricsProgressBar().setValue(0);
                mainFrame.getMetricsProgressBar().setIndeterminate(true);
                mainFrame.getMetricsProgressBar().setString("Extracting metrics...");
                extractSelectedMetrics();
                return null;
            }
            
            @Override
            public void done() {
                mainFrame.getMetricsProgressBar().setIndeterminate(false);
                mainFrame.getMetricsProgressBar().setString("Done");
            }
        }; 
    }
    
    private SwingWorker<Void,Void> progressMissingMetrics() {
        return new SwingWorker<Void,Void>() {
            @Override
            public Void doInBackground() {
                mainFrame.getMetricsProgressBar().setValue(0);
                mainFrame.getMetricsProgressBar().setIndeterminate(true);
                mainFrame.getMetricsProgressBar().setString("Extracting metrics...");
                extractMissingMetrics();
                return null;
            }
            
            @Override
            public void done() {
                mainFrame.getMetricsProgressBar().setIndeterminate(false);
                mainFrame.getMetricsProgressBar().setString("Done");
            }
        }; 
    }
    
    private SwingWorker<Void,Void> progressSelectedPrioritization() {
        return new SwingWorker<Void,Void>() {
            @Override
            public Void doInBackground() {
                mainFrame.getPrioProgressBar().setValue(0);
                mainFrame.getPrioProgressBar().setIndeterminate(true);
                mainFrame.getPrioProgressBar().setString("Extracting prioritizations...");
                extractSelectedPrioritization();
                return null;
            }
            
            @Override
            public void done() {
                mainFrame.getPrioProgressBar().setIndeterminate(false);
                mainFrame.getPrioProgressBar().setString("Done");
            }
        }; 
    }
    
    private void extractMissingMetrics() {
       int mov = mainFrame.getMetricsBackwardRadioBtn().isSelected() ? 0 : 1;
        Set<ParsedMerge> parsedSet = new HashSet<>();
        String type = mainFrame.getMetricsTypeCombo().getSelectedItem().toString();
        ParsedMerge pm;
        boolean flag1 = false, flag2 = false;
        
        for (int i = 0; i < mainFrame.getMergeListMetrics().getModel().getSize(); i++) {
            String actual = mainFrame.getMergeListMetrics().getModel().getElementAt(i);
            if (!Project.getMetricsSet().contains(actual + " " + type + (mov == 1 ? " Forward" : " Backward")) &&
                Project.getMergeVersionToTest().get(actual) != null) {
                for (String test : Project.getMergeVersionToTest().get(actual)) {
                    try {
                        XMLResource res = Project.getMergeResource(actual, test, mov);
                        if (res == null) {
                            break;
                        } else {
                           pm = new ParsedMerge(res.getDocumentId(), Integer.parseInt(actual.substring(1)), Integer.parseInt(test.substring(5)), res.getContentAsDOM(), type);
                           pm.parseXMLMerge();
                           parsedSet.add(pm);
                           flag1 = true;
                           flag2 = true;
                        }
                    } catch (XMLDBException exc) {

                    }
                }

                if (flag2) {
                    Node metrics = ParsedMerge.createMetricsNode(parsedSet, Project.getName(), Integer.parseInt(actual.substring(1)), type);
                    Project.addMetricsXMLResource(metrics, actual + "_" + type + (mov == 0 ? "_Backward.xml" : "_Forward.xml"));
                    Project.getMetricsSet().add(actual + " " + type + (mov == 0 ? " Backward" : " Forward"));
                }
            }

            flag2 = false;
            parsedSet = new HashSet<>();
        }

        Toolkit.getDefaultToolkit().beep();
        JOptionPane.showMessageDialog(null, "Metrics have been extracted.", "Done", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void extractAllMetrics() {
        int mov = mainFrame.getMetricsBackwardRadioBtn().isSelected() ? 0 : 1;
        Set<ParsedMerge> parsedSet = new HashSet<>();
        String type = mainFrame.getMetricsTypeCombo().getSelectedItem().toString();
        ParsedMerge pm;
        boolean flag1 = false, flag2 = false;
        
        for (int i = 0; i < mainFrame.getMergeListMetrics().getModel().getSize(); i++) {
            String actual = mainFrame.getMergeListMetrics().getModel().getElementAt(i);
            if (Project.getMergeVersionToTest().get(actual) != null) {
                for (String test : Project.getMergeVersionToTest().get(actual)) {
                    try {
                        XMLResource res = Project.getMergeResource(actual, test, mov);
                        if (res == null) {
                            break;
                        } else {
                           pm = new ParsedMerge(res.getDocumentId(), Integer.parseInt(actual.substring(1)), Integer.parseInt(test.substring(5)), res.getContentAsDOM(), type);
                           pm.parseXMLMerge();
                           parsedSet.add(pm);
                           flag1 = true;
                           flag2 = true;
                        }
                    } catch (XMLDBException exc) {

                    }
                }

                if (flag2) {
                    Node metrics = ParsedMerge.createMetricsNode(parsedSet, Project.getName(), Integer.parseInt(actual.substring(1)), type);
                    Project.addMetricsXMLResource(metrics, actual + "_" + type + (mov == 0 ? "_Backward.xml" : "_Forward.xml"));
                    Project.getMetricsSet().add(actual + " " + type + (mov == 0 ? " Backward" : " Forward"));
                }
            }

            flag2 = false;
            parsedSet = new HashSet<>();
        }

        Toolkit.getDefaultToolkit().beep();
        JOptionPane.showMessageDialog(null, "Metrics have been extracted.", "Done", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void extractSelectedMetrics() {
        int mov = mainFrame.getMetricsBackwardRadioBtn().isSelected() ? 0 : 1;
        List<String> selected = mainFrame.getMergeListMetrics().getSelectedValuesList();
        List<String> errors = new ArrayList<>();
        Set<ParsedMerge> parsedSet = new HashSet<>();
        String type = mainFrame.getMetricsTypeCombo().getSelectedItem().toString();
        ParsedMerge pm;
        boolean flag1 = false, flag2 = false;

        for (String sel : selected) {
            if (Project.getMergeVersionToTest().get(sel) != null) {
                for (String test : Project.getMergeVersionToTest().get(sel)) {
                    try {
                        XMLResource res = Project.getMergeResource(sel, test, mov);
                        if (res == null) {
                            errors.add(sel);
                            break;
                        } else {
                           pm = new ParsedMerge(res.getDocumentId(), Integer.parseInt(sel.substring(1)), Integer.parseInt(test.substring(5)), res.getContentAsDOM(), type);
                           pm.parseXMLMerge();
                           parsedSet.add(pm);
                           flag1 = true;
                           flag2 = true;
                        }
                    } catch (XMLDBException exc) {

                    }
                }

                if (flag2) {
                    Node metrics = ParsedMerge.createMetricsNode(parsedSet, Project.getName(), Integer.parseInt(sel.substring(1)), type);
                    Project.addMetricsXMLResource(metrics, sel + "_" + type + (mov == 0 ? "_Backward.xml" : "_Forward.xml"));
                    Project.getMetricsSet().add(sel + " " + type + (mov == 0 ? " Backward" : " Forward"));
                }
            }

            flag2 = false;
            parsedSet = new HashSet<>();
        }

        Toolkit.getDefaultToolkit().beep();
        if (errors.isEmpty())
            JOptionPane.showMessageDialog(null, "Metrics have been extracted.", "Done", JOptionPane.INFORMATION_MESSAGE);
        else if (!errors.isEmpty() && flag1) {
            String err = "<html>Metrics have been extracted. It was impossible to find:<br>";
            for (String er : errors)
                err = err + er + " Merges<br>";
            err = err + "</html>";
            JOptionPane.showMessageDialog(null, err, "Done", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void extractSelectedPrioritization() {
        List<String> selected = mainFrame.getPrioListMetrics().getSelectedValuesList();
        boolean flag1 = false, flag2 = false;
        ParsedMetric pm;
        float alfa;

        if (mainFrame.getSortCombo().getSelectedItem().toString().equals("Lex") && mainFrame.getAlfaParam().getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Alfa parameter must be a numerical value.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!mainFrame.getAlfaParam().getText().equals("")) {
            try {
                alfa = Float.parseFloat(mainFrame.getAlfaParam().getText());
            } catch(NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Alfa parameter must be a numerical value.", "Error", JOptionPane.ERROR_MESSAGE); 
                return;
            }
        } else
            alfa = -1;
        
        for (String sel : selected) {
            if (Project.getMetricsSet().contains(sel)) {
                try {
                    XMLResource res = Project.getMetricsResource(sel.replace(' ', '_') + ".xml");
                    if (res == null)
                        break;
                    else {
                        
                        pm = new ParsedMetric(Project.extractTypeFromMetric(sel), mainFrame.getSortCombo().getSelectedItem().toString(),
                                              Integer.parseInt(sel.substring(1, sel.indexOf(" "))), alfa, res.getContentAsDOM(),
                                              Project.extractMovFromMetric(sel), Project.getName());
                        
                        Node ris = pm.parseXMLMetrics();
                        
                        Project.addPrioritizationXMLResource(ris, 
                                                             sel.substring(0, sel.indexOf(" ")) + "_" + mainFrame.getSortCombo().getSelectedItem().toString() +
                                                             "_" + Project.extractTypeFromMetric(sel) + (Project.extractMovFromMetric(sel) == 0 ? "_Backward.xml" : "_Forward.xml"));
                        
                        Project.getPrioritizationSet().add(sel.substring(0, sel.indexOf(" ")) + " " + mainFrame.getSortCombo().getSelectedItem().toString() +
                                                             " " + Project.extractTypeFromMetric(sel) + (Project.extractMovFromMetric(sel) == 0 ? " Backward" : " Forward"));
                    }
                } catch (XMLDBException exc) {
                    
                }
            }
        }

        Toolkit.getDefaultToolkit().beep();
        JOptionPane.showMessageDialog(null, "Prioritizations have been extracted.", "Done", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // When mainFrame JFrame is visible for the first time
    private WindowAdapter mainFrameOpenedFirstTimeEvent() {
        return new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                // Create the instance of preferences file
                XMLPreferences.createInstance();
            }
        };
    }
    
    // When JList element is selected
    private ListSelectionListener mainFrameElementListSelectedEvent() {
        return new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                // Display all the tests in JList
                reportDisplayTests(mainFrame.getReportVersionList().getSelectedValue());
            }
        };
    }
    
    // When JList element is selected
    private ListSelectionListener mainFrameMergeElementListSelectedEvent() {
        return new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                // Display all the tests in JList
                mergeDisplayTests(mainFrame.getMergeVersionList().getSelectedValue());
            }
        };
    }
    
    // When JList element is selected
    private ListSelectionListener mainFrameTestListSelectedEvent() {
        return new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                // Display the selected test in JList
                reportDisplaySelectedTest(mainFrame.getReportVersionList().getSelectedValue(), mainFrame.getReportTestList().getSelectedValue());
                mainFrame.getReportTextArea().setCaretPosition(0);
            }
        };
    }
    
    // When JList element is selected
    private ListSelectionListener mainFrameMergeTestListSelectedEvent() {
        return new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                // Display the selected test in JList
                mergeDisplaySelectedTest(mainFrame.getMergeVersionList().getSelectedValue(), mainFrame.getMergeTestList().getSelectedValue());
                mainFrame.getMergeTextArea().setCaretPosition(0);
            }
        };
    }
    
    // When JList element is selected
    private ListSelectionListener mainFrameChurnListSelectedEvent() {
        return new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                // Display the selected churn in JList
                reportDisplaySelectedChurn(mainFrame.getChurnList().getSelectedValue());
                mainFrame.getChurnTextArea().setCaretPosition(0);
            }
        };
    }
    
    private ListSelectionListener mainFramePrioListSelectedEvent() {
        return new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                // Display the selected churn in JList
                prioDisplaySelectedPrio(mainFrame.getPrioList().getSelectedValue());
                mainFrame.getPrioTextArea().setCaretPosition(0);
            }
        };
    }
    
    private ListSelectionListener mainFrameMetricsListSelectedEvent() {
        return new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                // Display the selected churn in JList
                metricsDisplaySelectedMetric(mainFrame.getMetricsList().getSelectedValue());
                mainFrame.getMetricsTextArea().setCaretPosition(0);
            }
        };
    }
    
    private ChangeListener mainFrameTabChurnSelectedEvent() {
        return new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (mainFrame.getTabbedPane().getSelectedIndex() == 1) {
                    if (mainFrame.getChurnList().getModel().getSize() != Project.getChurnSet().size()) {
                        Set<String> churnSet = Project.getChurnSet();
                        DefaultListModel<String> churnModel = new DefaultListModel<>();
                        for (String churn : churnSet)
                            churnModel.addElement(churn);
                        mainFrame.getChurnList().setModel(churnModel);
                        mainFrame.getChurnList().setSelectedIndex(0);
                    }
                }
            }
        };
    }
    
    // When close project JMenuItem is clicked
    private ActionListener mainFrameClickCloseProjectItemEvent() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Close the project
                Project.cleanProject();
                // Hide JTabbedPane
                mainFrame.getTabbedPane().setVisible(false);
                // Active other JMenuItems and disable the closing one
                mainFrame.getNewProjectItem().setEnabled(true);
                mainFrame.getOpenProjectItem().setEnabled(true);
                mainFrame.getCloseProjectItem().setEnabled(false);
                // Reset the name of the mainFrame
                mainFrame.setTitle("Test Management Environment");
                mainFrame.getCovVersion().removeAllItems();
            }
        };
    }
    
    // When report JButton is clicked
    private ActionListener mainFrameClickAddReportBtnEvent() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Declare the file chooser and enable multiple file selections and filter
                JFileChooser chooser = new JFileChooser();
                chooser.setMultiSelectionEnabled(true);
                FileNameExtensionFilter filter = new FileNameExtensionFilter("eXtensible Markup Language (.xml)", "xml", "xml");
                chooser.setFileFilter(filter);
                
                // Show the dialog; wait until dialog is closed
                JFrame dialog = new JFrame();
                chooser.showOpenDialog(dialog);
                dialog.dispose();
                
                // Retrieve the selected files.
                File[] files = chooser.getSelectedFiles();
                
                // For each file we must create a DOM node
                // and create a new XMLResource containing that node
                // for store it into the collection
                try {
                    DocumentBuilderFactory dbFactory;
                    DocumentBuilder dBuilder;
                    Document doc;
                    DOMSource source;
                    
                    for (File f : files) {
                        dbFactory = DocumentBuilderFactory.newInstance();
                        dBuilder = dbFactory.newDocumentBuilder();
                        doc = dBuilder.parse(f);
                        source = new DOMSource(doc);
                        Project.addReportXMLResource(source.getNode(), f.getName());
                    }
                } catch (ParserConfigurationException | SAXException | IOException exc) {
                    
                }
                
                // Update the JList
                mainFrame.getReportVersionList().setSelectedIndex(mainFrame.getReportVersionList().getSelectedIndex()+1);
                mainFrame.getReportVersionList().setSelectedIndex(mainFrame.getReportVersionList().getSelectedIndex()-1);
            }
        };
    }
    
    // When report JButton is clicked
    private ActionListener mainFrameClickAddChurnBtnEvent() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Declare the file chooser and enable multiple file selections and filter
                JFileChooser chooser = new JFileChooser();
                chooser.setMultiSelectionEnabled(true);
                FileNameExtensionFilter filter = new FileNameExtensionFilter("eXtensible Markup Language (.xml)", "xml", "xml");
                chooser.setFileFilter(filter);
                
                // Show the dialog; wait until dialog is closed
                JFrame dialog = new JFrame();
                chooser.showOpenDialog(dialog);
                dialog.dispose();
                
                // Retrieve the selected files.
                File[] files = chooser.getSelectedFiles();
                
                // For each file we must create a DOM node
                // and create a new XMLResource containing that node
                // for store it into the collection
                try {
                    DocumentBuilderFactory dbFactory;
                    DocumentBuilder dBuilder;
                    Document doc;
                    DOMSource source;
                    
                    for (File f : files) {
                        dbFactory = DocumentBuilderFactory.newInstance();
                        dBuilder = dbFactory.newDocumentBuilder();
                        doc = dBuilder.parse(f);
                        source = new DOMSource(doc);
                        Project.addChurnXMLResource(source.getNode(), f.getName());
                    }
                } catch (ParserConfigurationException | SAXException | IOException exc) {
                    
                }
                
                // Update the JList
                mainFrame.getTabbedPane().setSelectedIndex(0);
                mainFrame.getTabbedPane().setSelectedIndex(1);
            }
        };
    }
    
    public ActionListener mainFrameClickRadioBtnEvent() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Display the selected test in JList
                mergeDisplaySelectedTest(mainFrame.getMergeVersionList().getSelectedValue(), mainFrame.getMergeTestList().getSelectedValue());
                mainFrame.getMergeTextArea().setCaretPosition(0);
            }
        };
    }
    
    public ActionListener mainFrameClickDelReportBtnEvent() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                int res = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete?", "Delete Reports", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                
                if (res == JOptionPane.YES_OPTION) {
                    String version = mainFrame.getReportVersionList().getSelectedValue();
                    List<String> tests = mainFrame.getReportTestList().getSelectedValuesList();

                    for (String t : tests)
                        Project.deleteReportXMLResource(version, t);

                    mainFrame.getReportVersionList().setSelectedIndex(mainFrame.getReportVersionList().getSelectedIndex()+1);
                    mainFrame.getReportVersionList().setSelectedIndex(mainFrame.getReportVersionList().getSelectedIndex()-1);
                }
            }
        };
    }
    
    public ActionListener mainFrameClickDelChurnBtnEvent() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                int res = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete?", "Delete Churns", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                
                if (res == JOptionPane.YES_OPTION) {
                    List<String> versions = mainFrame.getChurnList().getSelectedValuesList();

                    for (String v : versions)
                        Project.deleteChurnXMLResource(v);

                    mainFrame.getTabbedPane().setSelectedIndex(0);
                    mainFrame.getTabbedPane().setSelectedIndex(1);
                }
            }
        };
    }
    
    public ActionListener mainFrameClickDelMergeBtnEvent() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                int res = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete?", "Delete Merges", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                
                if (res == JOptionPane.YES_OPTION) {
                    String version = mainFrame.getMergeVersionList().getSelectedValue();
                    List<String> tests = mainFrame.getMergeTestList().getSelectedValuesList();
                    int movTo = -1;

                    if (mainFrame.getForwardRadioBtn().isSelected())
                        movTo = 1;
                    else if (mainFrame.getBackwardRadioBtn().isSelected())
                        movTo = 0;

                    for (String t : tests)
                        Project.deleteMergeXMLResource(version, t, movTo);

                    mainFrame.getMergeVersionList().setSelectedIndex(mainFrame.getMergeVersionList().getSelectedIndex()+1);
                    mainFrame.getMergeVersionList().setSelectedIndex(mainFrame.getMergeVersionList().getSelectedIndex()-1);
                }
            }
        };
    }
    
    private void reportDisplayTests(String version) {
        if (version != null) {
            // Take the map
            Map<String, Set<String>> reportVersionToTest = Project.getReportVersionToTest();
            // Create the model
            DefaultListModel<String> testModel = new DefaultListModel<>();
            // Iterate all over the map and build the model
            for (String test : reportVersionToTest.get(version))
                testModel.addElement(test);
            // Add the model to JList
            mainFrame.getReportTestList().setModel(testModel);
        }
    }
    
    private void mergeDisplayTests(String version) {
        if (version != null) {
            // Take the map
            Map<String, Set<String>> mergeVersionToTest = Project.getMergeVersionToTest();
            // Create the model
            DefaultListModel<String> testModel = new DefaultListModel<>();
            // Iterate all over the map and build the model
            for (String test : mergeVersionToTest.get(version))
                testModel.addElement(test);
            // Add the model to JList
            mainFrame.getMergeTestList().setModel(testModel);
        }
    }
    
    private void reportDisplaySelectedTest(String version, String test) {
        if (version != null && test != null) {
            // Take the Set
            Set<String> testSet = Project.getReportVersionToTest().get(version);
            // Look for the right test
            XMLResource res;
            if (testSet.contains(test)) {
                res = Project.getReportResource(version, test);
                try {
                    // Set visible the resource
                    mainFrame.getReportTextArea().setText((String)res.getContent());
                } catch (XMLDBException e) {
                    
                }
            }
        }
    }
    
    private void mergeDisplaySelectedTest(String version, String test) {
        if (version != null && test != null) {
            // Check if we have to go forward or backward
            int versStart = Integer.parseInt(mainFrame.getMergeVersionList().getModel().getElementAt(0).substring(1));
            int versEnd = Integer.parseInt(mainFrame.getMergeVersionList().getModel().getElementAt(mainFrame.getMergeVersionList().getModel().getSize()-1).substring(1));
            int versActual = Integer.parseInt(version.substring(1));
            int verTo = -1;
            // Take the Set
            Set<String> testSet = null;
            // Look for the right test
            XMLResource res;
            
            if (mainFrame.getForwardRadioBtn().isSelected()) {
                //if (versActual+1 <= versEnd) {
                    //version = "v" + (versActual+1);
                    testSet = Project.getMergeVersionToTest().get(version);
                    verTo = 1;
                //}
            } else if (mainFrame.getBackwardRadioBtn().isSelected()) {
                //if (versActual-1 >= versStart) {
                    //version = "v" + (versActual-1);
                    testSet = Project.getMergeVersionToTest().get(version);
                    verTo = 0;
                //} 
            }
            
            if (testSet != null && testSet.contains(test)) {
                res = Project.getMergeResource(version, test, verTo);
                try {
                    // Set visible the resource
                    mainFrame.getMergeTextArea().setText((String)res.getContent());
                } catch (NullPointerException | XMLDBException e) {
                    mainFrame.getMergeTextArea().setText("");
                }
            } else
                mainFrame.getMergeTextArea().setText("");
        }
    }
    
    private void reportDisplaySelectedChurn(String versions) {
        if (versions != null) {
            // Check if it exists
            XMLResource res;
            if (Project.getChurnSet().contains(versions)) {
                res = Project.getChurnResource(versions);
                try {
                    // Set visible the resource
                    mainFrame.getChurnTextArea().setText((String)res.getContent());
                } catch (XMLDBException e) {
                    
                }
            }
        }
    }
    
    private void prioDisplaySelectedPrio(String prio) {
        if (prio != null) {
            // Check if it exists
            XMLResource res;
            if (Project.getPrioritizationSet().contains(prio)) {
                res = Project.getPrioritizationResource(prio.replace(' ', '_') + ".xml");
                try {
                    // Set visible the resource
                    mainFrame.getPrioTextArea().setText((String)res.getContent());
                } catch (XMLDBException e) {
                    
                }
            }
        }
    }
    
    private void metricsDisplaySelectedMetric(String metric) {
        if (metric != null) {
            // Check if it exists
            XMLResource res;
            if (Project.getMetricsSet().contains(metric)) {
                res = Project.getMetricsResource(metric.replace(' ', '_') + ".xml");
                try {
                    // Set visible the resource
                    mainFrame.getMetricsTextArea().setText((String)res.getContent());
                } catch (XMLDBException e) {
                    
                }
            }
        }
    }
    
    private void coverageDisplaySelectedCoverage(String coverage) {
        try {
            XMLResource coverageRes = Project.getCoverageResource(coverage);
            String recovered;
            ResourceSet rs;
            CompiledExpression compiled;
            XQueryService query;
            int count;
            XYSeriesCollection xysc = new XYSeriesCollection();
            XYSeries xysStandard = new XYSeries("Standard");
            XYSeries xysGeneral = new XYSeries("General");
            XYSeries xysSpecific = new XYSeries("Specific");
            XYSeries xysLex = new XYSeries("Lex");
            String[] prioParamaters = coverage.split(" ");
            XMLResource appResource;
            ArrayList<Integer> prioritization;
            ArrayList<Integer> orderedForCover;
            int sum = 0, pos = 1;

            // Standard Prioritization
            appResource = Project.getPrioritizationResource(prioParamaters[0] + "_Standard_" + prioParamaters[1] + "_" + prioParamaters[2] + ".xml");
            prioritization = ParsedMetric.getListFromPrio(appResource.getContentAsDOM());
            orderedForCover = new ArrayList<>();;
            orderedForCover.add(0, -1);
            for (int i = 1; i <= prioritization.size()-1; i++) {
                query = (XQueryService)DatabaseManager.getCollection(DBConnector.getURI() + DBConnector.getCollection().getName()).getService("XQueryService", "1.0");
                query.setProperty(OutputKeys.INDENT, "yes");
                query.setProperty(OutputKeys.ENCODING, "UTF-8");
                compiled = query.compile("<count>{sum(doc('/db/root/" + Project.getName() + "/output/Coverage/" + coverage.replace(' ', '_') + ".xml')//Prioritization[@sort = 'Standard']//line[@pos = " + i + "]/@ci)}</count>");
                rs = query.execute(compiled);
                recovered = rs.getResource(0).getContent().toString();
                count = Integer.parseInt(recovered.substring(recovered.indexOf('>')+1, recovered.lastIndexOf('<')));
                orderedForCover.add(i, count);
            }
            for (int i = 1; i <= orderedForCover.size()-1; i++) {
                sum = sum + orderedForCover.get(i);
                xysStandard.add(sum, pos);
                pos++;
            }

            sum = 0;
            pos = 1;
             // General
            appResource = Project.getPrioritizationResource(prioParamaters[0] + "_General_" + prioParamaters[1] + "_" + prioParamaters[2] + ".xml");
            prioritization = ParsedMetric.getListFromPrio(appResource.getContentAsDOM());
            orderedForCover = new ArrayList<>();;
            orderedForCover.add(0, -1);
            for (int i = 1; i <= prioritization.size()-1; i++) {
                query = (XQueryService)DatabaseManager.getCollection(DBConnector.getURI() + DBConnector.getCollection().getName()).getService("XQueryService", "1.0");
                query.setProperty(OutputKeys.INDENT, "yes");
                query.setProperty(OutputKeys.ENCODING, "UTF-8");
                compiled = query.compile("<count>{sum(doc('/db/root/" + Project.getName() + "/output/Coverage/" + coverage.replace(' ', '_') + ".xml')//Prioritization[@sort = 'General']//line[@pos = " + i + "]/@ci)}</count>");
                rs = query.execute(compiled);
                recovered = rs.getResource(0).getContent().toString();
                count = Integer.parseInt(recovered.substring(recovered.indexOf('>')+1, recovered.lastIndexOf('<')));
                orderedForCover.add(i, count);
            }
            for (int i = 1; i <= orderedForCover.size()-1; i++) {
                sum = sum + orderedForCover.get(i);
                xysGeneral.add(sum, pos);
                pos++;
            }

            sum = 0;
            pos = 1;
            // Specific
            appResource = Project.getPrioritizationResource(prioParamaters[0] + "_Specific_" + prioParamaters[1] + "_" + prioParamaters[2] + ".xml");
            prioritization = ParsedMetric.getListFromPrio(appResource.getContentAsDOM());
            orderedForCover = new ArrayList<>();;
            orderedForCover.add(0, -1);
            for (int i = 1; i <= prioritization.size()-1; i++) {
                query = (XQueryService)DatabaseManager.getCollection(DBConnector.getURI() + DBConnector.getCollection().getName()).getService("XQueryService", "1.0");
                query.setProperty(OutputKeys.INDENT, "yes");
                query.setProperty(OutputKeys.ENCODING, "UTF-8");
                compiled = query.compile("<count>{sum(doc('/db/root/" + Project.getName() + "/output/Coverage/" + coverage.replace(' ', '_') + ".xml')//Prioritization[@sort = 'Specific']//line[@pos = " + i + "]/@ci)}</count>");
                rs = query.execute(compiled);
                recovered = rs.getResource(0).getContent().toString();
                count = Integer.parseInt(recovered.substring(recovered.indexOf('>')+1, recovered.lastIndexOf('<')));
                orderedForCover.add(i, count);
            }
            for (int i = 1; i <= orderedForCover.size()-1; i++) {
                sum = sum + orderedForCover.get(i);
                xysSpecific.add(sum, pos);
                pos++;
            }

            sum = 0;
            pos = 1;
            // Lex
            appResource = Project.getPrioritizationResource(prioParamaters[0] + "_Lex_" + prioParamaters[1] + "_" + prioParamaters[2] + ".xml");
            prioritization = ParsedMetric.getListFromPrio(appResource.getContentAsDOM());
            orderedForCover = new ArrayList<>();;
            orderedForCover.add(0, -1);
            for (int i = 1; i <= prioritization.size()-1; i++) {
                query = (XQueryService)DatabaseManager.getCollection(DBConnector.getURI() + DBConnector.getCollection().getName()).getService("XQueryService", "1.0");
                query.setProperty(OutputKeys.INDENT, "yes");
                query.setProperty(OutputKeys.ENCODING, "UTF-8");
                compiled = query.compile("<count>{sum(doc('/db/root/" + Project.getName() + "/output/Coverage/" + coverage.replace(' ', '_') + ".xml')//Prioritization[@sort = 'Lex']//line[@pos = " + i + "]/@ci)}</count>");
                rs = query.execute(compiled);
                recovered = rs.getResource(0).getContent().toString();
                count = Integer.parseInt(recovered.substring(recovered.indexOf('>')+1, recovered.lastIndexOf('<')));
                orderedForCover.add(i, count);
            }
            for (int i = 1; i <= orderedForCover.size()-1; i++) {
                sum = sum + orderedForCover.get(i);
                xysLex.add(sum, pos);
                pos++;
            }

            xysc.addSeries(xysStandard);
            xysc.addSeries(xysGeneral);
            xysc.addSeries(xysSpecific);
            xysc.addSeries(xysLex);

            mainFrame.lineChart = ChartFactory.createXYLineChart("Coverage Chart", "Number of covered instructions", "Test ordered by the prioritization", xysc, PlotOrientation.HORIZONTAL, true, true, false);
            mainFrame.lineChart.getXYPlot().setRenderer(new XYSplineRenderer());
            mainFrame.chart.setChart(mainFrame.lineChart);
        } catch (XMLDBException | NullPointerException exc) {

        }
    }
}