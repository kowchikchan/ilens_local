package com.pbs.tech.api;

import com.pbs.tech.vo.IlensApiClientVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api/v1/iLensApiClient")
public class IlensApiClientRest {

    @PostMapping
    public ResponseEntity<?> apiClient(@RequestHeader("CLIENT_KEY") String clientKey,
                                          @RequestBody IlensApiClientVO ilensApiClientVO)
            throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

        TrustStrategy acceptingTrustStrategy = (x509Certificates, s) -> true;
        SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null,
                acceptingTrustStrategy).build();
        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
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
