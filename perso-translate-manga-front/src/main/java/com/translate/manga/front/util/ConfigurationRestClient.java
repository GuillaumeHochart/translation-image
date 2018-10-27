package com.translate.manga.front.util;

import com.translate.manga.front.Enum.EnumVaadinSession;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Optional;

@Component
public class ConfigurationRestClient<T> {

    public HttpEntity tokenInHeader(){


        String token= ((String)VaadinSession.getCurrent().getAttribute(EnumVaadinSession.AUTHORIZATION.getNom()).toString()).replaceAll("[\\[\\](){}]","");

        if(token==null){
            Cookie cookies[] = VaadinService.getCurrentRequest().getCookies();
            Optional<Cookie> rememberMeCookie = Arrays.stream(cookies).filter(c -> c.getName().equals(EnumVaadinSession.AUTHORIZATION.getNom())&&c.getValue()!="").findFirst();

            if (rememberMeCookie.isPresent()) {
                token= URLDecoder.decode(rememberMeCookie.get().getValue());
            }
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set(EnumVaadinSession.AUTHORIZATION.getNom(), token);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        return entity;
    }

    public HttpEntity<T> tokenInHeaderWithBody(T body){

        HttpEntity<T> entity=null;


        Object o=VaadinSession.getCurrent().getAttribute(EnumVaadinSession.AUTHORIZATION.getNom());

        if(o==null){
            Cookie cookies[] = VaadinService.getCurrentRequest().getCookies();
            Optional<Cookie> rememberMeCookie = Arrays.stream(cookies).filter(c -> c.getName().equals(EnumVaadinSession.AUTHORIZATION.getNom())&&c.getValue()!="").findFirst();

            if (rememberMeCookie.isPresent()) {
                o= URLDecoder.decode(rememberMeCookie.get().getValue());
            }
        }
        if(o!=null) {
            String token = o.toString().replaceAll("[\\[\\](){}]", "");

            HttpHeaders headers = new HttpHeaders();
            //headers.set(EnumVaadinSession.AUTHORIZATION.getNom(), token);
            headers.setContentType(MediaType.APPLICATION_JSON);


            entity = new HttpEntity<T>(body, headers);
        }else {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            entity=new HttpEntity<>(body,headers);
        }

        return entity;
    }
}
