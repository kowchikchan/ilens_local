package com.pbs.tech.repo.big;

import com.pbs.tech.model.big.PeopleCount;
import org.springframework.data.repository.CrudRepository;

public interface PeopleCountRepo extends CrudRepository<PeopleCount, String> {

}