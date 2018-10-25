package com.translate.manga;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.api.Page;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.services.storage.model.Bucket;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.vision.v1.*;
import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;
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

    @PostConstruct
    public void init() throws IOException {

        InputStream isP=new ClassPathResource("project.json").getInputStream();


        GoogleCredentials credential=GoogleCredentials.fromStream(isP).createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));

        ImageAnnotatorSettings imageAnnotatorSettings =
                ImageAnnotatorSettings.newBuilder()
                        .setCredentialsProvider(FixedCredentialsProvider.create(credential))
                        .build();

        try (ImageAnnotatorClient vision = ImageAnnotatorClient.create(imageAnnotatorSettings)) {

            InputStream is=new ClassPathResource("test.jpg").getInputStream();

            byte[] data= ByteStreams.toByteArray(is);
            ByteString imgBytes = ByteString.copyFrom(data);

            // Builds the image annotation request
            List<AnnotateImageRequest> requests = new ArrayList<>();
            Image img = Image.newBuilder().setContent(imgBytes).build();
            Feature feat = Feature.newBuilder().setType(Type.LABEL_DETECTION).build();
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                    .addFeatures(feat)
                    .setImage(img)
                    .build();
            requests.add(request);

            // Performs label detection on the image file
            BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    System.out.printf("Error: %s\n", res.getError().getMessage());
                    return;
                }

                for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
                    annotation.getAllFields().forEach((k, v) ->
                            System.out.printf("%s : %s\n", k, v.toString()));
                }
            }
        }
    }
}
