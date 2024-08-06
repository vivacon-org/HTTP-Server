package org.vivacon.demo;


import org.vivacon.framework.bean.annotations.Component;

@Component
public class BroadcastService {

    public String echo(String message) {
        return message;
    }

}
