package com.translate.manga.googleService;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.common.collect.Lists;
import com.translate.manga.persistance.PageJson;
import com.translate.manga.persistance.PhraseJson;
import com.translate.manga.persistance.repository.PageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class GoogleTranslateService {

    @Autowired
    private PageRepository pageRepository;

    @Value(value = "${project.google.key}")
    private String projectGoogleKey;

    public PageJson getTextTranslated(PageJson pageJson) throws IOException {

        if (pageRepository.getByKey(pageJson.getKey()) == null) {

            InputStream isP = new ClassPathResource("project.json").getInputStream();
            GoogleCredentials credential = GoogleCredentials.fromStream(isP).createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));

            Translate translate = TranslateOptions.newBuilder().setCredentials(credential).build().getService();


            for (PhraseJson p : pageJson.getPhraseJsonList()) {
                Translation translation =
                        translate.translate(
                                p.getPhrase(),
                                Translate.TranslateOption.sourceLanguage("en"),
                                Translate.TranslateOption.targetLanguage("fr"));
                p.setPhraseTraduite(translation.getTranslatedText());
            }
            return pageJson;

        } else {
            return pageJson;
        }
    }
}
