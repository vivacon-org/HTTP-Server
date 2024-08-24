package org.vivacon.server;

import org.vivacon.server.life_cycle.LifeCycle;

public interface Server extends LifeCycle {
    int getPort();
}
