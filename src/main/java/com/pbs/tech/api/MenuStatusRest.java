package com.pbs.tech.api;

import com.pbs.tech.model.MenuStatus;
import com.pbs.tech.services.MenuStatusServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/menuStatus")
public class MenuStatusRest {
    @Autowired
    MenuStatusServices menuStatusServices;

    @PostMapping("/save")
    public ResponseEntity<Object> saveMenuStatus(@RequestHeader("CLIENT_KEY") String clientKey,
                                                   @RequestBody MenuStatus menuStatus) throws Exception {
        menuStatusServices.save(menuStatus);
        return new ResponseEntity<Object>(HttpStatus.ACCEPTED);
    }

    @GetMapping("/list")
    public ResponseEntity<Object> getList(@RequestHeader("CLIENT_KEY") String clientKey){
        return new ResponseEntity<>(menuStatusServices.getList(), HttpStatus.OK);
    }
}
