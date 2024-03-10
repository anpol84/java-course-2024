package edu.java.service.updater;

import edu.java.model.Link;


public interface LinkUpdater {
    int process(Link link);

    boolean support(String url);

    String getDomain();

    void setLastUpdate(Link link);
}
