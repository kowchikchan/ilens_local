package com.pbs.tech.repo;

import com.pbs.tech.model.Channel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ChannelRepo extends PagingAndSortingRepository<Channel,Long> {

    List<Channel> findAllByOrderByCreatedDtDesc(Pageable page);

    List<Channel> findAllByOrderByCreatedDtDesc();

    List<Channel> findByIp(String ip);
    List<Channel> findByIp(String ip, Pageable pageable);

    //@Query("SELECT channel FROM Channel channel WHERE channel.status = ?1 order by channel.createdDt DESC")
    List<Channel> findAllByStatusAndIp( boolean status, String ip, Pageable page);

    //@Query("SELECT channel FROM Channel channel WHERE channel.status = :status AND channel.ip = :ip order by channel.createdDt DESC")
    List<Channel> findAllByStatusAndIp(boolean status, String ip);

    List<Channel> findAllByStatus(boolean status);

    List<Channel> findAllByStatus(boolean status, Pageable pageable);

}
