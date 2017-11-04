package ui.input;

import javax.swing.*;

public interface HotKeyInterface {

    enum HotKeys{SaveState0,SaveState1,SaveState2,SaveState3,SaveState4,SaveState5,SaveState6,SaveState7,SaveState8,SaveState9,
                 LoadState0,LoadState1,LoadState2,LoadState3,LoadState4,LoadState5,LoadState6,LoadState7,LoadState8,LoadState9,
                 StartCPU,Reset,VolumeUp,VolumeDown,AutoLoad,ShowFPS,LoadRom,PauseCPU};
    InputMap getInputMap();

    ActionMap getActionMap();

    void updateInputMapHotKey(HotKeys key, KeyStroke in);

}
