/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.npdi.demo.pagseguro.resources;

import br.com.uol.pagseguro.api.application.authorization.search.AuthorizationDetail;
import br.com.uol.pagseguro.api.notification.PagSeguroNotificationHandler;
import br.com.uol.pagseguro.api.preapproval.search.PreApprovalDetail;
import br.com.uol.pagseguro.api.transaction.search.TransactionDetail;

/**
 *
 * @author NPDI
 */
public class PagSeguroNotificationHandlerImp implements PagSeguroNotificationHandler {

    @Override
    public void handleTransactionNotification(TransactionDetail td) {
        System.out.println(td);
    }

    @Override
    public void handleAuthorizationNotification(AuthorizationDetail ad) {
        System.out.println(ad);
    }

    @Override
    public void handlePreApprovalNotification(PreApprovalDetail pad) {
        System.out.println(pad);
    }

}
