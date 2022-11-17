package com.pbs.tech.repo;

import com.pbs.tech.model.ChannelResize;
import org.springframework.data.repository.CrudRepository;

public interface ChannelResizeRepo extends CrudRepository<ChannelResize,Long> {

    ChannelResize findByChannelId(long id);
}
