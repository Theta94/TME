package tme.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public final class NewProjectFrame extends JFrame {
    private final JButton createBtn;
    private final JTextField nameField;
    
    public NewProjectFrame() {
        final JPanel panel = new JPanel(new GridBagLayout());
        final GridBagConstraints gbc = new GridBagConstraints();
        
        // Padding and margin
        gbc.insets = new Insets(10, 10, 10, 10);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(200, 20));
        panel.add(nameField, gbc);
        
        // Create and position JButton
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        createBtn = new JButton("Create", new ImageIcon("images/add.png"));
        panel.add(createBtn, gbc);
        
        // Add JPanel to this JFrame
        getContentPane().add(panel);
        
        // Set some properties of this JFrame
        setTitle("New Project");
        setIconImage(new ImageIcon("images/folder_add.png").getImage());
        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 150);
        setMinimumSize(new Dimension(400, 100));
        setLocationRelativeTo(null);
    }
    
    public JTextField getNameField() {
        return nameField;
    }
    
    public JButton getCreateBtn() {
        return createBtn;
    }
}
