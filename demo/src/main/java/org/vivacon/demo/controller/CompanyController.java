package org.vivacon.demo.controller;


import org.vivacon.demo.service.BroadcastService;
import org.vivacon.framework.bean.annotation.Autowired;
import org.vivacon.framework.web.annotation.RequestMapping;
import org.vivacon.framework.web.annotation.RestController;

@RestController
@RequestMapping(path = "/")
public class CompanyController {

    private BroadcastService broadcastService;

    @Autowired
    public CompanyController(BroadcastService broadcastService) {
        this.broadcastService = broadcastService;
    }

    @RequestMapping(path = "department")
    public String getAllDepartments() {
        return this.broadcastService.echo("department");
    }

    @RequestMapping(path = "employee")
    public String getAllEmployee() {
        return this.broadcastService.echo("Employee");
    }

}
