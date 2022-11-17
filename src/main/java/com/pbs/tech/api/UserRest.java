package com.pbs.tech.api;

import com.pbs.tech.common.exception.AuthenticationException;
import com.pbs.tech.common.exception.IlensException;
import com.pbs.tech.services.UserService;
import com.pbs.tech.vo.UserFilterVO;
import com.pbs.tech.vo.UserVo;
import org.apache.juli.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/user")
public class UserRest {

    @Autowired
    UserService userService;

    Logger log= LoggerFactory.getLogger(UserRest.class);

    @PostMapping("/auth")
    public ResponseEntity<?> authUser(@RequestHeader("CLIENT_KEY") String clientKey, @RequestBody UserVo userVO){
        try {
            userService.authUser(userVO);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserbById(@RequestHeader("CLIENT_KEY") String clientKey, @PathVariable int id){
        try {
            return new ResponseEntity<>(userService.getUser(id),HttpStatus.OK);
        } catch (IlensException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }


    @PostMapping
    public ResponseEntity<?> addUser(@RequestHeader("CLIENT_KEY") String clientKey, @RequestBody UserVo user){
        try {
            userService.addUser(user);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<?> updateUser(@RequestHeader("CLIENT_KEY") String clientKey, @RequestBody UserVo user){
        try {
            userService.updateUser(user);
        } catch (IlensException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PutMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestHeader("CLIENT_KEY") String clientKey, @RequestBody UserVo user){
        try {
            userService.changePassword(user);
        } catch (IlensException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@RequestHeader("CLIENT_KEY") String clientKey, @PathVariable long userId){
        try {
            userService.deleteUser(userId);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping("/list/{pageNumber}")
    public ResponseEntity<?> getAllUser(@RequestHeader("CLIENT_KEY") String clientKey, @RequestBody(required = false) UserFilterVO userFilterVO,@PathVariable int pageNumber){
        return new ResponseEntity<>(userService.getUsers(userFilterVO,pageNumber),HttpStatus.OK);
    }
    @GetMapping("/usersList")
    public ResponseEntity<?> getAllUsersList(@RequestHeader("CLIENT_KEY") String clientKey){
        return new ResponseEntity<>(userService.getUsersList(),HttpStatus.OK);
    }
    @GetMapping("/count")
    public ResponseEntity<?> getAllUserCount(@RequestHeader("CLIENT_KEY") String clientKey, @RequestBody(  required = false) UserFilterVO userFilterVO){
        return new ResponseEntity<>(userService.getUsersCount(userFilterVO),HttpStatus.OK);
    }


    @PutMapping("/loadTraining/{id}")
    public ResponseEntity<?> loadDataTraining(@RequestHeader("CLIENT_KEY") String clientKey, @PathVariable String id, @RequestParam("file")MultipartFile file){
        userService.loadDataTraining(id,file);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);

    }

    @PutMapping("/starTraining/{id}")
    public ResponseEntity<?> startDataTraining(@RequestHeader("CLIENT_KEY") String clientKey, @PathVariable String id,
                                               HttpServletRequest request){
      return new ResponseEntity<>(userService.startUserDataTraining(id, request),HttpStatus.OK);
    }

    //Adding comments
    @PutMapping("/stopTraining/{id}")
    public ResponseEntity<?> startDataTraining(@RequestHeader("CLIENT_KEY") String clientKey, @PathVariable int id){
        userService.stopUserDataTraining(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/trained")
    public ResponseEntity<?> getLabels(@RequestHeader("CLIENT_KEY") String clientKey) throws IOException {
        return new ResponseEntity<>(userService.getTrainedLabels(), HttpStatus.OK);
    }
}
