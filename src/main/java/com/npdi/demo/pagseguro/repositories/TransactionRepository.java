package com.npdi.demo.pagseguro.repositories;

import com.npdi.demo.pagseguro.domains.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author DD
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Transactional(readOnly = true)
    Transaction findByCode(String code);
}
