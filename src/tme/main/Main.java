package tme.main;

import tme.controller.*;
import tme.view.*;

// This is the core, it handle just the controllers
public final class Main {
    
    public static void main(String[] args) {
        // Components declaration
        MainFrame mainFrame = new MainFrame();
        ConnectionFrame connectionFrame = new ConnectionFrame();
        OpenProjectFrame openProjectFrame = new OpenProjectFrame();
        NewProjectFrame newProjectFrame = new NewProjectFrame();
        MergeFrame mergeFrame = new MergeFrame();
        
        // Controllers declaration
        MainFrameController mfc = new MainFrameController(mainFrame);
        ConnectionFrameController cfc = new ConnectionFrameController(connectionFrame);
        OpenProjectFrameController opfc = new OpenProjectFrameController(openProjectFrame);
        MergeFrameController merfc = new MergeFrameController(mergeFrame);
        
        MainConnectionFramesController mcfc = new MainConnectionFramesController(mainFrame, connectionFrame);
        MainOpenProjectFramesController mopfc = new MainOpenProjectFramesController(mainFrame, openProjectFrame);
        MainNewProjectFramesController mnpfc = new MainNewProjectFramesController(mainFrame, newProjectFrame);
        MainMergeFramesController mmfc = new MainMergeFramesController(mainFrame, mergeFrame);
        
        AllFramesController afc = new AllFramesController(mainFrame, connectionFrame, openProjectFrame, newProjectFrame, mergeFrame);
        
        // Controllers start
        mfc.start();
        cfc.start();
        opfc.start();
        merfc.start();
  
        mcfc.start();
        mopfc.start();
        mnpfc.start();
        mmfc.start();
        
        afc.start();
    }
}
