package tme.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public final class OpenProjectFrame extends JFrame {
    private final JList<String> projectList;
    private final JButton openBtn;
    
    public OpenProjectFrame() {
        // Inner declarations
        final JPanel panel = new JPanel(new GridBagLayout());
        final GridBagConstraints gbc = new GridBagConstraints();
        
        // Padding and margin
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Create and position JList
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        projectList = new JList<>();
        projectList.setLayoutOrientation(JList.VERTICAL);
        
        // Create the JScrollPane
        final JScrollPane listScroller = new JScrollPane(projectList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        listScroller.setPreferredSize(new Dimension(300, 150));
        panel.add(listScroller, gbc);
        
        // Create and position JButton
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        openBtn = new JButton("Open", new ImageIcon("images/open_folder.png"));
        panel.add(openBtn, gbc);
        
        // Add JPanel to this JFrame
        getContentPane().add(panel);
        
        // Set some properties of this JFrame
        setTitle("Open Project");
        setIconImage(new ImageIcon("images/folder.png").getImage());
        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 300);
        setMinimumSize(new Dimension(600, 300));
        setLocationRelativeTo(null);
    }
    
    public JList<String> getProjectList() {
        return projectList;
    }
    
    public JButton getOpenBtn() {
        return openBtn;
    }
}
