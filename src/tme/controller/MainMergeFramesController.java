package tme.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import tme.view.MainFrame;
import tme.view.MergeFrame;

public final class MainMergeFramesController {
    private final MainFrame mainFrame;
    private final MergeFrame mergeFrame;
    
    public MainMergeFramesController(MainFrame mainFrame, MergeFrame mergeFrame) {
        this.mainFrame = mainFrame;
        this.mergeFrame = mergeFrame;
    }
    
    public void start() {
        // Events management
        mainFrame.getCreateMergeBtn().addActionListener(mainFrameClickMergeBtnEvent());
    }
    
    private ActionListener mainFrameClickMergeBtnEvent() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Show mergeFrame JFrame
                mergeFrame.setVisible(true);
            }
        };
    }
}
