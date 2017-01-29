package tme.controller;

import java.awt.Toolkit;
import tme.model.merge.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.DefaultListModel;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;
import tme.model.Project;
import tme.view.MergeFrame;

public final class MergeFrameController {
    private final MergeFrame mergeFrame;
    
    public MergeFrameController(MergeFrame mergeFrame) {
       this.mergeFrame = mergeFrame; 
    }
    
    public void start() {
        // Events management
        mergeFrame.addWindowListener(mergeFrameOpenedEvent());
        mergeFrame.addWindowListener(mergeFrameClosedEvent());
        mergeFrame.getMergeVersionList().addListSelectionListener(mergeFrameElementListSelectedEvent());
        mergeFrame.getMergeBtn().addActionListener(mergeFrameClickMergeBtnEvent());
    }
    
    private WindowListener mergeFrameOpenedEvent() {
        return new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                // Take the map
                Map<String, Set<String>> reportVersionToTest = Project.getReportVersionToTest();
                // Create the version model
                DefaultListModel<String> versionModel = new DefaultListModel<>();
                // Iterate all over the elements and build the model
                for (String version : reportVersionToTest.keySet())
                    versionModel.addElement(version);
                // Add model to JList
                mergeFrame.getMergeVersionList().setModel(versionModel);
                mergeFrame.getMergeVersionList().setSelectedIndex(0);
            }
        };
    }
    
    private WindowListener mergeFrameClosedEvent() {
        return new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                mergeFrame.getOutputLabel().setVisible(false);
                mergeFrame.getProgressBar().setValue(0);
                mergeFrame.getMergeTestList().clearSelection();
                mergeFrame.getProgressBar().setString("");
            }
        };
    }
    
    // When JList element is selected
    private ListSelectionListener mergeFrameElementListSelectedEvent() {
        return new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                // Display all the tests in JList
                reportDisplayTests(mergeFrame.getMergeVersionList().getSelectedValue());
            }
        };
    }
    
    private ActionListener mergeFrameClickMergeBtnEvent() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                progressMerge().execute();
            }
        };
    }
    
    private void merge() {
        mergeFrame.getOutputLabel().setText("");

        String version = mergeFrame.getMergeVersionList().getSelectedValue();
        List<String> tests = mergeFrame.getMergeTestList().getSelectedValuesList();
        ArrayList<String> errors = new ArrayList<>();
        int movTo = -1;
        boolean atLeastOne = false;

        if (mergeFrame.getForwardRadioBtn().isSelected())
            movTo = 1;
        else if (mergeFrame.getBackwardRadioBtn().isSelected())
            movTo = 0;

        for (String t : tests) {
            // Check if churn exists
            if (movTo == 1) {
                int verTo = Integer.parseInt(version.substring(1)) + 1;
                if (Project.getChurnSet().contains(version + " -> v" + verTo) && 
                    Project.getMergeResource(version, t, 1) == null) {
                    XMLResource resReport = Project.getReportResource(version, t);
                    XMLResource resChurn = Project.getChurnResource(version + " -> v" + verTo);
                    try {
                        ParsedReport pr = new ParsedReport(resReport.getContentAsDOM());
                        pr.parseXMLReport();
                        ParsedChurn pc = new ParsedChurn(resChurn.getContentAsDOM());
                        pc.parseXMLChurn();
                        XMLMerge merge = new XMLMerge(pr, pc);
                        Project.addMergeXMLResource(merge.createXMLMergeFile(), version, t, 1);
                        atLeastOne = true;
                    } catch (XMLDBException ex) {

                    }
                } else
                    errors.add(t);
            } else {
                int verFrom = Integer.parseInt(version.substring(1)) - 1;
                if (Project.getChurnSet().contains("v" + verFrom + " -> " + version) &&
                    Project.getMergeResource(version, t, 0) == null) {
                    XMLResource resReport = Project.getReportResource(version, t);
                    XMLResource resChurn = Project.getChurnResource("v" + verFrom + " -> " + version);
                    try {
                        ParsedReport pr = new ParsedReport(resReport.getContentAsDOM());
                        pr.parseXMLReport();
                        ParsedChurn pc = new ParsedChurn(resChurn.getContentAsDOM());
                        pc.parseXMLChurn();
                        XMLMerge merge = new XMLMerge(pr, pc);
                        Project.addMergeXMLResource(merge.createXMLMergeFile(), version, t, 0);
                        atLeastOne = true;
                    } catch (XMLDBException ex) {

                    }
                } else
                    errors.add(t);
            }
        }

        String out = "";

        if (atLeastOne) {
                out = out + "<html>Merge completed with success!<br><br>";
        }

        if (errors.size() > 0) {
            if (out.equals(""))
                out = out + "<html>Impossible to merge tests: ";
            else
                out = out + "Impossible to merge tests: ";
            for (String s : errors)
                out = out + s.substring(5) + " ";
            out = out + "<br>due to churn file is missing or this merge already exists.";
        }

        if (out.length() > 0)
            out = out + "</html>";

        mergeFrame.getOutputLabel().setText(out);
        mergeFrame.getOutputLabel().setVisible(true);
    }
    
    /*private void updateJProgressBar(int[] actual) {
        mergeFrame.getProgressBar().setValue((100*actual[0])/actual[1]);
    }*/
    
    private SwingWorker<Void,Void> progressMerge() {
        return new SwingWorker<Void,Void>() {
            @Override
            public Void doInBackground() {
                mergeFrame.getProgressBar().setValue(0);
                mergeFrame.getProgressBar().setIndeterminate(true);
                mergeFrame.getProgressBar().setString("Merging...");
                merge();
                return null;
            }
            
            @Override
            public void done() {
                mergeFrame.getProgressBar().setIndeterminate(false);
                mergeFrame.getProgressBar().setString("Done");
                Toolkit.getDefaultToolkit().beep();
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
            mergeFrame.getMergeTestList().setModel(testModel);
        }
    }
}
