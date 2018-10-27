package com.translate.manga.googleService;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.*;
import com.google.common.collect.Lists;
import com.google.protobuf.ByteString;
import com.translate.manga.persistance.CoordonneesJson;
import com.translate.manga.persistance.PageJson;
import com.translate.manga.persistance.PhraseJson;
import com.translate.manga.persistance.repository.PageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class GoogleVisionService
{

    @Autowired
    private PageRepository pageRepository;

    @Value(value = "${project.google.key}")
    private String projectGoogleKey;

    public PageJson getTextDetectedOnImage(PageJson pageJson) throws IOException {

        PageJson tmp=pageRepository.getByKey(pageJson.getKey());

        if(tmp==null){

            pageJson.setPhraseJsonList(new ArrayList<>());
            //IDENTIFICATION
            InputStream isP = new ClassPathResource(projectGoogleKey).getInputStream();
            GoogleCredentials credential = GoogleCredentials.fromStream(isP).createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
            ImageAnnotatorSettings imageAnnotatorSettings =
                    ImageAnnotatorSettings.newBuilder()
                            .setCredentialsProvider(FixedCredentialsProvider.create(credential))
                            .build();

            //Init image
            ByteString imgBytes = ByteString.copyFrom(pageJson.getContentNotModified());
            Image img = Image.newBuilder().setContent(imgBytes).build();

            List<AnnotateImageResponse> responses=null;

            try (ImageAnnotatorClient vision = ImageAnnotatorClient.create(imageAnnotatorSettings)) {


                List<AnnotateImageRequest> requests = new ArrayList<>();

                //CONFIGURATION
                Feature feat2 = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
                Feature feat = Feature.newBuilder().setType(Feature.Type.DOCUMENT_TEXT_DETECTION).build();

                //REQUEST

                AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                        .addFeatures(feat)
                        .addFeatures(feat2)
                        .setImage(img)
                        .build();

                requests.add(request);

                // Performs label detection on the image file
                BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests);
                responses = response.getResponsesList();

            }
            return textFormation(responses,pageJson);

        }else {
            return tmp;
        }

    }

    public PageJson textFormation(List<AnnotateImageResponse> responses,PageJson pageJson){
        for (AnnotateImageResponse res : responses) {
            for (Page page : res.getFullTextAnnotation().getPagesList()) {
                for (Block b : page.getBlocksList()) {
                    for (Paragraph p : b.getParagraphsList()) {

                        //INIT PHRASE
                        PhraseJson phraseJson = new PhraseJson();

                        for (Word w : p.getWordsList()) {
                            String mot = "";
                            for (Symbol s : w.getSymbolsList()) {
                                if (s.getText() != null) {
                                    mot += s.getText();
                                }
                            }
                            mot += " ";
                            phraseJson.setPhrase(phraseJson.getPhrase() + mot);
                        }

                        phraseJson.setPhrase(phraseJson.getPhrase().replaceAll("null", ""));
                        //ADD COORD
                        for (Vertex v : p.getBoundingBox().getVerticesList()) {
                            CoordonneesJson coordonneesJson = new CoordonneesJson(v.getX(), v.getY());
                            phraseJson.getCoordonneesJsonList().add(coordonneesJson);
                        }


                        if (phraseJson.getPhrase().length() > 3 &&
                                !phraseJson.getPhrase().replaceAll(" ", "").matches("[-/@#$%^&_.?+=()]+")) {
                            pageJson.getPhraseJsonList().add(phraseJson);
                        }
                    }
                }
            }
        }
        return pageJson;
    }
}
