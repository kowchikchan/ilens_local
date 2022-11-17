package com.pbs.tech.services;

import com.pbs.tech.model.ChannelResize;
import com.pbs.tech.repo.ChannelResizeRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChannelResizeServices {

    Logger log= LoggerFactory.getLogger(ChannelResizeServices.class);

    @Autowired
    ChannelResizeRepo channelResizeRepo;

    public void save(ChannelResize channelResizeVO) throws Exception {
        try {
            //Channel c = channelRepo.findById(channelResizeVO.getChannelId()).get();
            ChannelResize channelResize = channelResizeRepo.findByChannelId(channelResizeVO.getChannelId());
            channelResize.setChannelId(channelResizeVO.getChannelId());
            channelResize.setcLeft(channelResizeVO.getcLeft());
            channelResize.setcTop(channelResizeVO.getcTop());
            channelResize.setcWidth(channelResizeVO.getcWidth());
            channelResize.setcHeight(channelResizeVO.getcHeight());
            channelResize.settWidth(channelResizeVO.gettWidth());
            channelResize.settHeight(channelResizeVO.gettHeight());
            channelResizeRepo.save(channelResize);
        }catch (Exception e){
            throw new Exception("No channel found "+ e.getMessage());
        }
    }

    public ChannelResize getResizeList(long id) throws Exception {
        try {
            ChannelResize channelResize = channelResizeRepo.findByChannelId(id);
            return channelResize;
        }catch (Exception e){
            throw new Exception("No channel found "+ e.getMessage());
        }
    }

}
