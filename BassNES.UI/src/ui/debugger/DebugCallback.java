package ui.debugger;

public interface DebugCallback {
    Object[] getCPUDebuggingInformation();
    Object[] getPPUDebuggingInformation();
    void doCpuCycle();
    void exitDebugging();
}
