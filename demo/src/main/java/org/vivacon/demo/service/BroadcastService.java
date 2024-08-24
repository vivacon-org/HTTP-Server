package org.vivacon.demo.service;


import org.vivacon.demo.event.NewVideoPublishEvent;
import org.vivacon.framework.bean.annotations.Component;
import org.vivacon.framework.event.EventBroker;

@Component
public class BroadcastService {

    public String saveVideo(String nameVideo, String url) {
        EventBroker.getInstance().publish(new NewVideoPublishEvent(nameVideo, url));
        return nameVideo + url;
    }

}
