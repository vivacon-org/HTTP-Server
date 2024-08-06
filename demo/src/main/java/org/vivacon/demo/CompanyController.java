package org.vivacon.demo;


import org.vivacon.framework.bean.annotations.Autowired;
import org.vivacon.framework.web.annotations.Controller;
import org.vivacon.framework.web.annotations.RequestMapping;

@Controller
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
