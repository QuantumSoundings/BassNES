package ui.input;

import core.NesSettings;
import ui.OSD;
import ui.SystemManager;
import ui.settings.UISettings;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class HotKeyManager implements HotKeyInterface {
    InputMap inputMap;
    ActionMap actionMap;
    HotKeyCallback sys;

    public HotKeyManager(SystemManager s){
        inputMap = new InputMap();
        actionMap = new ActionMap();
        sys = s;

        initializeActionMap();

    }
    @Override
    public InputMap getInputMap() {
        return inputMap;
    }

    @Override
    public ActionMap getActionMap() {
        return actionMap;
    }

    @Override
    public void updateInputMapHotKey(HotKeys key, KeyStroke in) {
        inputMap.put(in, key);
    }
    private void loadInputMap(){

    }
    private void initializeActionMap(){
        Action startCPU = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                sys.doHotKey(HotKeys.StartCPU);
            }
        };
        Action loadRom = new AbstractAction(){
            public void actionPerformed(ActionEvent e) {
                sys.doHotKey(HotKeys.LoadRom);
            }
        };
        Action autoLoad = new AbstractAction(){
            public void actionPerformed(ActionEvent e) {
                UISettings.autoLoad = !UISettings.autoLoad;
            }
        };
        Action showFPS = new AbstractAction(){
            public void actionPerformed(ActionEvent e) {
                UISettings.ShowFPS = !UISettings.ShowFPS;
            }
        };
        Action saveState0= new AbstractAction(){
            public void actionPerformed(ActionEvent arg0) {
                sys.doHotKey(HotKeys.SaveState0);
            }
        };
        Action saveState1= new AbstractAction(){
            public void actionPerformed(ActionEvent arg0) {
                sys.doHotKey(HotKeys.SaveState1);
            }
        };
        Action saveState2= new AbstractAction(){
            public void actionPerformed(ActionEvent arg0) {
                sys.doHotKey(HotKeys.SaveState2);
            }
        };
        Action saveState3= new AbstractAction(){
            public void actionPerformed(ActionEvent arg0) {
                sys.doHotKey(HotKeys.SaveState3);
            }
        };
        Action saveState4= new AbstractAction(){
            public void actionPerformed(ActionEvent arg0) {
                sys.doHotKey(HotKeys.SaveState4);
            }
        };
        Action saveState5= new AbstractAction(){
            public void actionPerformed(ActionEvent arg0) {
                sys.doHotKey(HotKeys.SaveState5);
            }
        };
        Action saveState6= new AbstractAction(){
            public void actionPerformed(ActionEvent arg0) {
                sys.doHotKey(HotKeys.SaveState6);
            }
        };
        Action saveState7= new AbstractAction(){
            public void actionPerformed(ActionEvent arg0) {
                sys.doHotKey(HotKeys.SaveState7);
            }
        };
        Action saveState8= new AbstractAction(){
            public void actionPerformed(ActionEvent arg0) {
                sys.doHotKey(HotKeys.SaveState8);
            }
        };
        Action saveState9= new AbstractAction(){
            public void actionPerformed(ActionEvent arg0) {
                sys.doHotKey(HotKeys.SaveState9);
            }
        };
        Action loadState0= new AbstractAction(){
            public void actionPerformed(ActionEvent e) {
                sys.doHotKey(HotKeys.LoadState0);
            }
        };
        Action loadState1= new AbstractAction(){
            public void actionPerformed(ActionEvent e) {
                sys.doHotKey(HotKeys.LoadState1);
            }
        };
        Action loadState2= new AbstractAction(){
            public void actionPerformed(ActionEvent e) {
                sys.doHotKey(HotKeys.LoadState2);
            }
        };
        Action loadState3= new AbstractAction(){
            public void actionPerformed(ActionEvent e) {
                sys.doHotKey(HotKeys.LoadState3);
            }
        };
        Action loadState4= new AbstractAction(){
            public void actionPerformed(ActionEvent e) {
                sys.doHotKey(HotKeys.LoadState4);
            }
        };
        Action loadState5= new AbstractAction(){
            public void actionPerformed(ActionEvent e) {
                sys.doHotKey(HotKeys.LoadState5);
            }
        };
        Action loadState6= new AbstractAction(){
            public void actionPerformed(ActionEvent e) {
                sys.doHotKey(HotKeys.LoadState6);
            }
        };
        Action loadState7= new AbstractAction(){
            public void actionPerformed(ActionEvent e) {
                sys.doHotKey(HotKeys.LoadState7);
            }
        };
        Action loadState8= new AbstractAction(){
            public void actionPerformed(ActionEvent e) {
                sys.doHotKey(HotKeys.LoadState8);
            }
        };
        Action loadState9= new AbstractAction(){
            public void actionPerformed(ActionEvent e) {
                sys.doHotKey(HotKeys.LoadState9);
            }
        };
        Action volumeUp = new AbstractAction(){
            public void actionPerformed(ActionEvent e) {
                NesSettings.masterMixLevel = Math.min(100, NesSettings.masterMixLevel+5);
                OSD.addOSDMessage("Master volume: "+NesSettings.masterMixLevel+"%", 120);
            }
        };
        Action volumeDown = new AbstractAction(){
            public void actionPerformed(ActionEvent e) {
                NesSettings.masterMixLevel = Math.max(0, NesSettings.masterMixLevel-5);
                OSD.addOSDMessage("Master volume: "+NesSettings.masterMixLevel+"%", 120);
            }
        };
        actionMap.put(HotKeys.LoadRom,loadRom);
        actionMap.put(HotKeys.AutoLoad,autoLoad);
        actionMap.put(HotKeys.ShowFPS,showFPS);

        actionMap.put(HotKeys.SaveState0,saveState0);
        actionMap.put(HotKeys.SaveState1,saveState1);
        actionMap.put(HotKeys.SaveState2,saveState2);
        actionMap.put(HotKeys.SaveState3,saveState3);
        actionMap.put(HotKeys.SaveState4,saveState4);
        actionMap.put(HotKeys.SaveState5,saveState5);
        actionMap.put(HotKeys.SaveState6,saveState6);
        actionMap.put(HotKeys.SaveState7,saveState7);
        actionMap.put(HotKeys.SaveState8,saveState8);
        actionMap.put(HotKeys.SaveState9,saveState9);

        actionMap.put(HotKeys.LoadState0,loadState0);
        actionMap.put(HotKeys.LoadState1,loadState1);
        actionMap.put(HotKeys.LoadState2,loadState2);
        actionMap.put(HotKeys.LoadState3,loadState3);
        actionMap.put(HotKeys.LoadState4,loadState4);
        actionMap.put(HotKeys.LoadState5,loadState5);
        actionMap.put(HotKeys.LoadState6,loadState6);
        actionMap.put(HotKeys.LoadState7,loadState7);
        actionMap.put(HotKeys.LoadState8,loadState8);
        actionMap.put(HotKeys.LoadState9,loadState9);

        actionMap.put(HotKeys.VolumeDown,volumeDown);
        actionMap.put(HotKeys.VolumeUp,volumeUp);
    }
}
