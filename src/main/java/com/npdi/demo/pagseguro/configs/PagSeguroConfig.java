/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.npdi.demo.pagseguro.configs;

import br.com.uol.pagseguro.api.PagSeguro;
import br.com.uol.pagseguro.api.PagSeguroEnv;
import br.com.uol.pagseguro.api.application.authorization.AuthorizationRegistration;
import br.com.uol.pagseguro.api.application.authorization.AuthorizationRegistrationBuilder;
import br.com.uol.pagseguro.api.application.authorization.RegisteredAuthorization;
import br.com.uol.pagseguro.api.common.domain.PermissionCode;
import br.com.uol.pagseguro.api.credential.Credential;
import br.com.uol.pagseguro.api.http.JSEHttpClient;
import br.com.uol.pagseguro.api.utils.logging.SimpleLoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author DD
 */
@Configuration
public class PagSeguroConfig {

    @Value("${ps.seller_email}")
    private String sellerEmail;

    @Value("${ps.seller_token}")
    private String sellerToken;

    @Value("${ps.app_id}")
    private String appId;

    @Value("${ps.app_key}")
    private String appKey;

    @Bean
    public PagSeguro pagSeguroClient() {
        return PagSeguro
                .instance(new SimpleLoggerFactory(), new JSEHttpClient(),
                        getCredentials(), PagSeguroEnv.SANDBOX);
    }

    private Credential getCredentials() {
        return Credential.sellerCredential(sellerEmail, sellerToken);
    }

    private void authorizationApp() {
        try {
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

            RegisteredAuthorization ra = pagSeguroClient().authorizations().register(authorizationRegistration);
            System.out.print(ra);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
