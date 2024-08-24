package org.vivacon.demo.service;


import org.vivacon.framework.bean.annotation.Component;

@Component
public class BroadcastService {

    public String echo(String message) {
        return message;
    }

}
