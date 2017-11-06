package core;

public class NESBuilder {
    public static NES buildNes(NESCallback sys){
        return new DefaultNES(sys);
    }
}
