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
import java.util.*;

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
        String swapType = "";
        String swapName = "";
        if (unknownFilterVO != null) {
            Date selectedDate = ilenService.getDayStTime(unknownFilterVO.getDate());
            Date endDate = ilenService.getDayEndTime(unknownFilterVO.getDate());
            Slice<EntryViolation> violations = entryViolationRepo.getViolationListByPageable(selectedDate, endDate,
                    CassandraPageRequest.first(10));
            while(violations.hasNext() && currPage < pageNumber) {
                violations = entryViolationRepo.getViolationListByPageable(selectedDate, endDate, violations.nextPageable());
                currPage++;
            }
            for (int i=0; i<violations.getContent().size();i++) {
                if(i==0){
                    EntryViolation violation = mapper.convertValue(violations.getContent().get(i), EntryViolation.class);
                    entity.add(violation);
                    swapType = violations.getContent().get(i).getType();
                    swapName = violations.getContent().get(i).getName();
                }else if(!Objects.equals(swapType, violations.getContent().get(i).getType()) ||
                        !Objects.equals(swapName, violations.getContent().get(i).getName())){
                    EntryViolation violation = mapper.convertValue(violations.getContent().get(i), EntryViolation.class);
                    entity.add(violation);
                    swapType = violations.getContent().get(i).getType();
                    swapName = violations.getContent().get(i).getName();
                }
            }
        }
        return entity;
    }

    public List<EntryViolation> violationList(UnknownFilterVO unknownFilterVO) {
        List<EntryViolation> entity = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        if (unknownFilterVO != null) {
            cal.setTime(unknownFilterVO.getDate());
            Date selectedDate = ilenService.getDayStTime(unknownFilterVO.getDate());
            Date endDate = ilenService.getDayEndTime(unknownFilterVO.getDate());
            List<EntryViolation> entryViolationList = entryViolationRepo.getViolationList(selectedDate, endDate);
            String swapType = "";
            String swapName = "";
            for(int i=0; i<entryViolationList.size();i++){
                if(i==0){
                    EntryViolation violation = mapper.convertValue(entryViolationList.get(i), EntryViolation.class);
                    entity.add(violation);
                    swapType = entryViolationList.get(i).getType();
                    swapName = entryViolationList.get(i).getName();
                }else if(!Objects.equals(swapName, entryViolationList.get(i).getName()) ||
                        !Objects.equals(swapType, entryViolationList.get(i).getType())){
                    EntryViolation violation = mapper.convertValue(entryViolationList.get(i), EntryViolation.class);
                    entity.add(violation);
                    swapName = entryViolationList.get(i).getName();
                    swapType = entryViolationList.get(i).getType();
                }
            }
        }
        return entity;
    }

    public long violationCount(String date) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat(dateFormatForDb);
        UnknownFilterVO unknownFilterVO = new UnknownFilterVO();
        unknownFilterVO.setDate(df.parse(date));
        List<EntryViolation> entryViolation = this.violationList(unknownFilterVO);
        return entryViolation.size();
    }
}
