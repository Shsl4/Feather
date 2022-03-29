package dev.sl4sh.feather;

public interface Service {

    void loadConfiguration();

    void writeConfiguration();

    boolean getServiceState();

}
