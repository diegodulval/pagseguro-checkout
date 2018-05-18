package com.npdi.demo.pagseguro.repositories;

import com.npdi.demo.pagseguro.domains.Agreement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author DD
 */
@Repository
public interface AgreementRepository extends JpaRepository< Agreement, Long> {

    Agreement findByCodeReference(String codeReference);

}
