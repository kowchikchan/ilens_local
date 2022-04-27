package com.pbs.tech.repo;


import com.pbs.tech.model.FRConfig;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FRConfigRepo extends CrudRepository<FRConfig,Long>{

    FRConfig findByChannelId(long channelId);
}

