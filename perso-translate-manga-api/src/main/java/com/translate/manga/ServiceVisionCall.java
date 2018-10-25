package com.translate.manga;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.api.Page;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.services.storage.model.Bucket;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.cloud.vision.v1.*;
import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;
import com.translate.manga.persistance.CoordonneesJson;
import com.translate.manga.persistance.PhraseJson;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.google.cloud.vision.v1.Feature.Type;
import com.google.protobuf.ByteString;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ServiceVisionCall {

    private List<PhraseJson> phraseJsons=new ArrayList<>();


    @PostConstruct
    public void init() throws IOException {

        //RECUPERATION IMAGE

        InputStream is=new ClassPathResource("test2.jpg").getInputStream();
        byte[] data= ByteStreams.toByteArray(is);
        ByteString imgBytes = ByteString.copyFrom(data);
        Image img = Image.newBuilder().setContent(imgBytes).build();



        //IDENTIFICATION
        InputStream isP=new ClassPathResource("project.json").getInputStream();
        GoogleCredentials credential=GoogleCredentials.fromStream(isP).createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));

        ImageAnnotatorSettings imageAnnotatorSettings =
                ImageAnnotatorSettings.newBuilder()
                        .setCredentialsProvider(FixedCredentialsProvider.create(credential))
                        .build();

        //APPEL GOOGLE API VISION

        try (ImageAnnotatorClient vision = ImageAnnotatorClient.create(imageAnnotatorSettings)) {


            List<AnnotateImageRequest> requests = new ArrayList<>();

            //CONFIGURATION

            Feature feat2 = Feature.newBuilder().setType(Type.OBJECT_LOCALIZATION).build();
            Feature feat = Feature.newBuilder().setType(Type.DOCUMENT_TEXT_DETECTION).build();

            //REQUEST

            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                    .addFeatures(feat)
                    .addFeatures(feat2)
                    .setImage(img)
                    .build();

            requests.add(request);

            // Performs label detection on the image file
            BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            //TRAITEMENT REPONSE


            for (AnnotateImageResponse res : responses) {
                for (EntityAnnotation annotation : res.getTextAnnotationsList()) {

                    PhraseJson phraseJson=new PhraseJson();
                    phraseJson.setPhrase(annotation.getDescription());

                    for(Vertex v:annotation.getBoundingPoly().getVerticesList()){
                        CoordonneesJson coordonneesJson=new CoordonneesJson(v.getX(),v.getY());
                        phraseJson.getCoordonneesJsonList().add(coordonneesJson);
                    }
                    phraseJsons.add(phraseJson);
                }
            }
            System.out.print("");

            //TRANSLATE

            Translate translate = TranslateOptions.newBuilder().setCredentials(credential).build().getService();




            for(PhraseJson p:phraseJsons) {
                Translation translation =
                        translate.translate(
                                p.getPhrase(),
                                Translate.TranslateOption.sourceLanguage("en"),
                                Translate.TranslateOption.targetLanguage("fr"));
                p.setPhraseTraduite(translation.getTranslatedText());
            }
            System.out.print("");
        }
    }
}
