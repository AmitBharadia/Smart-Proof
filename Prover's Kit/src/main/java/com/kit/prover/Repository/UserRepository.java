package com.kit.prover.Repository;

import com.kit.prover.Entity.Prover;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<Prover, Integer> {

}
