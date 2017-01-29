package tme.controller;

import tme.model.DBConnector;
import tme.view.*;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import org.xmldb.api.base.XMLDBException;
import tme.model.Project;

// Handle events in common with all the frames
public final class AllFramesController {
    private final MainFrame mainFrame;
    private final ConnectionFrame connectionFrame;
    private final OpenProjectFrame openProjectFrame;
    private final MergeFrame mergeFrame;
    
    public AllFramesController(MainFrame mainFrame, ConnectionFrame connectionFrame, OpenProjectFrame openProjectFrame, MergeFrame mergeFrame) {
        this.mainFrame = mainFrame;
        this.connectionFrame = connectionFrame;
        this.openProjectFrame = openProjectFrame;
        this.mergeFrame = mergeFrame;
    }
    
    public void start() {
        // Events management
        mainFrame.addWindowListener(mainFrameCloseEvent());
    }
    
    // When the user close mainFrame JFrame
    private WindowAdapter mainFrameCloseEvent() {
        return new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Close other frames
                connectionFrame.dispose();
                mainFrame.dispose();
                openProjectFrame.dispose();
                mergeFrame.dispose();
                
                // Close the current project
                Project.cleanProject();
                
                // Close the connection of the database
                DBConnector.close();
            }
        };
    }
}
