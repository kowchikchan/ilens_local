package com.pbs.tech.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pbs.tech.model.big.EntryViolation;
import com.pbs.tech.repo.big.EntryViolationRepo;
import com.pbs.tech.vo.UnknownFilterVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class AccessViolationServices {
    Logger log = LoggerFactory.getLogger(AccessViolationServices.class);

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    EntryViolationRepo entryViolationRepo;

    public List<EntryViolation> getViolationList(UnknownFilterVO unknownFilterVO){
        List<EntryViolation> entity = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        if (unknownFilterVO != null) {
            cal.setTime(unknownFilterVO.getDate());

            //selected date
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 1);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            Date selectedDate = cal.getTime();

            //selected date end time.
            cal.add(Calendar.HOUR_OF_DAY, 23);
            cal.add(Calendar.MINUTE, 58);
            cal.add(Calendar.SECOND, 59);
            cal.add(Calendar.MILLISECOND, 0);
            Date endDate = cal.getTime();

            List<EntryViolation> entryViolationList = entryViolationRepo.getViolationList(selectedDate, endDate);
            for (EntryViolation entryViolation : entryViolationList) {
                EntryViolation violation = mapper.convertValue(entryViolation, EntryViolation.class);
                entity.add(violation);
            }
        }
        return entity;
    }
}
