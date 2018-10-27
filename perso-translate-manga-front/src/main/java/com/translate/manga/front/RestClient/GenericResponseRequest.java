package com.translate.manga.front.RestClient;

import com.translate.manga.front.Enum.EnumVaadinSession;
import com.translate.manga.front.util.ConfigurationRestClient;
import com.translate.manga.persistance.Return;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class GenericResponseRequest<T> {

    private RestTemplate restTemplate = new RestTemplate();

    private String url;

    private ConfigurationRestClient configurationRestClient;


    public GenericResponseRequest(String urlOrigin, ConfigurationRestClient configurationRestClient){
        this.url=urlOrigin;
        this.configurationRestClient=configurationRestClient;
    }

    public ResponseEntity<Return<T>> genericCall(String path,
                                                 ParameterizedTypeReference<Return<T>> parameterizedTypeReference,
                                                 HttpMethod typeCall,
                                                 Object postObject,
                                                 String[] ...parameters ) {

        MultiValueMap<String,String> map=new LinkedMultiValueMap<>();
        for(String[] s:parameters){
            if(!s[1].equals("")){
                List<String> list=new ArrayList<>();
                list.add(s[1]);
                map.put(s[0],list);
            }
        }

        URI targetUrl = UriComponentsBuilder.fromHttpUrl(url)  // Build the base link
                .path(path)
                .queryParams(map)
                .build()                                                 // Build the URL
                .encode()                                                // Encode any URI items that need to be encoded
                .toUri();

        ResponseEntity<Return<T>> response=null;

        try {

        if(typeCall.equals(HttpMethod.GET)) {

            response = restTemplate.exchange(targetUrl, HttpMethod.GET, configurationRestClient.tokenInHeader(), parameterizedTypeReference);

        }else if (postObject!=null) {

            HttpEntity entity=null;

            HttpHeaders headers = new HttpHeaders();
            //headers.set(EnumVaadinSession.AUTHORIZATION.getNom(), token);
            headers.setContentType(MediaType.APPLICATION_JSON);


            entity = new HttpEntity(postObject, headers);

            response = restTemplate.exchange(targetUrl, HttpMethod.POST, entity, parameterizedTypeReference);

        }
        }catch (HttpClientErrorException h) {

            h.printStackTrace();

            if (response==null||(response.getStatusCodeValue() == 403 || response.getStatusCodeValue() == 401)) {

                VaadinSession.getCurrent().setAttribute(EnumVaadinSession.AUTHORIZATION.getNom(),"");

                return null;
            }
        }
        return response;
    }
}
