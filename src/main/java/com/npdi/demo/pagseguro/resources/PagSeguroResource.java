package com.npdi.demo.pagseguro.resources;

import br.com.uol.pagseguro.api.PagSeguro;
import br.com.uol.pagseguro.api.PagSeguroEnv;
import br.com.uol.pagseguro.api.checkout.CheckoutRegistration;
import br.com.uol.pagseguro.api.checkout.CheckoutRegistrationBuilder;
import br.com.uol.pagseguro.api.checkout.RegisteredCheckout;
import br.com.uol.pagseguro.api.common.domain.DataList;
import br.com.uol.pagseguro.api.common.domain.builder.AcceptedPaymentMethodsBuilder;
import br.com.uol.pagseguro.api.common.domain.builder.ConfigBuilder;
import br.com.uol.pagseguro.api.common.domain.builder.DateRangeBuilder;
import br.com.uol.pagseguro.api.common.domain.builder.PaymentItemBuilder;
import br.com.uol.pagseguro.api.common.domain.builder.PaymentMethodBuilder;
import br.com.uol.pagseguro.api.common.domain.builder.PaymentMethodConfigBuilder;
import br.com.uol.pagseguro.api.common.domain.builder.PhoneBuilder;
import br.com.uol.pagseguro.api.common.domain.builder.PreApprovalBuilder;
import br.com.uol.pagseguro.api.common.domain.builder.SenderBuilder;
import br.com.uol.pagseguro.api.common.domain.enums.ConfigKey;
import br.com.uol.pagseguro.api.common.domain.enums.Currency;
import br.com.uol.pagseguro.api.common.domain.enums.PaymentMethodGroup;
import br.com.uol.pagseguro.api.credential.Credential;
import br.com.uol.pagseguro.api.http.JSEHttpClient;
import br.com.uol.pagseguro.api.notification.NotificationsResource;
import br.com.uol.pagseguro.api.notification.PagSeguroNotificationHandler;
import br.com.uol.pagseguro.api.preapproval.PreApprovalRegistrationBuilder;
import br.com.uol.pagseguro.api.preapproval.RegisteredPreApproval;
import br.com.uol.pagseguro.api.utils.Builder;
import br.com.uol.pagseguro.api.utils.logging.SimpleLoggerFactory;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
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

    private final String sellerEmail = "dulval@email.com";
    private final String sellerToken = "19548637EC994DF58E5A028D38CA1E58";

    private final PagSeguro pagSeguro = PagSeguro
            .instance(new SimpleLoggerFactory(), new JSEHttpClient(),
                    getCredentials(), PagSeguroEnv.SANDBOX);

    @GetMapping("/payment")
    public @ResponseBody
    String createCheckout(
            @RequestParam(value = "email", required = true) String email,
            @RequestParam(value = "plan", required = true) String plan
    ) {
        try {
            RegisteredCheckout registeredCheckout
                    = pagSeguro
                            .checkouts()
                            .register(getCheckoutResources(email, plan));
            return registeredCheckout.getRedirectURL();
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private SenderBuilder getSender(String email) {

        //Get user in database
        //User user = userService.getUserByEmail(email);
        
        SenderBuilder sender = new SenderBuilder()
                .withEmail("c48491423398757312715@sandbox.pagseguro.com.br")//
                .withName("Mulato Urbano")
                //.withCPF("12679485666")
                .withPhone(new PhoneBuilder()
                        .withAreaCode("99")
                        .withNumber("99999999"));

        return sender;
    }

    private PaymentItemBuilder getItems() {

        return new PaymentItemBuilder()
                .withId("0001")
                .withDescription("Produto #1")
                .withAmount(new BigDecimal(99999.99))
                .withQuantity(1);
    }

    @CrossOrigin(origins = "https://sandbox.pagseguro.uol.com.br")
    @PostMapping("/notification")
    public @ResponseBody
    ResponseEntity registerNotify(
            HttpServletRequest request, HttpServletResponse response) {

        PagSeguroNotificationHandler nHandler = new PagSeguroNotificationHandlerImp();

        NotificationsResource nResource = new NotificationsResource(pagSeguro, new JSEHttpClient());

        nResource.handle(request, nHandler);

        return ResponseEntity.ok(response);
    }

    private Credential getCredentials() {
        return Credential.sellerCredential(sellerEmail, sellerToken);
    }

    private Builder<CheckoutRegistration> getCheckoutResources(String email, String plan) {

        return new CheckoutRegistrationBuilder()
                .withCurrency(Currency.BRL)
                .withReference("CODE-TESTE")
                .withSender(getSender(email))
                .addItem(getItems())
                .withPreApproval(createPreApproval());

        //.addPaymentMethodConfig(getPaymentMethodConfig());
        //.withAcceptedPaymentMethods(getPaymentMethod())
    }

    private AcceptedPaymentMethodsBuilder getPaymentMethod() {
        return new AcceptedPaymentMethodsBuilder()
                .addInclude(new PaymentMethodBuilder()
                        .withGroup(PaymentMethodGroup.BALANCE)
                )
                .addInclude(new PaymentMethodBuilder()
                        .withGroup(PaymentMethodGroup.BANK_SLIP)
                );
    }

    private PaymentMethodConfigBuilder getPaymentMethodConfig() {
        return new PaymentMethodConfigBuilder()
                .withPaymentMethod(new PaymentMethodBuilder()
                        .withGroup(PaymentMethodGroup.CREDIT_CARD)
                )
                .withConfig(new ConfigBuilder()
                        .withKey(ConfigKey.MAX_INSTALLMENTS_LIMIT)
                        .withValue(new BigDecimal(1))
                );
    }

    @GetMapping("createPlan")
    private @ResponseBody
    String preApproval() {

        try {

            RegisteredPreApproval registeredPreApproval
                    = pagSeguro
                            .preApprovals()
                            .register(
                                    new PreApprovalRegistrationBuilder()
                                            .withPreApproval(new PreApprovalBuilder()
                                                    .withCharge("manual")
                                                    .withName("BASIC - 3 Meses")
                                                    .withDetails("Cada dia 10 será cobrado o valor de R$100,00 referente ao plano basico ")
                                                    .withAmountPerPayment(BigDecimal.TEN)
                                                    //.withMaxTotalAmount(new BigDecimal(200))
                                                    .withMaxAmountPerPeriod(BigDecimal.TEN)
                                                    .withMaxPaymentsPerPeriod(3)
                                                    .withPeriod("monthly")
                                                    .withDateRange(new DateRangeBuilder()
                                                            .between(
                                                                    new Date(),
                                                                    DatatypeConverter.parseDateTime("2018-08-09T23:59:59.000-03:00")
                                                                            .getTime())
                                                    )));

            return registeredPreApproval.getPreApprovalCode();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("searchPlan")
    private @ResponseBody
    DataList searchPreApproval(Date sDate, Date eDate) {

        try {

            // Busca de assinaturas
            DataList dataList = pagSeguro.preApprovals().search().byDateRange(
                    new DateRangeBuilder().between(sDate, eDate), 1, 10);

            return dataList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }

    }

    private PreApprovalBuilder createPreApproval() {

        Calendar now = Calendar.getInstance();
        Calendar ends = Calendar.getInstance();
        ends.add(Calendar.MONTH, 3);

        return new PreApprovalBuilder()
                .withCharge("manual")
                .withName("BASIC - 3 Meses")
                .withDetails("Cada dia 10 será cobrado o valor de R$10,00 referente ao plano basico ")
                .withAmountPerPayment(BigDecimal.TEN)
                .withMaxTotalAmount(new BigDecimal(200))
                .withMaxAmountPerPeriod(BigDecimal.TEN)
                .withMaxPaymentsPerPeriod(3)
                .withPeriod("monthly")
                .withDateRange(new DateRangeBuilder()
                        .between(
                                now.getTime(),
                                ends.getTime()
                        ));
    }

}
