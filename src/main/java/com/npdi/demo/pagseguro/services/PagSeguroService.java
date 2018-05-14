package com.npdi.demo.pagseguro.services;

import br.com.uol.pagseguro.api.PagSeguro;
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
import br.com.uol.pagseguro.api.http.JSEHttpClient;
import br.com.uol.pagseguro.api.notification.NotificationsResource;
import br.com.uol.pagseguro.api.notification.PagSeguroNotificationHandler;
import br.com.uol.pagseguro.api.preapproval.PreApprovalRegistrationBuilder;
import br.com.uol.pagseguro.api.preapproval.RegisteredPreApproval;
import br.com.uol.pagseguro.api.utils.Builder;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author DD
 */
@Service
public class PagSeguroService {

    @Autowired
    private PagSeguro pagSeguro;

    public String createCheckout(String email, String plan, String type) {
        try {
            RegisteredCheckout registeredCheckout
                    = pagSeguro
                            .checkouts()
                            .register(registerCheckout(email, plan, type));

            return registeredCheckout.getRedirectURL();
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private Builder<CheckoutRegistration> registerCheckout(String email, String plan, String type) {
        CheckoutRegistrationBuilder check = new CheckoutRegistrationBuilder()
                .withCurrency(Currency.BRL)
                .withReference("TRAS-0001")
                .withSender(getSender(email))
                .addItem(getItems());

        if ("PREAP".equals(type)) {
            check.withPreApproval(instantiatePreApproval());
        }
        //.addPaymentMethodConfig(getPaymentMethodConfig());
        //.withAcceptedPaymentMethods(getPaymentMethod())
        return check;
    }

    public String registerNotify(HttpServletRequest request) {

        PagSeguroNotificationHandler nHandler = new PagSeguroNotificationHandlerImp();

        NotificationsResource nResource = new NotificationsResource(pagSeguro, new JSEHttpClient());

        nResource.handle(request, nHandler);

        return "1";
    }

    private SenderBuilder getSender(String user) {

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
                .withDescription("PLANO BASICO - VCESTUDA")
                .withAmount(new BigDecimal(10.99))
                .withQuantity(1);
    }

    private PreApprovalBuilder instantiatePreApproval() {

        Calendar now = Calendar.getInstance();
        Calendar ends = Calendar.getInstance();
        ends.add(Calendar.MONTH, 1);

        return new PreApprovalBuilder()
                .withCharge("manual")
                .withName("BASIC - 3 Meses")
                .withDetails("Nos proximos 3 meses, serão debitados do seu cartão o valor de R$10,99 referente ao plano basico ")
                .withAmountPerPayment(new BigDecimal(20))
                .withMaxTotalAmount(new BigDecimal(20))
                .withPeriod("monthly")
                .withDateRange(new DateRangeBuilder()
                        .between(
                                now.getTime(),
                                ends.getTime()
                        ));
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

    public DataList getPreApproval(Date sDate, Date eDate) {
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

    public String registerPreApproval() {
        try {
            RegisteredPreApproval registeredPreApproval
                    = pagSeguro
                            .preApprovals()
                            .register(
                                    new PreApprovalRegistrationBuilder()
                                            .withPreApproval(
                                                    instantiatePreApproval()
                                            )
                            );

            return registeredPreApproval.getPreApprovalCode();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

}
