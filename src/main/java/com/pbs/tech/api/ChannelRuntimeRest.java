package com.pbs.tech.api;

import com.pbs.tech.common.exception.IlensException;
import com.pbs.tech.services.IlenService;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/channels/runtime")
public class ChannelRuntimeRest {

    @Autowired
    IlenService ilenService;

    @GetMapping
    public ResponseEntity<?> getRuntimes(@RequestHeader("CLIENT_KEY") String clientKey){
       return new ResponseEntity(ilenService.getRuntimes(), HttpStatus.OK);
    }

    @PostMapping("/start/{id}")
    public ResponseEntity<?> starChanel(@RequestHeader("CLIENT_KEY") String clientKey, @PathVariable String id){
        try {
            ilenService.startRuntime(id);
        } catch (IlensException | JSONException | IOException | InterruptedException e) {
            return new ResponseEntity(e.getLocalizedMessage(),HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PostMapping("/stop/{id}")
    public ResponseEntity<?> stopChanel(@RequestHeader("CLIENT_KEY") String clientKey,
                                        @PathVariable String id) throws IOException {
        ilenService.stopRunTime(id);
        return new ResponseEntity(HttpStatus.OK);
    }

}
