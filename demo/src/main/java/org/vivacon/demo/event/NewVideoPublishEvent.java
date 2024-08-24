package org.vivacon.demo.event;

import org.vivacon.framework.event.Event;

public class NewVideoPublishEvent implements Event {
    private final String nameVideo;
     private final String url;

    public NewVideoPublishEvent(String nameVideo, String url) {
        this.nameVideo = nameVideo;
        this.url = url;
    }
}
