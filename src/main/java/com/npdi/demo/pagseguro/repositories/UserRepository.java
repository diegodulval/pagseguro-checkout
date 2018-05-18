package com.npdi.demo.pagseguro.repositories;

import com.npdi.demo.pagseguro.domains.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author DD
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

}
