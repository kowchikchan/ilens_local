package com.pbs.tech.services;

import com.google.common.collect.Lists;
import com.pbs.tech.common.CONS;
import com.pbs.tech.common.ROLES_TYPE;
import com.pbs.tech.common.exception.AuthenticationException;
import com.pbs.tech.common.exception.IlensException;
import com.pbs.tech.common.security.ChipperUtil;
import com.pbs.tech.model.User;
import com.pbs.tech.repo.UserRepo;
import com.pbs.tech.vo.UserFilterVO;
import com.pbs.tech.vo.UserTokenVO;
import com.pbs.tech.vo.UserVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@Service
public class UserService {

    private final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Autowired
    UserRepo userRepo;

    @Autowired
    ChipperUtil chipperUtil;

    @Value("${ilens.user.train-data.path}")
    String trainDataPath;


    @Value("${ilens.python.path}")
    String pythonPath;


    @Autowired
    FileServices fileServices;

    Logger log = LoggerFactory.getLogger(UserService.class);

    public UserVo authUser(UserVo user) throws AuthenticationException {

        if (StringUtils.isEmpty(user.getUserId()) || StringUtils.isEmpty(user.getUserSecret())) {
            throw new AuthenticationException("Invalid user Credentials");
        }
        User userObj = userRepo.findByUsername(user.getUserId());
        if (userObj == null) {
            throw new AuthenticationException("User Does not exists ");
        }

        //Validate credentials
        String userPass = chipperUtil.getSHA(user.getUserSecret());
        if (userObj.getUsername().equals(user.getUserId()) && userObj.getPassword().equals(userPass)) {
            user.setUserSecret("");
            user.setId(userObj.getId());
            try {
                if (userObj.getRole() != null) {
                    user.setRole(userObj.getRole().toString());
                }
            }catch (NoSuchElementException e){
                throw new NoSuchElementException("No such Role Found {}"+ e.getMessage());
            }
            return user;
        }

        throw new AuthenticationException("Invalid user Credentials");
    }

    //Adduser
    public void addUser(UserVo user) throws AuthenticationException {
        if (StringUtils.isEmpty(user.getUserId()) || StringUtils.isEmpty(user.getUserSecret())) {
            throw new AuthenticationException("In-Sufficient user details");
        }

        User userObj = userRepo.findByUsername(user.getUserId());

        if (userObj != null) {
            throw new AuthenticationException("User details already exists ");
        }

        User newUser = new User();
        newUser.setUsername(user.getUserId());
        newUser.setPassword(chipperUtil.getSHA(user.getUserSecret()));
        newUser.setRole(ROLES_TYPE.valueOf(user.getRole()));
        newUser.setCreatedBy(user.getCreateBy());
        newUser.setCreatedDt(new Date());
        newUser.setUpdatedBy(user.getUpdatedBy());
        newUser.setUpdatedDt(new Date());
        newUser.setFirstName(user.getFirstName());
        newUser.setLastName(user.getLastName());
        newUser.setActive(user.isActive());
        newUser.setDepartment(user.getDepartment());
        newUser.setLocation(user.getLocation());
        userRepo.save(newUser);
    }

    //update user
    public void updateUser(UserVo user) throws IlensException {


        try {
            User userObj = userRepo.findById(user.getId()).get();
            userObj.setFirstName(user.getFirstName());
            userObj.setLastName(user.getLastName());
            // userObj.setPassword(chipperUtil.getSHA(user.getUserSecret()));
            userObj.setUpdatedBy(user.getUpdatedBy());
            userObj.setUpdatedDt(new Date());
            userObj.setDepartment(user.getDepartment());
            userObj.setRole(ROLES_TYPE.valueOf(user.getRole()));
            userObj.setLocation(user.getLocation());
            userRepo.save(userObj);
        } catch (NoSuchElementException e) {
            throw new IlensException("No object found");
        }


    }

    //delete user

    public void deleteUser(long userId) throws AuthenticationException {
        User userObj = userRepo.findById(userId).get();
        userRepo.delete(userObj);

    }

    public UserVo getUser(long userId) throws IlensException {
        try {
            User userObj = userRepo.findById(userId).get();
            return new UserVo(userObj.getId(), userObj.getUsername(), userObj.getRole(), userObj.getFirstName(),
                    userObj.getLastName(), userObj.getDateOfBirth(), userObj.isActive(), userObj.getCreatedDt(),
                    userObj.getCreatedBy(), userObj.getUpdatedDt(), userObj.getUpdatedBy(), userObj.getDepartment(),
                    userObj.getLocation());
        } catch (NoSuchElementException e) {
            throw new IlensException("No object found");
        }
        //return null;
    }


    //list user/
    public List<UserVo> getUsers(UserFilterVO filterVO, int pageNumber) {

        int itemsPerPage = 10;
        Sort sortting = sortAndOrderBy("updatedDt", Sort.Direction.DESC);
        ;

        if (filterVO != null && filterVO.getPageNumber() > 0) {
            pageNumber = filterVO.getPageNumber();
        }
        if (filterVO != null && filterVO.getItemsPerPage() > 0) {
            itemsPerPage = filterVO.getItemsPerPage();
        }
        if (filterVO != null && filterVO.getSortBy() != null) {
            if (filterVO.getOrderBy() != null && filterVO.getOrderBy().equals("DESC")) {
                sortting = sortAndOrderBy(filterVO.getSortBy(), Sort.Direction.DESC);
            } else {
                sortting = sortAndOrderBy(filterVO.getSortBy(), Sort.Direction.ASC);
            }
        }

        Pageable page = PageRequest.of(pageNumber, itemsPerPage, sortting);
        return Lists.newArrayList(userRepo.findUsersPerPage(page));
    }

    public List<UserVo> getUsersList() {

        return Lists.newArrayList(userRepo.findByAll());
    }

    public long getUsersCount(UserFilterVO filterVO) {

        return Lists.newArrayList(userRepo.findAll()).size();
    }

    private Sort sortAndOrderBy(String sortBy, Sort.Direction direction) {
        Sort sort = Sort.by(direction, sortBy);
        return sort;
    }

    public void changePassword(UserVo user) throws IlensException {
        try {
            User userObj = userRepo.findById(user.getId()).get();
            userObj.setPassword(chipperUtil.getSHA(user.getUserSecret()));
            userRepo.save(userObj);
        } catch (NoSuchElementException e) {
            throw new IlensException("No object found");
        }

    }


    public void addAdminUser(String password) {
        LOG.debug("User not exists ..adding admin user ");
        //add admin user with default password
        User user = new User();
        user.setUsername(CONS.adminUser);
        //Password
        Date createdDt = new Date();
        user.setPassword(chipperUtil.getSHA(password));
        user.setRole(ROLES_TYPE.Admin);
        user.setFirstName("admin");
        user.setLastName("admin");
        user.setActive(true);
        user.setCreatedBy("system");
        user.setCreatedDt(createdDt);
        user.setUpdatedBy("system");
        user.setUpdatedDt(createdDt);
        userRepo.save(user);

    }


    public void loadDataTraining(String id, MultipartFile file) {

        //save file fileServices
        fileServices.storeFile(file, id);
        //call training TRAIN_SCRIPT_NAME

    }

    public long startUserDataTraining(String id, HttpServletRequest request) {
        log.info("LOG {}", trainDataPath);
        String scriptPath = System.getProperty("SCRIPT_PATH");
        log.info("SCRIPTPATH:" + scriptPath);
        String trainSrcLocation = trainDataPath + File.separator + "." + id;
        HttpSession session = request.getSession();
        Object userId = session.getAttribute("USER_ID");
        String targetTrainLocation = scriptPath + File.separator + "faceDetection";
        String result = "";
        ProcessBuilder builder = new ProcessBuilder(pythonPath, scriptPath + File.separator + CONS.TRAIN_SCRIPT_NAME, "-i", id, "-b", targetTrainLocation, "-p", trainSrcLocation, "-u", userId.toString());
        try {
            Process p = builder.start();
            log.info("CMD,{}", String.join(" ", builder.command()));

            final BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            //p.waitFor();
            StringJoiner sj = new StringJoiner(System.getProperty("line.separator"));
            reader.lines().iterator().forEachRemaining(sj::add);
            result = sj.toString();
            log.info("OUTPUT : {}", result);

            p.destroy();
            return p.pid();
        } catch (IOException e) {
            log.error(e.getMessage());
            return 0;
        }
    }

    public void stopUserDataTraining(long pid) {
        Optional<ProcessHandle> optionalProcessHandle = ProcessHandle.of(pid);
        optionalProcessHandle.get().destroy();

    }

    public UserTokenVO getById(long id) throws Exception {
        try {
            return userRepo.findAllById(id);
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    public List<UserVo> getAllByRole(String role) throws Exception {
        try {
            return userRepo.findAllByRole(ROLES_TYPE.valueOf(role));
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

}
