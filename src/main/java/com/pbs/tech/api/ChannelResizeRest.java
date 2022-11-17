package com.pbs.tech.api;

import com.pbs.tech.model.ChannelResize;
import com.pbs.tech.services.ChannelResizeServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/channel/resize")
public class ChannelResizeRest {

    @Autowired
    ChannelResizeServices channelResizeServices;


    @PostMapping
    public ResponseEntity<?> save(@RequestHeader("CLIENT_KEY") String clientKey, @RequestBody ChannelResize channelResize) throws Exception {
        channelResizeServices.save(channelResize);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getResizeById(@RequestHeader("CLIENT_KEY") String clientKey, @PathVariable long id) throws Exception {
        return new ResponseEntity<>(channelResizeServices.getResizeList(id), HttpStatus.OK);
    }

}
