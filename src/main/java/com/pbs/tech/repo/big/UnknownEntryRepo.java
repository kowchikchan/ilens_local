package com.pbs.tech.repo.big;


import com.pbs.tech.model.big.UnknownEntry;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface UnknownEntryRepo extends CrudRepository<UnknownEntry, String> {

    @Query("SELECT * FROM UnknownEntries WHERE time >= ?0 AND time < ?1 ALLOW FILTERING")
    List<UnknownEntry> getUnknownList(Date time1, Date time2);
}
