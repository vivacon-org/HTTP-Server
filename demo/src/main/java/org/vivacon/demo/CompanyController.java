package org.vivacon.demo;


import org.vivacon.framework.bean.Autowired;
import org.vivacon.framework.web.Controller;
import org.vivacon.framework.web.RequestMapping;

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
