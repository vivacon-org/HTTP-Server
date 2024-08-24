package org.vivacon.demo.service;

import org.vivacon.demo.event.NewVideoPublishEvent;
import org.vivacon.framework.bean.annotations.Service;
import org.vivacon.framework.event.annotation.EventListener;

@Service
public class EmailNotificationService {
    @EventListener
    public void onNewVideoPublish(NewVideoPublishEvent newVideoPublishEvent){
        //send to subscriber youtube (handle when event is publish)
    }
}
