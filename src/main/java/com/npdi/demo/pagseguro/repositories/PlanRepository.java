package com.npdi.demo.pagseguro.repositories;

import com.npdi.demo.pagseguro.domains.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author DD
 */
@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {

}
