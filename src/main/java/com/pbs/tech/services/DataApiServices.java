package com.pbs.tech.services;

import com.pbs.tech.model.DataApi;
import com.pbs.tech.repo.DataApiRepo;
import com.pbs.tech.vo.DataApiVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.NoSuchElementException;

@Service
public class DataApiServices {
    @Autowired
    DataApiRepo dataApiRepo;

    Logger log= LoggerFactory.getLogger(DataApiServices.class);

    public void saveDataApiDetails(DataApiVO dataApiVO, HttpServletRequest request) {
        DataApi dataApi = null;
        try {
            dataApi = dataApiRepo.findById(dataApiVO.getId()).get();
        }catch (NoSuchElementException e){
            dataApi = new DataApi();
        }
        dataApi.setId(dataApiVO.getId());
        dataApi.setDataApi(dataApiVO.getDataApi());
        dataApi.setReportApi(dataApiVO.getReportApi());
        dataApi.setApiToken(dataApiVO.getApiToken());
        dataApiRepo.save(dataApi);

        // update session values.
        HttpSession session = request.getSession(true);
        session.removeAttribute("DATA_API");
        session.removeAttribute("REPORT_API");
        session.setAttribute("DATA_API", dataApi.getDataApi());
        session.setAttribute("REPORT_API", dataApi.getReportApi());
        log.info("DataApi Details Updated.");
    }

    public DataApiVO getDataApiDetails(){
        DataApiVO dataApiVO = new DataApiVO();
        try {
            DataApi dataApi = dataApiRepo.findById(Long.valueOf(0)).get();
            dataApiVO.setId(dataApi.getId());
            dataApiVO.setApiToken(dataApi.getApiToken());
            dataApiVO.setDataApi(dataApi.getDataApi());
            dataApiVO.setReportApi(dataApi.getReportApi());
        }catch (NoSuchElementException e){
            throw new NoSuchElementException("No Element Found" + e.getMessage());
        }
        return dataApiVO;
    }

}
