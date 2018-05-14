package com.npdi.demo.pagseguro;

import br.com.uol.pagseguro.api.PagSeguro;
import br.com.uol.pagseguro.api.PagSeguroEnv;
import br.com.uol.pagseguro.api.application.authorization.AuthorizationRegistration;
import br.com.uol.pagseguro.api.application.authorization.AuthorizationRegistrationBuilder;
import br.com.uol.pagseguro.api.application.authorization.RegisteredAuthorization;
import br.com.uol.pagseguro.api.common.domain.PermissionCode;
import br.com.uol.pagseguro.api.credential.Credential;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PagseguroApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(PagseguroApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        String appId = "dulval";
        String appKey = "378D72184949AD32244BCFA8DE4ED0B1";

        try {

            final PagSeguro pagSeguro = PagSeguro.instance(Credential.applicationCredential(appId,
                    appKey), PagSeguroEnv.SANDBOX);

            // Registra as autorizações
            AuthorizationRegistration authorizationRegistration
                    = new AuthorizationRegistrationBuilder()
                            .withNotificationUrl("http://www.dulval.com/pagseguro/api/notification")
                            .withReference("NPDI-01")
                            .withRedirectURL("www.dulval.com/pagseguro")
                            .addPermission(PermissionCode.Code.CREATE_CHECKOUTS)
                            .addPermission(PermissionCode.Code.RECEIVE_TRANSACTION_NOTIFICATIONS)
                            .addPermission(PermissionCode.Code.SEARCH_TRANSACTIONS)
                            .build();

            RegisteredAuthorization ra = pagSeguro.authorizations().register(authorizationRegistration);
            System.out.print(ra);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
