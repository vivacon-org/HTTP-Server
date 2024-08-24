package org.vivacon.demo.service;

import org.vivacon.demo.event.NewVideoPublishEvent;
import org.vivacon.framework.bean.annotation.Service;
import org.vivacon.framework.event.annotation.EventListener;

@Service
public class SMSNotificationService {
    @EventListener
    public void onNewVideoPublish(NewVideoPublishEvent newVideoPublishEvent){
    }
}
