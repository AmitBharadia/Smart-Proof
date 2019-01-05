package com.kit.prover.Controller;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<Zkp_user, Integer> {

    //List<Prover> findByUniqueId(String uid);
}
