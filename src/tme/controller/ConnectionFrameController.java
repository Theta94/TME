package tme.controller;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import tme.model.XMLPreferences;
import tme.view.ConnectionFrame;

// Handle MainForm events
public final class ConnectionFrameController {
    private final ConnectionFrame connectionFrame;
    
    public ConnectionFrameController(ConnectionFrame connectionFrame) {
        this.connectionFrame = connectionFrame;
    }
    
    public void start() {
        // Initializize connectionFrame components
        connectionFrame.getDisconnectBtn().setEnabled(false);
        
        // Events magamenet
        connectionFrame.addWindowListener(connectionFrameOpenedEvent());
    }
    
    // Each time connectionFrame JFrame become visibile
    private WindowAdapter connectionFrameOpenedEvent() {
        return new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                // Fill JTextFields with preferences data
                try {
                    String[] data = XMLPreferences.getConnectionNode();
                    connectionFrame.getURIField().setText(data[0]);
                    connectionFrame.getCollectionField().setText(data[1]);
                } catch (NullPointerException exc) {
                    // Create the instance of the preferences file
                    XMLPreferences.createInstance();
                }
            }
        };
    }
}
