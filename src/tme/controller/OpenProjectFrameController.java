package tme.controller;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.TreeSet;
import javax.swing.DefaultListModel;
import org.xmldb.api.base.XMLDBException;
import tme.view.OpenProjectFrame;
import tme.model.DBConnector;

// Handle OpenProjectForm events
public final class OpenProjectFrameController {
    private final OpenProjectFrame openProjectFrame;
    
    public OpenProjectFrameController(OpenProjectFrame openProjectFrame) {
        this.openProjectFrame = openProjectFrame;
    }
    
    public void start() {
        // Events management
        openProjectFrame.addWindowListener(openProjectFrameOpenedEvent());
    }
    
    private WindowAdapter openProjectFrameOpenedEvent() {
        return new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                try {
                    // Recover all the projects from the current root collection
                    String[] projects = DBConnector.getCollection().listChildCollections();
                    TreeSet<String> projectList = new TreeSet<>();
                    
                    // Exchange from array to list
                    for (String s : projects)
                        projectList.add(s);
                    projectList.remove("_query");
                    
                    // Create the model and fill it with projects
                    DefaultListModel<String> model = new DefaultListModel<>();
                    for (String s : projectList)
                        model.addElement(s);
                    
                    // Fill JList with the model
                    openProjectFrame.getProjectList().setModel(model);
                    openProjectFrame.getProjectList().setSelectedIndex(0);
                } catch (XMLDBException exc) {
                    
                }
            }
        };
    }
}
