package tme.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;

public final class MainFrame extends JFrame {
    private final JMenuBar upperMenu;
    private final JMenu projectMenu, databaseMenu;
    private final JMenuItem newProjectItem, openProjectItem, closeProjectItem, connectionItem;
    private final JList<String> reportVersionList, reportTestList, churnList, mergeVersionList, mergeTestList, mergeListMetrics, metricsList, prioListMetrics, prioList, coverageList;
    private final JSplitPane projectSplitPane, mergeSplitPane;
    private final JTabbedPane tabbedPane;
    private final RSyntaxTextArea reportTextArea, churnTextArea, mergeTextArea, prioTextArea, metricsTextArea;
    private final JButton addReportBtn, addChurnBtn, createMergeBtn, delReportBtn, delChurnBtn, delMergeBtn, metricsCalculateSelBtn, metricsCalculateMissBtn, metricsCalculateAllBtn, prioCalculateSelBtn, distBtn, covBtn, compRepBtn;
    private final JRadioButton forwardRadioBtn, backwardRadioBtn, metricsBackwardRadioBtn, metricsForwardRadioBtn;
    private final JComboBox metricsTypeCombo, sortCombo, covVersion, covType, covMov;
    private final JProgressBar metricsProgressBar, prioProgressBar, covProgressBar;
    private final JTextField alfaParam;
    public ChartPanel chart;
    public JFreeChart lineChart;
    
    public MainFrame() {
        // Create JMenuBar
        upperMenu = new JMenuBar();
        
        // Create JMenus
        projectMenu = new JMenu("Project");
        databaseMenu = new JMenu("Database", false);
        
        // Create JMenuItems
        newProjectItem = new JMenuItem("New project", new ImageIcon("images/folder_add.png"));
        openProjectItem = new JMenuItem("Open project", new ImageIcon("images/folder.png"));
        closeProjectItem = new JMenuItem("Close project", new ImageIcon("images/folder_del.png"));
        connectionItem = new JMenuItem("Connection", new ImageIcon("images/database.png"));
        
        // Add JMenuItems to JMenus
        projectMenu.add(newProjectItem);
        projectMenu.add(openProjectItem);
        projectMenu.add(closeProjectItem);
        databaseMenu.add(connectionItem);
        
        // Add JMenus to JMenuBar
        upperMenu.add(projectMenu);
        upperMenu.add(databaseMenu);
        
        // Add JMenuBar to this JFrame
        setJMenuBar(upperMenu);
        
        // Create the main panel
        final JPanel mainPanel = new JPanel(new GridBagLayout());
        final GridBagConstraints gbc = new GridBagConstraints();
        
        // Create and position JTabbedPane
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        tabbedPane = new JTabbedPane();
        tabbedPane.setMinimumSize(new Dimension(950, 400));
        mainPanel.add(tabbedPane, gbc);
        
        // Create the Report panel
        final JPanel reportPanel = new JPanel(new GridBagLayout());
        reportPanel.setBackground(Color.white);
                  
        // Create the lists
        reportVersionList = new JList<>();
        reportVersionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reportTestList = new JList<>();
        
        // Create scroll panes
        final JScrollPane reportVersionScrollPane = new JScrollPane(reportVersionList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        reportVersionScrollPane.setPreferredSize(new Dimension(150, 400));
        final JScrollPane reportTestScrollPane = new JScrollPane(reportTestList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        reportTestScrollPane.setPreferredSize(new Dimension(150, 400));
        
        // Create and position the split pane
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        projectSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, reportVersionScrollPane, reportTestScrollPane);
        projectSplitPane.setOneTouchExpandable(true);
        projectSplitPane.setDividerLocation(80);
        reportPanel.add(projectSplitPane, gbc);
        
        // Create the RSyntaxTextArea for reports
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        reportTextArea = new RSyntaxTextArea();
        reportTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
        reportTextArea.setCodeFoldingEnabled(true);
        reportTextArea.setEditable(false);
        RTextScrollPane reportTextScrollPane = new RTextScrollPane(reportTextArea);
        reportTextScrollPane.setPreferredSize(new Dimension(670, 400));
        reportPanel.add(reportTextScrollPane, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        delReportBtn = new JButton("Delete Reports", new ImageIcon("images/delete.png"));
        reportPanel.add(delReportBtn, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        compRepBtn = new JButton("Extract Compact Reports");
        reportPanel.add(compRepBtn, gbc);
        
        // Create JButton
        gbc.gridx = 2;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        addReportBtn = new JButton("Add Reports", new ImageIcon("images/add.png"));
        reportPanel.add(addReportBtn, gbc);
        
        // Add the reportPanel to JTabbedPane
        tabbedPane.add(reportPanel, "Report");
        
        // Create the Churn panel
        final JPanel churnPanel = new JPanel(new GridBagLayout());
        churnPanel.setBackground(Color.white);
        
        // Create the list
        churnList = new JList<>();
        
        // Create and position the scoll pane
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        final JScrollPane churnScrollPane = new JScrollPane(churnList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        churnScrollPane.setPreferredSize(new Dimension(150, 400));
        churnPanel.add(churnScrollPane, gbc);
        
        // Create the RSyntaxTextArea for churn
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        churnTextArea = new RSyntaxTextArea();
        churnTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
        churnTextArea.setCodeFoldingEnabled(true);
        churnTextArea.setEditable(false);
        RTextScrollPane churnRTextScrollPane = new RTextScrollPane(churnTextArea);
        churnRTextScrollPane.setPreferredSize(new Dimension(830, 400));
        churnPanel.add(churnRTextScrollPane, gbc);
        
        // Create JButton
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        addChurnBtn = new JButton("Add Churns", new ImageIcon("images/add.png"));
        churnPanel.add(addChurnBtn, gbc);
        
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        delChurnBtn = new JButton("Delete Churns", new ImageIcon("images/delete.png"));
        churnPanel.add(delChurnBtn, gbc);
        
        // Add churnPanel to JTabbedPane
        tabbedPane.add(churnPanel, "Churn");
        
        // Create the Churn panel
        final JPanel mergePanel = new JPanel(new GridBagLayout());
        mergePanel.setBackground(Color.white);
        
        // Create the lists
        mergeVersionList = new JList<>();
        mergeVersionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mergeTestList = new JList<>();
        
        // Create scroll panes
        final JScrollPane mergeVersionScrollPane = new JScrollPane(mergeVersionList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mergeVersionScrollPane.setPreferredSize(new Dimension(150, 340));
        final JScrollPane mergeTestScrollPane = new JScrollPane(mergeTestList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mergeTestScrollPane.setPreferredSize(new Dimension(150, 340));
        
        // Create and position the split pane
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        mergeSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mergeVersionScrollPane, mergeTestScrollPane);
        mergeSplitPane.setOneTouchExpandable(true);
        mergeSplitPane.setDividerLocation(80);
        mergePanel.add(mergeSplitPane, gbc);
        
        // Create the RSyntaxTextArea for merges
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        mergeTextArea = new RSyntaxTextArea();
        mergeTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
        mergeTextArea.setCodeFoldingEnabled(true);
        mergeTextArea.setEditable(false);
        RTextScrollPane mergeTextScrollPane = new RTextScrollPane(mergeTextArea);
        mergeTextScrollPane.setPreferredSize(new Dimension(670, 400));
        mergePanel.add(mergeTextScrollPane, gbc);
        
        // Create JButton
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        createMergeBtn = new JButton("New Merges", new ImageIcon("images/merge.png"));
        mergePanel.add(createMergeBtn, gbc);
        
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        delMergeBtn = new JButton("Delete Merges", new ImageIcon("images/delete.png"));
        mergePanel.add(delMergeBtn, gbc);
        
        // RadioBtnPanel, JRadioButtons
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        final JPanel radioBtnPanel = new JPanel();
        radioBtnPanel.setBorder(BorderFactory.createTitledBorder("Movimentation"));
        
        backwardRadioBtn = new JRadioButton("Backward");
        backwardRadioBtn.setSelected(true);
        forwardRadioBtn = new JRadioButton("Forward");
        final ButtonGroup radioBtnGroup = new ButtonGroup();
        radioBtnGroup.add(backwardRadioBtn);
        radioBtnGroup.add(forwardRadioBtn);
        radioBtnPanel.add(backwardRadioBtn, gbc);
        radioBtnPanel.add(forwardRadioBtn, gbc);
        mergePanel.add(radioBtnPanel, gbc);
        
        // Add the mergePanel to JTabbedPane
        tabbedPane.add(mergePanel, "Merge");
        
        // Create the Metrics panel
        final JPanel metricsPanel = new JPanel(new GridBagLayout());
        metricsPanel.setBackground(Color.white);
        
        gbc.insets = new Insets(3, 3, 3, 3);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        metricsPanel.add(new JLabel("Available Merges"), gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        mergeListMetrics = new JList<>();
        final JScrollPane metricsScroll1 = new JScrollPane(mergeListMetrics, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        metricsScroll1.setPreferredSize(new Dimension(100, 400));
        metricsPanel.add(metricsScroll1, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        metricsPanel.add(new JLabel("Available Metrics"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        metricsList = new JList<>();
        final JScrollPane metricsScroll2 = new JScrollPane(metricsList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        metricsScroll2.setPreferredSize(new Dimension(200, 400));
        metricsPanel.add(metricsScroll2, gbc);
        
        gbc.gridx = 2;
        gbc.gridy = 1;
        metricsTextArea = new RSyntaxTextArea();
        metricsTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
        metricsTextArea.setCodeFoldingEnabled(true);
        metricsTextArea.setEditable(false);
        RTextScrollPane metricsTextScrollPane = new RTextScrollPane(metricsTextArea);
        metricsTextScrollPane.setPreferredSize(new Dimension(380, 400));
        metricsPanel.add(metricsTextScrollPane, gbc);
        
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        final JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Input Panel"));
        inputPanel.setPreferredSize(new Dimension(250, 400));
        metricsPanel.add(inputPanel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        final JPanel prioRadioBtnPanel = new JPanel(new GridBagLayout());
        prioRadioBtnPanel.setBorder(BorderFactory.createTitledBorder("Movimentation"));
        metricsBackwardRadioBtn = new JRadioButton("Backward");
        metricsBackwardRadioBtn.setSelected(true);
        metricsForwardRadioBtn = new JRadioButton("Forward");
        final ButtonGroup prioRadioBtnGroup = new ButtonGroup();
        prioRadioBtnGroup.add(metricsBackwardRadioBtn);
        prioRadioBtnGroup.add(metricsForwardRadioBtn);
        prioRadioBtnPanel.add(metricsBackwardRadioBtn, gbc);
        gbc.gridy++;
        prioRadioBtnPanel.add(metricsForwardRadioBtn, gbc);
        inputPanel.add(prioRadioBtnPanel, gbc);
        
        gbc.gridy++;
        metricsTypeCombo = new JComboBox();
        inputPanel.add(metricsTypeCombo, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        metricsCalculateSelBtn = new JButton("Calculate selected metrics");
        inputPanel.add(metricsCalculateSelBtn, gbc);
        gbc.gridy++;
        metricsCalculateMissBtn = new JButton("Calculate missing metrics");
        inputPanel.add(metricsCalculateMissBtn, gbc);
        gbc.gridy++;
        metricsCalculateAllBtn = new JButton("Calculate all metrics");

        inputPanel.add(metricsCalculateAllBtn, gbc);
        
        gbc.gridy++;
        metricsProgressBar = new JProgressBar(0, 100);
        metricsProgressBar.setStringPainted(true);
        metricsProgressBar.setString("");
        inputPanel.add(metricsProgressBar, gbc);
        
        tabbedPane.add(metricsPanel, "Metrics");
        
        // Create the Priorization panel
        final JPanel prioPanel = new JPanel(new GridBagLayout());
        prioPanel.setBackground(Color.white);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        prioPanel.add(new JLabel("Available Metrics"), gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        prioListMetrics = new JList<>();
        final JScrollPane prioScroll1 = new JScrollPane(prioListMetrics, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        prioScroll1.setPreferredSize(new Dimension(150, 400));
        prioPanel.add(prioScroll1, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        prioPanel.add(new JLabel("Available Prioritizations"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        prioList = new JList<>();
        final JScrollPane prioScroll2 = new JScrollPane(prioList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        prioScroll2.setPreferredSize(new Dimension(220, 400));
        prioPanel.add(prioScroll2, gbc);
        
        gbc.gridx = 2;
        gbc.gridy = 1;
        prioTextArea = new RSyntaxTextArea();
        prioTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
        prioTextArea.setCodeFoldingEnabled(true);
        prioTextArea.setEditable(false);
        RTextScrollPane prioTextScrollPane = new RTextScrollPane(prioTextArea);
        prioTextScrollPane.setPreferredSize(new Dimension(320, 400));
        prioPanel.add(prioTextScrollPane, gbc);
        
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        final JPanel prioInputPanel = new JPanel(new GridBagLayout());
        prioInputPanel.setBorder(BorderFactory.createTitledBorder("Input Panel"));
        prioInputPanel.setPreferredSize(new Dimension(250, 400));
        prioPanel.add(prioInputPanel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        sortCombo = new JComboBox();
        prioInputPanel.add(sortCombo, gbc);
        gbc.gridy++;
        alfaParam = new JTextField();
        prioInputPanel.add(alfaParam, gbc);
        
        gbc.gridy = 4;
        prioCalculateSelBtn = new JButton("Calculate selected prioritizations");
        prioInputPanel.add(prioCalculateSelBtn, gbc);
        gbc.gridy++;
        distBtn = new JButton("Calculate distance");
        prioInputPanel.add(distBtn, gbc);
        gbc.gridy++;

        prioProgressBar = new JProgressBar(0, 100);
        prioProgressBar.setStringPainted(true);
        prioProgressBar.setString("");
        prioInputPanel.add(prioProgressBar, gbc);
        
        tabbedPane.add(prioPanel, "Prioritization");
        
        // Create the Coverage panel
        final JPanel coveragePanel = new JPanel(new GridBagLayout());
        coveragePanel.setBackground(Color.white);
        
        // Creare bottoni e come inserire input (probabilmente per combobox)
        gbc.gridx = 0;
        gbc.gridy = 0;
        final JPanel coverageInput = new JPanel(new GridBagLayout());
        coverageInput.setBorder(BorderFactory.createTitledBorder("Input Panel"));
        coverageInput.setPreferredSize(new Dimension(180, 400));
        coveragePanel.add(coverageInput, gbc);
        gbc.gridx = 0;
        gbc.gridy = 0;
        coverageInput.add(new JLabel("Prioritization:"), gbc);
        gbc.gridy++;
        covVersion = new JComboBox();
        coverageInput.add(covVersion, gbc);
        gbc.gridy++;
        covType = new JComboBox();
        coverageInput.add(covType, gbc);
        gbc.gridy++;
        covMov = new JComboBox();
        coverageInput.add(covMov, gbc);
        gbc.gridy++;
        covBtn = new JButton("Calculate coverage");
        coverageInput.add(covBtn, gbc);
        gbc.gridy++;
        covProgressBar = new JProgressBar(0, 100);
        covProgressBar.setStringPainted(true);
        covProgressBar.setString("");
        coverageInput.add(covProgressBar, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        coveragePanel.add(new JLabel("Available Coverages"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.SOUTH;
        coverageList = new JList<>();
        final JScrollPane covScroll = new JScrollPane(coverageList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        covScroll.setPreferredSize(new Dimension(150, 380));
        coveragePanel.add(covScroll, gbc);
        
        gbc.gridx = 2;
        gbc.gridy = 0;
        lineChart = ChartFactory.createXYLineChart("Coverage Chart", "Number of covered instructions", "Test ordered by the number of covered instructions", null, PlotOrientation.HORIZONTAL, false, true, false);
        chart = new ChartPanel(lineChart);
        chart.setPreferredSize(new Dimension(600, 400));
        coveragePanel.add(chart, gbc);
        
        tabbedPane.add(coveragePanel, "Coverage");
        
        // Add JPanel to this JFrame
        getContentPane().add(mainPanel);
        
        // Set some properties of this JFrame
        setTitle("Test Management Environment");
        setIconImage(new ImageIcon("images/bug.png").getImage());
        pack();
        setSize(1024, 600);
        setMinimumSize(new Dimension(1024, 600));
        setLocationRelativeTo(null);
    }
    
    public JMenuItem getNewProjectItem() {
        return newProjectItem;
    }
    
    public JMenuItem getOpenProjectItem() {
        return openProjectItem;
    }
    
    public JMenuItem getCloseProjectItem() {
        return closeProjectItem;
    }
    
    public JMenuItem getConnectionItem() {
        return connectionItem;
    }
    
    public JMenu getDatabaseMenu() {
        return databaseMenu;
    }
    
    public JSplitPane getProjectSplitPane() {
        return projectSplitPane;
    }
    
    public JSplitPane getMergeSplitPane() {
        return mergeSplitPane;
    }
    
    public JList<String> getReportVersionList() {
        return reportVersionList;
    }
    
    public JList<String> getReportTestList() {
        return reportTestList;
    }
    
    public JList<String> getChurnList() {
        return churnList;
    }
    
    public JList<String> getMergeVersionList() {
        return mergeVersionList;
    }
    
    public JList<String> getMergeTestList() {
        return mergeTestList;
    }
    
    public RSyntaxTextArea getReportTextArea() {
        return reportTextArea;
    }
    
    public RSyntaxTextArea getChurnTextArea() {
        return churnTextArea;
    }
    
    public RSyntaxTextArea getMergeTextArea() {
        return mergeTextArea;
    }
    
    public JButton getAddReportBtn() {
        return addReportBtn;
    }
    
    public JButton getAddChurnBtn() {
        return addChurnBtn;
    }
    
    public JButton getCreateMergeBtn() {
        return createMergeBtn;
    }
    
    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }
    
    public JRadioButton getForwardRadioBtn() {
        return forwardRadioBtn;
    }
    
    public JRadioButton getBackwardRadioBtn() {
        return backwardRadioBtn;
    }
    
    public JButton getDelChurnBtn() {
        return delChurnBtn;
    }
    
    public JButton getDelReportBtn() {
        return delReportBtn;
    }
    
    public JButton getDelMergeBtn() {
        return delMergeBtn;
    }
    
    public JList<String> getMergeListMetrics() {
        return mergeListMetrics;
    }
    
    public JList<String> getMetricsList() {
        return metricsList;
    }
    
    public JRadioButton getMetricsBackwardRadioBtn() {
        return metricsBackwardRadioBtn;
    }
    
    public JRadioButton getMetricsForwardRadioBtn() {
        return metricsForwardRadioBtn;
    }
    
    public JButton getMetricsCalculateSelBtn() {
        return metricsCalculateSelBtn;
    }
    
    public JButton getMetricsCalculateMissBtn() {
        return metricsCalculateMissBtn;
    }
    
    public JButton getMetricsCalculateAllBtn() {
        return metricsCalculateAllBtn;
    }
    
    public JComboBox getMetricsTypeCombo() {
        return metricsTypeCombo;
    }
    
    public JProgressBar getMetricsProgressBar() {
        return metricsProgressBar;
    }
    
    public JList<String> getPrioListMetrics() {
        return prioListMetrics;
    }
    
    public JList<String> getPrioList() {
        return prioList;
    }
    
    public JComboBox getSortCombo() {
        return sortCombo;
    }
    
    public JButton getPrioCalculateSelBtn() {
        return prioCalculateSelBtn;
    }
    
    public JProgressBar getPrioProgressBar() {
        return prioProgressBar;
    }
    
    public JTextField getAlfaParam() {
        return alfaParam;
    }
    
    public RSyntaxTextArea getPrioTextArea() {
        return prioTextArea;
    }
    
    public RSyntaxTextArea getMetricsTextArea() {
        return metricsTextArea;
    }
    
    public JButton getDistBtn() {
        return distBtn;
    }
    
    public JList<String> getCoverageList() {
        return coverageList;
    }
    
    public ChartPanel getChart() {
        return chart;
    }
    
    public JButton getCovBtn() {
        return covBtn;
    }
    
    public JComboBox getCovVersion() {
        return covVersion;
    }
    
    public JComboBox getCovType() {
        return covType;
    }
    
    public JComboBox getCovMov() {
        return covMov;
    }
    
    public JProgressBar getCovProgressBar() {
        return covProgressBar;
    }
    
    public JButton getCompRepBtn() {
        return compRepBtn;
    }
    
    public JFreeChart getLineChart() {
        return lineChart;
    }
}