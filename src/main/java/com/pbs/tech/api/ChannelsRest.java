package com.pbs.tech.api;

import com.pbs.tech.services.ChannelsServices;
import com.pbs.tech.services.IlenService;
import com.pbs.tech.vo.ChannelFilterVO;
import com.pbs.tech.vo.ChannelVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/channels")
public class ChannelsRest {

    @Autowired
    ChannelsServices channelsServices;

    @Autowired
    IlenService ilenService;

    @GetMapping("/{filter}")
    public ResponseEntity<Object> getChannels(@RequestHeader("CLIENT_KEY") String clientKey, @PathVariable(required = false) String filter){
        return new ResponseEntity<Object>(channelsServices.getConnected(filter), HttpStatus.OK);
    }

    @PostMapping("/page/{pageNumber}")
    public ResponseEntity<Object> getChannelsList(@RequestHeader("CLIENT_KEY") String clientKey,
                                                  @RequestBody ChannelFilterVO channelFilterVO,
                                                  @PathVariable int pageNumber){
        return new ResponseEntity<>(channelsServices.getChannelsList(channelFilterVO, pageNumber), HttpStatus.OK);
    }

    @GetMapping("/pageCount/{filterKey}")
    public ResponseEntity<Object> getPageCount(@RequestHeader("CLIENT_KEY") String clientKey, @PathVariable String filterKey){
        return new ResponseEntity<Object>(channelsServices.getPageCount(filterKey), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestHeader("CLIENT_KEY") String clientKey, @RequestBody ChannelVo vo) throws Exception {
        channelsServices.saveChannel(vo);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@RequestHeader("CLIENT_KEY") String clientKey, @PathVariable long id){
        channelsServices.deleteChannel(id);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<?> getById(@RequestHeader("CLIENT_KEY") String clientKey, @PathVariable long id){
        return new ResponseEntity<>(channelsServices.getChannelById(id),HttpStatus.OK);
    }

    @PostMapping("/active/{id}")
    public ResponseEntity<?>channelActive(@RequestHeader("CLIENT_KEY") String clientKey, @PathVariable long id){
        channelsServices.setActive(id);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PostMapping("/inActive/{id}")
    public ResponseEntity<?>channelInActive(@RequestHeader("CLIENT_KEY") String clientKey, @PathVariable long id){
        channelsServices.setInActive(id);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

}
