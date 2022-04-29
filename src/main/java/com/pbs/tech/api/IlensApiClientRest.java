package com.pbs.tech.api;

import com.pbs.tech.vo.IlensApiClientVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/v1/iLensApiClient")
public class IlensApiClientRest {

    @PostMapping
    public ResponseEntity<?> apiClient(@RequestHeader("CLIENT_KEY") String clientKey,
                                          @RequestBody IlensApiClientVO ilensApiClientVO){
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        HttpHeaders headers = new HttpHeaders();
        headers.set("CLIENT_KEY", ilensApiClientVO.getClientKey());

        HttpMethod method = null;
        HttpEntity entity = null;

        if(StringUtils.equalsIgnoreCase(ilensApiClientVO.getMethod(), "GET")){
            method = HttpMethod.GET;
            entity = new HttpEntity(headers);
        }else if(StringUtils.equalsIgnoreCase(ilensApiClientVO.getMethod(), "POST")){
            method = HttpMethod.POST;
            entity = new HttpEntity(ilensApiClientVO.getInputVo(), headers);
        }else if(StringUtils.equalsIgnoreCase(ilensApiClientVO.getMethod(), "PUT")){
            method = HttpMethod.PUT;
            entity = new HttpEntity(ilensApiClientVO.getInputVo(), headers);
        }else if(StringUtils.equalsIgnoreCase(ilensApiClientVO.getMethod(), "DELETE")){
            method = HttpMethod.DELETE;
            entity = new HttpEntity(headers);
        }
        HttpEntity<Object> response = restTemplate.exchange(ilensApiClientVO.getApi(), method, entity, Object.class);
        return new ResponseEntity<>(response.getBody(),HttpStatus.OK);
    }

}
