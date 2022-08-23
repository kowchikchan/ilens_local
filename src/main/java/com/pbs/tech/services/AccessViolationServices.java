package com.pbs.tech.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pbs.tech.common.SearchCriteria;
import com.pbs.tech.model.big.EntryViolation;
import com.pbs.tech.repo.big.EntryViolationRepo;
import com.pbs.tech.vo.UnknownFilterVO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraOperations;
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

    @Autowired
    private CassandraOperations cassandraTemplate;

    public List<EntryViolation> getViolationList(UnknownFilterVO unknownFilterVO, int pageNumber) throws Exception {
        List<EntryViolation> entity = new ArrayList<>();
        String swapType = "";
        String swapName = "";
        if (!StringUtils.isBlank(unknownFilterVO.getId())) {
            List<SearchCriteria> specificationValues = new ArrayList<>();
            if (unknownFilterVO.getDate() != null && !StringUtils.isEmpty(unknownFilterVO.getDate().toString())) {
                SimpleDateFormat df = new SimpleDateFormat(dateFormatForDb);
                String selectedDate = df.format(ilenService.getDayStTime(unknownFilterVO.getDate()));
                String endDate = df.format(ilenService.getDayEndTime(unknownFilterVO.getDate()));

                //start time
                specificationValues.add(new SearchCriteria("time", ">=", selectedDate));

                //end time.
                specificationValues.add(new SearchCriteria("time", "<=", endDate));
            }
            if (!StringUtils.isBlank(unknownFilterVO.getId())) {
                //id
                specificationValues.add(new SearchCriteria("id", "=", unknownFilterVO.getId()));
            }
            //generate query.
            StringBuilder stringBuilder = new StringBuilder();
            for (int k = 0; k < specificationValues.size(); k++) {
                if (k == 0) {
                    stringBuilder.append("SELECT * FROM ilens.entryviolation WHERE ");
                }
                stringBuilder.append(specificationValues.get(k).getKey() + " " + specificationValues.get(k).getOperation() + " " + "'" + specificationValues.get(k).getValue() + "'");
                if (!(k == specificationValues.size() - 1)) {
                    stringBuilder.append(" " + "AND" + " ");
                }
                if (k == specificationValues.size() - 1) {
                    stringBuilder.append(" " + " ALLOW FILTERING");
                }
            }
            try {
                log.info("Generated Query : " + stringBuilder);
                List<EntryViolation> filterData = cassandraTemplate.select(stringBuilder.toString(), EntryViolation.class);
                for (int i = 0; i < filterData.size(); i++) {
                    if (i == 0) {
                        EntryViolation violation = mapper.convertValue(filterData.get(i), EntryViolation.class);
                        entity.add(violation);
                        swapType = filterData.get(i).getType();
                        swapName = filterData.get(i).getName();
                    } else if (!Objects.equals(swapType, filterData.get(i).getType()) ||
                            !Objects.equals(swapName, filterData.get(i).getName())) {
                        EntryViolation violation = mapper.convertValue(filterData.get(i), EntryViolation.class);
                        entity.add(violation);
                        swapType = filterData.get(i).getType();
                        swapName = filterData.get(i).getName();
                    }
                }
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }

        } else {
            int itemsPerPage = 10;
            int perPage = Integer.parseInt(String.valueOf(pageNumber) + "0");
            List<EntryViolation> violations = null;
            violations = this.violationList(unknownFilterVO);
            int sizeOfList = violations.size();
            try {
                if (perPage > sizeOfList) {
                    entity = violations.subList((perPage - itemsPerPage), sizeOfList);
                } else {
                    entity = violations.subList((perPage - itemsPerPage), perPage);
                }
            }catch (Exception e){
                e.printStackTrace();
                return entity;
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
