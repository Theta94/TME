package tme.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public final class ConnectionFrame extends JFrame {
    private final JTextField uriField, collectionField;
    private final JButton connectBtn, disconnectBtn;
    private final JCheckBox saveBox;
    private final JLabel outputLabel;
    
    public ConnectionFrame() {
        // Inner declarations
        final JPanel panel = new JPanel(new GridBagLayout());
        final GridBagConstraints gbc = new GridBagConstraints();
        
        // Padding and margin
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Create and position inner JLabels
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(new JLabel("URI:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Collection:"), gbc);
        
        // Create and position JTextFields
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.LINE_START;
        uriField = new JTextField(30);
        collectionField = new JTextField(30);
        panel.add(uriField, gbc);
        gbc.gridy++;
        panel.add(collectionField, gbc);
        
        // Create and position JButtons and JCheckBox
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        connectBtn = new JButton("Connect", new ImageIcon("images/cable.png"));
        disconnectBtn = new JButton("Disconnect", new ImageIcon("images/disconnect.png"));
        saveBox = new JCheckBox("Save data", true);
        panel.add(saveBox, gbc);
        gbc.gridx++;
        panel.add(connectBtn, gbc);
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(disconnectBtn, gbc);
        
        // Create and position JLabel
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        outputLabel = new JLabel();
        panel.add(outputLabel, gbc);
        
        // Add JPanel to this JFrame
        getContentPane().add(panel);
        
        // Set some properties of this JFrame
        setTitle("Database Connection");
        setIconImage(new ImageIcon("images/database.png").getImage());
        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 300);
        setMinimumSize(new Dimension(600, 300));
        setLocationRelativeTo(null);
    }
    
    public JTextField getURIField() {
        return uriField;
    }
    
    public JTextField getCollectionField() {
        return collectionField;
    }
    
    public JButton getConnectBtn() {
        return connectBtn;
    }
    
    public JButton getDisconnectBtn() {
        return disconnectBtn;
    }
    
    public JCheckBox getSaveBox() {
        return saveBox;
    }
    
    public JLabel getOutputLabel() {
        return outputLabel;
    }
}
