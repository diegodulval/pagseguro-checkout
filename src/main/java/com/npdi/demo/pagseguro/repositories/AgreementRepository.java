package com.npdi.demo.pagseguro.repositories;

import com.npdi.demo.pagseguro.domains.Agreement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author DD
 */
@Repository
public interface AgreementRepository extends JpaRepository< Agreement, Long> {

    @Transactional(readOnly = true)
    Agreement findByCodeReference(String codeReference);

}
