package com.npdi.demo.pagseguro.services;

import br.com.uol.pagseguro.api.PagSeguro;
import br.com.uol.pagseguro.api.checkout.CheckoutRegistration;
import br.com.uol.pagseguro.api.checkout.CheckoutRegistrationBuilder;
import br.com.uol.pagseguro.api.checkout.RegisteredCheckout;
import br.com.uol.pagseguro.api.common.domain.DataList;
import br.com.uol.pagseguro.api.common.domain.PreApproval;
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
import com.npdi.demo.pagseguro.domains.Agreement;
import com.npdi.demo.pagseguro.domains.Plan;
import com.npdi.demo.pagseguro.domains.User;
import com.npdi.demo.pagseguro.repositories.AgreementRepository;
import java.math.BigDecimal;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
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

    @Autowired
    private AgreementRepository agreRepo;

    //Instancia uma instancia do Plano escolhido
    private PreApprovalRegistrationBuilder instantiatePreApproval(Plan plan) {

        PreApproval preApproval = new PreApprovalBuilder()
                .withCharge("AUTO")
                .withPeriod(plan.getPeriod())
                .withAmountPerPayment(plan.getPrice())
                .withName(plan.getName())
                .build();

        return new PreApprovalRegistrationBuilder()
                .withCurrency(Currency.BRL)
                .withPreApproval(preApproval)
                .withRedirectURL("http://www.vcestuda.com.br")
                .withNotificationURL("http:localhost/notification");
    }

    //Cria o checkout no pagseguro
    public String createCheckout(String email, String plan) {
        try {
            RegisteredCheckout registeredCheckout
                    = pagSeguro
                            .checkouts()
                            .register(registerCheckout(email, plan));

            return registeredCheckout.getRedirectURL();
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    //Monta o objeto de checkout 
    private Builder<CheckoutRegistration> registerCheckout(String email, String plan) {
        CheckoutRegistrationBuilder check = new CheckoutRegistrationBuilder()
                .withCurrency(Currency.BRL)
                .withReference("TRAS-0001")
                .withSender(getSender(email))
                .addItem(getItems());
        //.addPaymentMethodConfig(getPaymentMethodConfig());
        //.withAcceptedPaymentMethods(getPaymentMethod())
        return check;
    }

    //Registra as notificações
    public String registerNotify(HttpServletRequest request) {

        PagSeguroNotificationHandler nHandler = new PagSeguroNotificationHandlerImp();

        NotificationsResource nResource = new NotificationsResource(pagSeguro, new JSEHttpClient());

        nResource.handle(request, nHandler);

        return "1";
    }

    //Instancia o comprador
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

    //Instancia um item
    private PaymentItemBuilder getItems() {
        return new PaymentItemBuilder()
                .withId("0001")
                .withDescription("PLANO BASICO - VCESTUDA")
                .withAmount(new BigDecimal(10.99))
                .withQuantity(1);
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

    //Cria plano especializado para cada cliente
    public Agreement signaturePlan(Plan plan, User user) {
        try {

            String code = "PP-" + user.getId().toString() + "-" + plan.getId().toString();

            RegisteredPreApproval registeredPreApproval
                    = pagSeguro
                            .preApprovals()
                            .register(
                                    instantiatePreApproval(plan)
                                            .withReference(code));

            Agreement agre = new Agreement();
            agre.setCodeReference(code);
            agre.setCodePreApproval(registeredPreApproval.getPreApprovalCode());
            agre.setPlan(plan);
            agre.setStatus("PENDING");
            agre.setCreatedAt(new Date());
            agre.setUpdatedAt(new Date());
            agre.setRedirectURL(registeredPreApproval.getRedirectURL());

            return agreRepo.save(agre);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public String searchPreApproval() {
        try {
            // Busca de assinaturas
            DataList dataList = pagSeguro.preApprovals().search().byDateRange(
                    new DateRangeBuilder().between(
                            DatatypeConverter.parseDateTime("2018-01-01T00:00:00.000-03:00").getTime(),
                            DatatypeConverter.parseDateTime("2018-05-17T12:56:00.000-03:00").getTime()),
                    1,
                    10
            );

            System.out.println(dataList);
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "Erro";
        }
    }
}
