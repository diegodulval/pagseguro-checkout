package com.npdi.demo.pagseguro.services;

import br.com.uol.pagseguro.api.application.authorization.search.AuthorizationDetail;
import br.com.uol.pagseguro.api.common.domain.TransactionStatus;
import br.com.uol.pagseguro.api.common.domain.TransactionType;
import br.com.uol.pagseguro.api.notification.PagSeguroNotificationHandler;
import br.com.uol.pagseguro.api.preapproval.search.PreApprovalDetail;
import br.com.uol.pagseguro.api.transaction.search.TransactionDetail;
import com.npdi.demo.pagseguro.domains.Agreement;
import com.npdi.demo.pagseguro.domains.Plan;
import com.npdi.demo.pagseguro.domains.Transaction;
import com.npdi.demo.pagseguro.repositories.AgreementRepository;
import com.npdi.demo.pagseguro.repositories.TransactionRepository;
import com.npdi.demo.pagseguro.services.exceptions.ObjectNotFoundException;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author DD
 */
@Service
public class PagSeguroNotificationHandlerImp implements PagSeguroNotificationHandler {

    @Autowired
    private AgreementRepository agreeRepo;

    @Autowired
    private TransactionRepository transRepo;

    @Override
    public void handleTransactionNotification(TransactionDetail td) {
        Transaction obj = transRepo.findByCode(td.getCode());
        if (obj == null) {
            obj = new Transaction();
            obj.setCode(td.getCode());
            obj.setPaymentURL(td.getPaymentLink());
            obj.setCreatedAt(td.getDate());
            obj.setPrice(td.getDiscountAmount()); //conferir valor
            obj.setAgreement(agreeRepo.findByCodeReference(td.getReference()));
        }
        obj.setUpdatedAt(td.getDate());
        obj.setStatus(td.getStatus().getStatus().name());

        transRepo.save(obj);
    }

    @Override
    public void handleAuthorizationNotification(AuthorizationDetail ad) {
        System.out.println(ad);
    }

    @Override
    public void handlePreApprovalNotification(PreApprovalDetail pad) {

        Agreement agree = agreeRepo.findByCodeReference(pad.getReference());
        agree.setStatus(pad.getStatus().getStatus().name());
        agree.setUpdatedAt(pad.getLastEvent());

        switch (pad.getStatus().getStatus().name()) {
            case "ACTIVE":
                agree.setStartDate(new Date());
                break;
            case "CANCELLED":
            case "CANCELLED_BY_RECEIVER":
            case "CANCELLED_BY_SENDER":
                agree.setFinalDate(new Date());
            default:
                break;
        }

        agreeRepo.save(agree);
    }

}
