package org.vivacon.demo;

import org.vivacon.framework.Controller;
import org.vivacon.framework.RequestMapping;

@Controller
@RequestMapping(path = "/")
public class CompanyController {

    private BroadcastService broadcastService;

    @RequestMapping(path = "department")
    public String getAllDepartments() {
        return this.broadcastService.echo("department");
    }

    @RequestMapping(path = "employee")
    public String getAllEmployee() {
        return this.broadcastService.echo("Employee");
    }

}
