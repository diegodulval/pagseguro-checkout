package com.npdi.demo.pagseguro.resources;

import com.npdi.demo.pagseguro.domains.Agreement;
import com.npdi.demo.pagseguro.domains.Plan;
import com.npdi.demo.pagseguro.domains.User;
import com.npdi.demo.pagseguro.services.PagSeguroService;
import com.npdi.demo.pagseguro.services.PlanService;
import com.npdi.demo.pagseguro.services.UserService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Dulval
 */
@RestController
@RequestMapping("/api")
public class PagSeguroResource {

    @Autowired
    private PagSeguroService service;

    @Autowired
    private UserService userService;

    @Autowired
    private PlanService planService;

    @GetMapping("/checkoutTransaction")
    public @ResponseBody
    String createTransactionCheckout(
            @RequestParam(value = "email", required = true) String email,
            @RequestParam(value = "plan", required = true) String plan
    ) {
        return service.createCheckout(email, plan);
    }

    @CrossOrigin(origins = "https://sandbox.pagseguro.uol.com.br")
    @PostMapping("/notification")
    public @ResponseBody
    ResponseEntity registerNotify(
            HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(service.registerNotify(request));
    }

    @GetMapping("signatures")
    public @ResponseBody
    Agreement signaturePreApproval(
            @RequestParam(value = "user", required = true) Long userId,
            @RequestParam(value = "plan", required = true) Long planId) {

        User user = userService.readById(userId);
        Plan plan = planService.readById(planId);

        return service.signaturePlan(plan, user);
    }

    @GetMapping("plans")
    public @ResponseBody
    String searchPreApproval() {
        return service.searchPreApproval();
    }

    @PostMapping("plans")
    public @ResponseBody
    Plan createPlan(@RequestBody Plan plan) {
        return planService.create(plan);
    }

}
