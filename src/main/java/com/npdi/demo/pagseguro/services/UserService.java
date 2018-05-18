package com.npdi.demo.pagseguro.services;

import com.npdi.demo.pagseguro.domains.Plan;
import com.npdi.demo.pagseguro.domains.User;
import com.npdi.demo.pagseguro.repositories.UserRepository;
import com.npdi.demo.pagseguro.services.exceptions.ObjectNotFoundException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 *
 * @author DD
 */
@Service
public class UserService {

    @Autowired
    private UserRepository repo;

    public User create(User obj) {
        return repo.save(obj);
    }

    public User readById(Long id) {
        Optional<User> obj = repo.findById(id);
        if (!obj.isPresent()) {
            throw new ObjectNotFoundException(
                    "Usuário não encontrado! Id: " + id
                    + ", Tipo: " + User.class.getName());
        }
        return obj.get();
    }

    public Page<User> readByCriteria(String info, Integer page, Integer linesPerPage, String orderBy, String direction) {
        PageRequest pageRequest = new PageRequest(page, linesPerPage, Sort.Direction.valueOf(direction), orderBy);
        return repo.findAll(pageRequest);
    }

}
