package com.npdi.demo.pagseguro.resources;

import com.npdi.demo.pagseguro.services.PagSeguroNotificationHandlerImp;
import br.com.uol.pagseguro.api.common.domain.DataList;
import br.com.uol.pagseguro.api.common.domain.builder.DateRangeBuilder;
import br.com.uol.pagseguro.api.common.domain.builder.PreApprovalBuilder;
import br.com.uol.pagseguro.api.http.JSEHttpClient;
import br.com.uol.pagseguro.api.notification.NotificationsResource;
import br.com.uol.pagseguro.api.notification.PagSeguroNotificationHandler;
import br.com.uol.pagseguro.api.preapproval.PreApprovalRegistrationBuilder;
import br.com.uol.pagseguro.api.preapproval.RegisteredPreApproval;
import com.npdi.demo.pagseguro.services.PagSeguroService;
import java.math.BigDecimal;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author DD
 */
@RestController
@RequestMapping("/api")
public class PagSeguroResource {

    @Autowired
    PagSeguroService service;

    @GetMapping("/payment")
    public @ResponseBody
    String createCheckout(
            @RequestParam(value = "email", required = true) String email,
            @RequestParam(value = "plan", required = true) String plan
    ) {
        return service.createPayment(email, plan);
    }

    @CrossOrigin(origins = "https://sandbox.pagseguro.uol.com.br")
    @PostMapping("/notification")
    public @ResponseBody
    ResponseEntity registerNotify(
            HttpServletRequest request, HttpServletResponse response) {

        return ResponseEntity.ok(service.registerNotify(request));
    }

    @GetMapping("createPlan")
    private @ResponseBody
    String preApproval() {
        return service.registerPreApproval();
    }

    @GetMapping("searchPlan")
    private @ResponseBody
    DataList searchPreApproval(Date sDate, Date eDate) {
        return service.getPreApproval(sDate, eDate);
    }

}
