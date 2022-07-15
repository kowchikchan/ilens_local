package com.pbs.tech.services;

import com.pbs.tech.model.MenuStatus;
import com.pbs.tech.model.ReportPeriod;
import com.pbs.tech.repo.MenuStatusRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class MenuStatusServices {
    @Autowired
    MenuStatusRepo menuStatusRepo;

    Logger log = LoggerFactory.getLogger(MenuStatusServices.class);

    public void save(MenuStatus menuStatus) throws Exception {
        try {
            MenuStatus menuStatus1 = menuStatusRepo.findById(1L).get();
            menuStatus1.setId(menuStatus.getId());
            menuStatus1.setStatus(menuStatus.isStatus());
            menuStatusRepo.save(menuStatus1);
        }catch (Exception e){
            throw new Exception("Configurations Not saved " + e.getMessage());
        }
    }

    public MenuStatus getList(){
        MenuStatus menuStatus = null;
        try {
            menuStatus = menuStatusRepo.findById(Long.valueOf(1)).get();
        }catch (NoSuchElementException e){
            throw new NoSuchElementException("No Such Element Found " + e.getMessage());
        }
        return menuStatus;
    }

}
