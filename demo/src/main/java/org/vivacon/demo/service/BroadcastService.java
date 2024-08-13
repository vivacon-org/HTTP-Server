package org.vivacon.demo.service;


import org.vivacon.framework.bean.annotations.Component;

@Component
public class BroadcastService {

    public String echo(String message) {
        return message;
    }

}
