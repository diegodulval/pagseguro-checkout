package com.npdi.demo.pagseguro.resources;

import br.com.uol.pagseguro.api.PagSeguro;
import br.com.uol.pagseguro.api.PagSeguroEnv;
import br.com.uol.pagseguro.api.checkout.CheckoutRegistration;
import br.com.uol.pagseguro.api.checkout.CheckoutRegistrationBuilder;
import br.com.uol.pagseguro.api.checkout.RegisteredCheckout;
import br.com.uol.pagseguro.api.common.domain.builder.AcceptedPaymentMethodsBuilder;
import br.com.uol.pagseguro.api.common.domain.builder.ConfigBuilder;
import br.com.uol.pagseguro.api.common.domain.builder.PaymentItemBuilder;
import br.com.uol.pagseguro.api.common.domain.builder.PaymentMethodBuilder;
import br.com.uol.pagseguro.api.common.domain.builder.PaymentMethodConfigBuilder;
import br.com.uol.pagseguro.api.common.domain.builder.PhoneBuilder;
import br.com.uol.pagseguro.api.common.domain.builder.SenderBuilder;
import br.com.uol.pagseguro.api.common.domain.enums.ConfigKey;
import br.com.uol.pagseguro.api.common.domain.enums.Currency;
import br.com.uol.pagseguro.api.common.domain.enums.PaymentMethodGroup;
import br.com.uol.pagseguro.api.credential.Credential;
import br.com.uol.pagseguro.api.http.JSEHttpClient;
import br.com.uol.pagseguro.api.notification.NotificationsResource;
import br.com.uol.pagseguro.api.notification.PagSeguroNotificationHandler;
import br.com.uol.pagseguro.api.utils.Builder;
import br.com.uol.pagseguro.api.utils.logging.SimpleLoggerFactory;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @GetMapping("/payment")
    public @ResponseBody
    String createCheckout() {
        try {

            final PagSeguro pagSeguro = PagSeguro
                    .instance(new SimpleLoggerFactory(), new JSEHttpClient(),
                            getCredentials(), PagSeguroEnv.SANDBOX);

            RegisteredCheckout registeredCheckout = pagSeguro.checkouts().register(getCheckoutRespurces());

            return registeredCheckout.getRedirectURL();

        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private SenderBuilder getSender() {

        SenderBuilder sender = new SenderBuilder()//
                .withEmail("c48491423398757312715@sandbox.pagseguro.com.br")//
                .withName("Mulato Urbano")
                .withCPF("03316526170")
                .withPhone(new PhoneBuilder()//
                        .withAreaCode("99") //
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

        final PagSeguro pagSeguro = PagSeguro
                .instance(new SimpleLoggerFactory(), new JSEHttpClient(),
                        getCredentials(), PagSeguroEnv.SANDBOX);

        PagSeguroNotificationHandler nHandler = new PagSeguroNotificationHandlerImp();

        NotificationsResource nResource = new NotificationsResource(pagSeguro, new JSEHttpClient());

        nResource.handle(request, nHandler);

        return ResponseEntity.ok(response);
    }

    private Credential getCredentials() {
        return Credential.sellerCredential(sellerEmail, sellerToken);
    }

    private Builder<CheckoutRegistration> getCheckoutRespurces() {
        return new CheckoutRegistrationBuilder()
                .withCurrency(Currency.BRL)
                //.withExtraAmount(BigDecimal.ONE) 
                .withReference("CODE-TESTE")
                .withSender(getSender())
                .addItem(getItems());
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
}
