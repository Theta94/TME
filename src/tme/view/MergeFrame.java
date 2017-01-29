package tme.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;

public final class MergeFrame extends JFrame {
    private final JList<String> mergeVersionList, mergeTestList;
    private final JRadioButton forwardRadioBtn, backwardRadioBtn;
    private final JButton mergeBtn;
    private final JSplitPane mergeSplitPane;
    private final JProgressBar progressBar;
    private final JLabel outputLabel;
    
    public MergeFrame() {
        mergeVersionList = new JList<>();
        mergeVersionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        mergeTestList = new JList<>();
        
        final JPanel panel = new JPanel(new GridBagLayout());
        final GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.insets = new Insets(10, 10, 10, 10);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Select Reports:"), gbc);
        gbc.gridx++;
        
        final JScrollPane mergeVersionScrollPane = new JScrollPane(mergeVersionList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mergeVersionScrollPane.setPreferredSize(new Dimension(100, 200));
        final JScrollPane mergeTestScrollPane = new JScrollPane(mergeTestList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mergeTestScrollPane.setPreferredSize(new Dimension(100, 200));
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.VERTICAL;
        mergeSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mergeVersionScrollPane, mergeTestScrollPane);
        mergeSplitPane.setOneTouchExpandable(true);
        mergeSplitPane.setDividerLocation(80);
        panel.add(mergeSplitPane, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        final JPanel movPanel = new JPanel();
        movPanel.setBorder(BorderFactory.createTitledBorder("Movimentation"));
        backwardRadioBtn = new JRadioButton("Backward");
        backwardRadioBtn.setSelected(true);
        forwardRadioBtn = new JRadioButton("Forward");
        final ButtonGroup radioBtnGroup = new ButtonGroup();
        radioBtnGroup.add(backwardRadioBtn);
        radioBtnGroup.add(forwardRadioBtn);
        movPanel.add(backwardRadioBtn, gbc);
        movPanel.add(forwardRadioBtn, gbc);
        panel.add(movPanel, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        mergeBtn = new JButton("Merge", new ImageIcon("images/merge.png"));
        panel.add(mergeBtn, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        gbc.fill = GridBagConstraints.BOTH;
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setString("");
        panel.add(progressBar, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        outputLabel = new JLabel();
        panel.add(outputLabel, gbc);
        
        this.add(panel);
        
        setTitle("Merge");
        setIconImage(new ImageIcon("images/merge.png").getImage());
        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 500);
        setMinimumSize(new Dimension(600, 500));
        setLocationRelativeTo(null);
    }
    
    public JList<String> getMergeVersionList() {
        return mergeVersionList;
    }
    
    public JList<String> getMergeTestList() {
        return mergeTestList;
    }
    
    public JRadioButton getForwardRadioBtn() {
        return forwardRadioBtn;
    }
    
    public JRadioButton getBackwardRadioBtn() {
        return backwardRadioBtn;
    }
    
    public JButton getMergeBtn() {
        return mergeBtn;
    }
    
    public JSplitPane getMergeSplitPane() {
        return mergeSplitPane;
    }
    
    public JLabel getOutputLabel() {
        return outputLabel;
    }
    
    public JProgressBar getProgressBar() {
        return progressBar;
    }
}
