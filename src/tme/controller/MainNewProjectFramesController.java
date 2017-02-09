package tme.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JOptionPane;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.CollectionManagementService;
import tme.view.MainFrame;
import tme.view.NewProjectFrame;
import tme.model.Project;
import tme.model.DBConnector;
import tme.model.XMLPreferences;

public final class MainNewProjectFramesController {
    private final MainFrame mainFrame;
    private final NewProjectFrame newProjectFrame;
    
    public MainNewProjectFramesController(MainFrame mainFrame, NewProjectFrame newProjectFrame) {
        this.mainFrame = mainFrame;
        this.newProjectFrame = newProjectFrame;
    }
    
    public void start() {
        // Events management
            mainFrame.getNewProjectItem().addActionListener(mainFrameClickNewProjectItemEvent());
            newProjectFrame.getCreateBtn().addActionListener(newProjectFrameClickCreateBtnEvent());
            newProjectFrame.addWindowListener(newProjectFrameClosed());
    }
    
    private ActionListener mainFrameClickNewProjectItemEvent() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Show openProjectFrame JFrame
                newProjectFrame.setVisible(true);
            }
        };
    }
    
    private ActionListener newProjectFrameClickCreateBtnEvent() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!newProjectFrame.getNameField().getText().equals("")) {
                    
                    try {
                        Collection check = DatabaseManager.getCollection(DBConnector.getURI() + DBConnector.getCollection().getName());
                        for (int i = 0; i < check.getChildCollectionCount(); i++)
                            if (check.getChildCollection(newProjectFrame.getNameField().getText()) != null) {
                                JOptionPane.showMessageDialog(null, "This Collection already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                    } catch (XMLDBException exc) {
                         JOptionPane.showMessageDialog(null, "An error occurred while creating the new project.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    try {
                        Collection root = DatabaseManager.getCollection(DBConnector.getURI() + DBConnector.getCollection().getName());
                        CollectionManagementService cms = (CollectionManagementService)root.getService("CollectionManagementService", "1.0");
                        cms.createCollection(newProjectFrame.getNameField().getText());
                        
                        root = DatabaseManager.getCollection(DBConnector.getURI() + DBConnector.getCollection().getName() + "/" + newProjectFrame.getNameField().getText());
                        cms = (CollectionManagementService)root.getService("CollectionManagementService", "1.0");
                        cms.createCollection("input");
                        cms.createCollection("output");
                        
                        root = DatabaseManager.getCollection(DBConnector.getURI() + DBConnector.getCollection().getName() + "/" + newProjectFrame.getNameField().getText() + "/input");
                        cms = (CollectionManagementService)root.getService("CollectionManagementService", "1.0");
                        cms.createCollection("churn");
                        cms.createCollection("merge");
                        cms.createCollection("report");
                        cms.createCollection("report_compact");
                        
                        root = DatabaseManager.getCollection(DBConnector.getURI() + DBConnector.getCollection().getName() + "/" + newProjectFrame.getNameField().getText() + "/output");
                        cms = (CollectionManagementService)root.getService("CollectionManagementService", "1.0");
                        cms.createCollection("Coverage");
                        cms.createCollection("Metrics");
                        cms.createCollection("Prioritization");
                        
                        newProjectFrame.dispose();
                        JOptionPane.showMessageDialog(null, "Project has been created with success!", "Done", JOptionPane.INFORMATION_MESSAGE);
                    } catch (XMLDBException exc) {
                        JOptionPane.showMessageDialog(null, "An error occurred while creating the new project.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        };
    }
    
    // When mainFrame JFrame is visible for the first time
    private WindowAdapter newProjectFrameClosed() {
        return new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                newProjectFrame.getNameField().setText("");
            }
        };
    }
}