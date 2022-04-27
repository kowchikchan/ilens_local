package com.pbs.tech.repo.big;

import com.pbs.tech.model.big.EntryExitEntity;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;

public interface EntryExitRepo extends CrudRepository<EntryExitEntity,String> {


    @Query("SELECT * FROM EntryExist WHERE id=?0 LIMIT ?1")
    Iterable<EntryExitEntity> findByUser(String user, Integer limit);

    @Query("SELECT * FROM EntryExit WHERE time >= ?0 group by id ALLOW FILTERING")
    Slice<EntryExitEntity> getTodayAttendance(Date today, Pageable page);

    @Query("SELECT * FROM EntryExit WHERE time >= ?0 group by id ALLOW FILTERING")
    Iterable<EntryExitEntity> getTodayAttendanceCount(Date today);

    @Query("SELECT * FROM EntryExit WHERE time >= ?0 AND time <= ?1 group by id ALLOW FILTERING")
    Iterable<EntryExitEntity> getLastHourEntryOrOnTimeEntry(Date pastOneHourOrToday, Date PresentHourOrOnTime);

    @Query("SELECT * FROM EntryExit WHERE time >= ?0 AND time < ?1 group by id ALLOW FILTERING")
    Iterable<EntryExitEntity> getEveryTenMinutes(Date pastOneHourOrToday, Date PresentHourOrOnTime);

    // @Query("SELECT * FROM EntryExit WHERE time >= ?0 AND time <= ?1 group by id ALLOW FILTERING")
    // Iterable<EntryExitEntity> getGraceTime(Date onTime,Date graceTime);


}
