package com.pbs.tech.repo.big;

import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.CrudRepository;

import com.pbs.tech.model.big.ExitView;
import java.util.Date;

public interface ExitViewRepo extends CrudRepository<ExitView,String> {


    @Query("SELECT * FROM ExitView WHERE time >= ?0 AND id = ?1 AND type = 'exit'  ALLOW FILTERING")
    Slice<ExitView> getTodayExit(Date today,String id,Pageable page);

    @Query("SELECT * FROM ExitView WHERE time >= ?0 group by id ALLOW FILTERING")
    Slice<ExitView> getExitView(Date today,Pageable page);



}
