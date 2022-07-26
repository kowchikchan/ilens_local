package com.pbs.tech.repo;

import com.pbs.tech.model.AccessConfigs;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface AccessConfigRepo extends CrudRepository<AccessConfigs,Long> {

    List<AccessConfigs> findByChannelId(long channelId);

   AccessConfigs findByChannelIdAndPersonId(long channelId,String PersonId);

}
