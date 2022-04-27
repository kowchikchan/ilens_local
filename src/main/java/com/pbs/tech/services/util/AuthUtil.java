package com.pbs.tech.services.util;

import com.pbs.tech.common.CONS;
import com.pbs.tech.common.ROLES_TYPE;
import com.pbs.tech.common.security.ChipperUtil;
import com.pbs.tech.model.User;
import com.pbs.tech.repo.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

//@Component
//@Order(1)
public class AuthUtil //implements CommandLineRunner
{

    private static final Logger LOG= LoggerFactory.getLogger(AuthUtil.class);

    @Autowired
    ChipperUtil chipperUtil;

    @Autowired
    UserRepo userRepo;

   // @Override
    public void run(String... args) throws Exception {

        LOG.info("Check is existing user there");
        User userObj= userRepo.findByUsername(CONS.adminUser);

       LOG.info("SIZE {}",userObj);
       if(userObj == null ){
           LOG.debug( "User not exists ..Adding admin user ");
            //add admin user with default password
            User user=new User();
            user.setUsername(CONS.adminUser);
            //Password
           Date createdDt=new Date();
           user.setPassword(chipperUtil.getSHA(CONS.adminSecrect));
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


    }
}
