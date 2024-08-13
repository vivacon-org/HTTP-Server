package org.vivacon.framework.core.event;

import java.io.Serializable;

public interface Event extends Serializable {

    void handleEvent(Event e);
}
