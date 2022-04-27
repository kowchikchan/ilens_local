package com.pbs.tech.repo.big;

import com.pbs.tech.model.big.EntryExitEntity;
import com.pbs.tech.model.big.EntryViolation;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface EntryViolationRepo extends CrudRepository<EntryViolation,String> {



    @Query("SELECT * FROM EntryViolation WHERE time >= ?0 group by id ALLOW FILTERING")
    Slice<EntryViolation> getEntryViolations(Date today, Pageable page);

    @Query("SELECT * FROM EntryViolation WHERE time >= ?0 and location = ?1 group by id ALLOW FILTERING")
    List<EntryViolation> getEntryViolationsByLoc(Date today, String location);

}
