package ui.input;

public interface ControllerInterface {

    enum ControllerButtons {A,B,Select,Start,Up,Down,Left,Right};
    enum QuickKeyButtons {SaveState,LoadState,InputRecord,InputPlay,AudioRecord};

    void setControllerButton(int controllerNumber, ControllerButtons button);

    void setQuickKeyButton(QuickKeyButtons button);

    String getControllerButtonIdName(int controllerNumber, ControllerButtons button);

    String getQuickKeyButtonIdName(QuickKeyButtons button);

    void configureControllerInput(int controllerNumber,ControllerButtons button, String config);

    void configureQuickKeyInput(QuickKeyButtons button, String config);

    String getControllerInputConfig(int controllerNumber, ControllerButtons button);

    String getQuickKeyInputConfig(QuickKeyButtons button);

    boolean[][] getCurrentControllerOutputs();

    boolean[] getCurrentQuickKeyOutputs();

    void updateInputs();




}
