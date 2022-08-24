package com.pbs.tech.services;

import com.pbs.tech.model.WeekDays;
import com.pbs.tech.repo.WeekDaysRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.NoSuchElementException;


@Service
public class WeekDayServices {
    private final Logger LOG = LoggerFactory.getLogger(WeekDayServices.class);

    @Autowired
    WeekDaysRepo weekDaysRepo;


    public void save(WeekDays weekDays, HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession();
        Object userId = session.getAttribute("USER_ID");
        try {
            WeekDays weekDay = weekDaysRepo.findById(1L).get();
            weekDay.setId(1L);
            weekDay.setWeekDays(weekDays.getWeekDays());
            weekDay.setUpdatedBy(userId.toString());
            weekDay.setUpdatedDt(new Date());
            weekDaysRepo.save(weekDay);
        }catch (Exception e){
            throw new Exception("WeekDays configurations Not saved " + e.getMessage());
        }
    }

    public WeekDays getList(){
        WeekDays weekDays = null;
        try {
            weekDays = weekDaysRepo.findById(Long.valueOf(1)).get();
        }catch (NoSuchElementException e){
            throw new NoSuchElementException("No Such Element Found " + e.getMessage());
        }
        return weekDays;
    }

}
