package com.translate.manga.front.service;

import com.translate.manga.front.RestClient.GenericResponseRequest;
import com.translate.manga.front.util.ConfigurationRestClient;
import com.translate.manga.persistance.PageJson;
import com.translate.manga.persistance.Return;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class DrawPageService {

    @Value(value = "${address.back}")
    private String url;

    @Autowired
    private ConfigurationRestClient configurationRestClient;

    public PageJson getPageJsonTranslateAndDraw(PageJson pageJson){
        ResponseEntity<Return<PageJson>> legalStatus=new GenericResponseRequest<PageJson>(url,configurationRestClient)
                .genericCall("/api/translate/page",
                        new ParameterizedTypeReference<Return<PageJson>>(){},
                        HttpMethod.POST,
                        pageJson);
        if (legalStatus!=null&&legalStatus.getBody()!=null){
            return legalStatus.getBody().getResults();
        }
        return null;
    }
}
