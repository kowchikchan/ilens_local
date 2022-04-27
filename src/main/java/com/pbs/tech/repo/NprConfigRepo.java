package com.pbs.tech.repo;

import com.pbs.tech.model.NprConfig;
import org.springframework.data.repository.CrudRepository;

public interface NprConfigRepo extends CrudRepository<NprConfig,Long>{

    NprConfig findByChannelId(long channelId);
}
