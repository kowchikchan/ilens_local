package com.pbs.tech.repo;


import com.pbs.tech.model.FCMToken;
import org.springframework.data.repository.CrudRepository;

public interface FCMRepo extends CrudRepository<FCMToken, Long> {
}
