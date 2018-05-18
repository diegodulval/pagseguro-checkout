package com.npdi.demo.pagseguro.services;

import com.npdi.demo.pagseguro.domains.Plan;
import com.npdi.demo.pagseguro.repositories.PlanRepository;
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
public class PlanService {

    @Autowired
    private PlanRepository repo;

    public Plan create(Plan obj) {
        return repo.save(obj);
    }

    public Plan readById(Long id) {
        Optional<Plan> obj = repo.findById(id);
        if (!obj.isPresent()) {
            throw new ObjectNotFoundException(
                    "Plano não encontrado! Id: " + id
                    + ", Tipo: " + Plan.class.getName());
        }
        return obj.get();
    }

    public Page<Plan> readByCriteria(String info, Integer page, Integer linesPerPage, String orderBy, String direction) {
        PageRequest pageRequest = new PageRequest(page, linesPerPage, Sort.Direction.valueOf(direction), orderBy);
        return repo.findAll(pageRequest);
    }

}
