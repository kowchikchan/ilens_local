package com.pbs.tech.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pbs.tech.model.big.EntryViolation;
import com.pbs.tech.repo.big.EntryViolationRepo;
import com.pbs.tech.vo.UnknownFilterVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.query.CassandraPageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AccessViolationServices {
    Logger log = LoggerFactory.getLogger(AccessViolationServices.class);

    private static final String dateFormatForDb = "yyyy-MM-dd HH:mm:ss";
    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    EntryViolationRepo entryViolationRepo;

    @Autowired
    IlenService ilenService;

    public List<EntryViolation> getViolationList(UnknownFilterVO unknownFilterVO, int pageNumber) throws Exception {
        List<EntryViolation> entity = new ArrayList<>();
        int currPage = 0;
        if (unknownFilterVO != null) {
            Date selectedDate = ilenService.getDayStTime(unknownFilterVO.getDate());
            Date endDate = ilenService.getDayEndTime(unknownFilterVO.getDate());
            Slice<EntryViolation> violations = entryViolationRepo.getViolationList(selectedDate, endDate,
                    CassandraPageRequest.first(10));
            while(violations.hasNext() && currPage < pageNumber) {
                violations = entryViolationRepo.getViolationList(selectedDate, endDate, violations.nextPageable());
                currPage++;
            }
            for (int i=0; i<violations.getContent().size();i++) {
                EntryViolation violation = mapper.convertValue(violations.getContent().get(i), EntryViolation.class);
                entity.add(violation);
            }
        }
        return entity;
    }

    public long violationCount(String date) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat(dateFormatForDb);
        Date date1 = df.parse(date);
        return entryViolationRepo.getViolationCount(ilenService.getDayStTime(date1), ilenService.getDayEndTime(date1)).
                size();
    }
}
