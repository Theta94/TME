package tme.controller;

import tme.view.MainFrame;
import tme.view.OpenProjectFrame;
import tme.model.Project;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.Set;
import javax.swing.DefaultListModel;

// Handle events between MainForm and OpenProjectForm
public final class MainOpenProjectFramesController {
    private final MainFrame mainFrame;
    private final OpenProjectFrame openProjectFrame;
    
    public MainOpenProjectFramesController(MainFrame mainFrame, OpenProjectFrame openProjectFrame) {
        this.mainFrame = mainFrame;
        this.openProjectFrame = openProjectFrame;
    }
    
    public void start() {
        // Events management
        mainFrame.getOpenProjectItem().addActionListener(mainFrameClickOpenProjectItemEvent());
        
        openProjectFrame.getOpenBtn().addActionListener(openProjectFrameClickOpenBtnEvent());
        openProjectFrame.getProjectList().addMouseListener(openProjectFrameDoubleClickJListEvent());
    }
    
    private ActionListener mainFrameClickOpenProjectItemEvent() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Show openProjectFrame JFrame
                openProjectFrame.setVisible(true);
            }
        };
    }
    
    private ActionListener openProjectFrameClickOpenBtnEvent() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open the project
                openProject();
                // Close this openProjectFrame
                openProjectFrame.dispose();
                // Show JTabbedPane
                mainFrame.getTabbedPane().setVisible(true);
                // Add the project name on the title
                mainFrame.setTitle("Project " + openProjectFrame.getProjectList().getSelectedValue() + " - " + mainFrame.getTitle());
                // Block and allow other project menu
                mainFrame.getNewProjectItem().setEnabled(false);
                mainFrame.getOpenProjectItem().setEnabled(false);
                mainFrame.getCloseProjectItem().setEnabled(true);
                // Show first test
                mainFrame.getReportTestList().setSelectedIndex(0);
                mainFrame.getMergeTestList().setSelectedIndex(0);
                // Trigger
                mainFrame.getTabbedPane().setSelectedIndex(1);
                mainFrame.getTabbedPane().setSelectedIndex(0);
            }
        };
    }
    
    private MouseAdapter openProjectFrameDoubleClickJListEvent() {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    // Open the project
                    openProject();
                    // Close this openProjectFrame
                    openProjectFrame.dispose();
                    // Show JTabbedPane
                    mainFrame.getTabbedPane().setVisible(true);
                    // Add the project name on the title
                    mainFrame.setTitle("Project " + openProjectFrame.getProjectList().getSelectedValue() + " - " + mainFrame.getTitle());
                    // Block and allow other project menu
                    mainFrame.getNewProjectItem().setEnabled(false);
                    mainFrame.getOpenProjectItem().setEnabled(false);
                    mainFrame.getCloseProjectItem().setEnabled(true);
                    // Show first test
                    mainFrame.getReportTestList().setSelectedIndex(0);
                    mainFrame.getMergeTestList().setSelectedIndex(0);
                }
            }
        };
    }
    
    private void openProject() {
        // Take the name of the project
        String name = openProjectFrame.getProjectList().getSelectedValue();
        // Create the project model
        Project.initProject(name);
        
        // Extract all the reports
        Project.buildMapReportVersionToTest();
        // Take the map
        Map<String, Set<String>> reportVersionToTest = Project.getReportVersionToTest();
        // Create the version model
        DefaultListModel<String> versionModel = new DefaultListModel<>();
        // Iterate all over the elements and build the model
        for (String version : reportVersionToTest.keySet())
            versionModel.addElement(version);
        // Add model to JList
        mainFrame.getReportVersionList().setModel(versionModel);
        mainFrame.getReportVersionList().setSelectedIndex(0);
        
        // Extract all the churn
        Project.buildChurnSet();
        // Take the set
        Set<String> churnSet = Project.getChurnSet();
        // Create the churn model
        DefaultListModel<String> churnModel = new DefaultListModel<>();
        // Iterate all over the elements and build the model
        for (String churn : churnSet)
            churnModel.addElement(churn);
        // Add model to JList
        mainFrame.getChurnList().setModel(churnModel);
        mainFrame.getChurnList().setSelectedIndex(0);
        
        // Extract all the merges
        Project.buildMapMergeVersionToTest();
        // Take the map
        Map<String, Set<String>> mergeVersionToTest = Project.getMergeVersionToTest();
        // Create the version model
        DefaultListModel<String> mergeModel = new DefaultListModel<>();
        // Iterate all over the elements and build the model
        for (String version : mergeVersionToTest.keySet())
            mergeModel.addElement(version);
        // Add model to JList
        mainFrame.getMergeVersionList().setModel(mergeModel);
        mainFrame.getMergeVersionList().setSelectedIndex(0);
        
        // Extract all the metrics
        Project.buildMetricsSet();
        
        // Extract all the priorizations
        Project.buildPrioritizationSet();
        
        // Extract all the coverages
        Project.buildCoverageSet();
        
        for (String rep : Project.getReportVersionToTest().keySet())
            mainFrame.getCovVersion().addItem(rep);
        
        mainFrame.getTabbedPane().setSelectedIndex(0);
    }
}
