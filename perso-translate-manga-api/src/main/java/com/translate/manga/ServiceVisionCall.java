package com.translate.manga;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.services.storage.model.Bucket;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.cloud.vision.v1.*;
import com.google.cloud.vision.v1.Image;
import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;
import com.translate.manga.persistance.CoordonneesJson;
import com.translate.manga.persistance.PageJson;
import com.translate.manga.persistance.PhraseJson;
import com.translate.manga.persistance.repository.PageRepository;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.google.cloud.vision.v1.Feature.Type;
import com.google.protobuf.ByteString;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
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

    @Autowired
    private PageRepository pageRepository;

    private PageJson pageJson;

    @PostConstruct
    public void init() throws IOException {

        pageJson=pageRepository.getByFilename("test2.jpg");

        if(pageRepository.getByFilename("test2.jpg")==null) {

            pageJson=new PageJson();

            //RECUPERATION IMAGE

            InputStream is=new ClassPathResource("test2.jpg").getInputStream();
            byte[] data= ByteStreams.toByteArray(is);
            pageJson.setContent(data);
            pageJson.setFilename("test2.jpg");
            ByteString imgBytes = ByteString.copyFrom(data);
            Image img = Image.newBuilder().setContent(imgBytes).build();




            //IDENTIFICATION
            InputStream isP = new ClassPathResource("project.json").getInputStream();
            GoogleCredentials credential = GoogleCredentials.fromStream(isP).createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));

            ImageAnnotatorSettings imageAnnotatorSettings =
                    ImageAnnotatorSettings.newBuilder()
                            .setCredentialsProvider(FixedCredentialsProvider.create(credential))
                            .build();

            //APPEL GOOGLE API VISION

            try (ImageAnnotatorClient vision = ImageAnnotatorClient.create(imageAnnotatorSettings)) {


                List<AnnotateImageRequest> requests = new ArrayList<>();

                //CONFIGURATION
                Feature feat2 = Feature.newBuilder().setType(Type.TEXT_DETECTION).build();
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
                                    phraseJsons.add(phraseJson);
                                }
                            }
                        }
                    }
                }
                System.out.print("");

            }

            //TRANSLATE

            Translate translate = TranslateOptions.newBuilder().setCredentials(credential).build().getService();


            for (PhraseJson p : phraseJsons) {
                Translation translation =
                        translate.translate(
                                p.getPhrase(),
                                Translate.TranslateOption.sourceLanguage("en"),
                                Translate.TranslateOption.targetLanguage("fr"));
                p.setPhraseTraduite(translation.getTranslatedText());
            }
            System.out.print("");

        }else {
            phraseJsons=pageJson.getPhraseJsonList();
        }

        //ECRITURE

        BufferedImage image= ImageIO.read(new ClassPathResource("test2.jpg").getInputStream());


        for(PhraseJson p:phraseJsons){

            Graphics g =image.getGraphics();

            g.setColor(Color.white);
            g.fillRect(p.getCoordonneesJsonList().get(0).getX()-10,
                    p.getCoordonneesJsonList().get(0).getY()-10,
                    p.getCoordonneesJsonList().get(1).getX()-p.getCoordonneesJsonList().get(0).getX()+15,
                    p.getCoordonneesJsonList().get(2).getY()-p.getCoordonneesJsonList().get(0).getY()+15);
            g.setFont(g.getFont().deriveFont(25f));
            g.setColor(Color.BLACK);
            //g.drawString(StringEscapeUtils.unescapeXml(p.getPhraseTraduite()), p.getCoordonneesJsonList().get(0).getX(), p.getCoordonneesJsonList().get(0).getY());
            drawStringMultiLine(
                    (Graphics2D)g,
                    StringEscapeUtils.unescapeXml(p.getPhraseTraduite()),
                    p.getCoordonneesJsonList().get(2).getY()-p.getCoordonneesJsonList().get(0).getY()+15,
                    p.getCoordonneesJsonList().get(0).getX(), p.getCoordonneesJsonList().get(0).getY()
                    );
            g.dispose();
        }

        ImageIO.write(image, "jpg", new File("testTranslate.jpg"));

        pageJson.setPhraseJsonList(phraseJsons);

        pageRepository.save(pageJson);

    }
    public void drawStringMultiLine(Graphics2D g, String text, int lineWidth, int x, int y) {
        FontMetrics m = g.getFontMetrics();
        if(m.stringWidth(text) < lineWidth) {
            g.drawString(text, x, y);
        } else {
            String[] words = text.split(" ");
            String currentLine = words[0];
            for(int i = 1; i < words.length; i++) {
                if(m.stringWidth(currentLine+words[i]) < lineWidth) {
                    currentLine += " "+words[i];
                } else {
                    g.drawString(currentLine, x, y);
                    y += m.getHeight();
                    currentLine = words[i];
                }
            }
            if(currentLine.trim().length() > 0) {
                g.drawString(currentLine, x, y);
            }
        }
    }
}
