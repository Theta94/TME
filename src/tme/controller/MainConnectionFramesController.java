package tme.controller;

import java.awt.Color;

import tme.view.MainFrame;
import tme.view.ConnectionFrame;
import tme.model.DBConnector;
import tme.model.XMLPreferences;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.xmldb.api.base.Database;
import tme.model.Project;

// Handle events between MainForm and ConnectionForm
public final class MainConnectionFramesController {
    private final MainFrame mainFrame;
    private final ConnectionFrame connectionFrame;
    
    public MainConnectionFramesController(MainFrame mainFrame, ConnectionFrame connectionFrame) {
        this.mainFrame = mainFrame;
        this.connectionFrame = connectionFrame;
    }
    
    public void start() {
        // Events management
        mainFrame.getConnectionItem().addActionListener(mainFrameClickConnectionItemEvent());
        
        connectionFrame.getConnectBtn().addActionListener(connectionFrameClickConnectBtnEvent());
        connectionFrame.getDisconnectBtn().addActionListener(connectionFrameClickDisconnectBtnEvent());
    }
    
    // When the user clicks on Database JMenu
    private ActionListener mainFrameClickConnectionItemEvent() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Show connectionFrame
                connectionFrame.setVisible(true);
            }
        };
    }
    
    // When the user clicks on Connect JButton
    private ActionListener connectionFrameClickConnectBtnEvent() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Check if JTextFields aren't empty
                if (!connectionFrame.getURIField().getText().equals("") && 
                    !connectionFrame.getCollectionField().getText().equals("")) {
                    // Open a connection with the database
                    Database database = DBConnector.getInstance();
                    // Check if the database was opened correctly
                    // Insert JTextFields data into DBConnector attributes and check it
                    if (database != null && 
                        DBConnector.setCollection(connectionFrame.getURIField().getText(), connectionFrame.getCollectionField().getText())) {
                        // Disable Connect JButton and enable Disconnect JButton
                        connectionFrame.getConnectBtn().setEnabled(false);
                        connectionFrame.getSaveBox().setEnabled(false);
                        connectionFrame.getDisconnectBtn().setEnabled(true);
                        
                        // Show the JLabel
                        connectionFrame.getOutputLabel().setText("Connection has been established with success.");
                        connectionFrame.getOutputLabel().setForeground(new Color(0, 180, 0));
                        connectionFrame.getOutputLabel().setVisible(true);
                        
                        // Enable JMenuItems of project JMenu
                        mainFrame.getNewProjectItem().setEnabled(true);
                        mainFrame.getOpenProjectItem().setEnabled(true);
                        
                        // Check if saveBox JCheckBox is checked
                        if (connectionFrame.getSaveBox().isSelected())
                            XMLPreferences.setConnectionNode(connectionFrame.getURIField().getText(), connectionFrame.getCollectionField().getText());
                        else
                            XMLPreferences.setConnectionNode("", "");
                    } else {
                        // Show the JLabel
                        connectionFrame.getOutputLabel().setText("<html>Impossible to establish a connection with the database or recover the specified collection."
                                                               + "<br>Make sure you started eXistdb and specified a valid URI and collection.</html>");
                        connectionFrame.getOutputLabel().setForeground(Color.red);
                        connectionFrame.getOutputLabel().setVisible(true);
                    }
                } else {
                    // Show the JLabel
                    connectionFrame.getOutputLabel().setText("You must fill each field.");
                    connectionFrame.getOutputLabel().setForeground(Color.red);
                    connectionFrame.getOutputLabel().setVisible(true);
                }
            }
        };
    }
    
    // When the user clicks on Disconnect JButton
    private ActionListener connectionFrameClickDisconnectBtnEvent() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Close any opened collection from the database
                DBConnector.close();
                Project.cleanProject();

                // Show the JLabel
                connectionFrame.getOutputLabel().setText("Connection has been closed with success.");
                connectionFrame.getOutputLabel().setForeground(new Color(0, 180, 0));
                connectionFrame.getOutputLabel().setVisible(true);
                
                // Disable project JMenuItems
                mainFrame.getNewProjectItem().setEnabled(false);
                mainFrame.getOpenProjectItem().setEnabled(false);
                mainFrame.getCloseProjectItem().setEnabled(false);
                
                // Disable disconnect JButton
                connectionFrame.getDisconnectBtn().setEnabled(false);
                
                // Enable JCheckBox and connect JButton
                connectionFrame.getSaveBox().setEnabled(true);
                connectionFrame.getConnectBtn().setEnabled(true);
                
                // Set the standard title for the mainFrame
                mainFrame.setTitle("Test Management Environment");
                // Close JTabbedPane
                mainFrame.getTabbedPane().setVisible(false);
            }
        };
    }
}
