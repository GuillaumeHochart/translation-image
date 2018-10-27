package com.translate.manga.front.view;

import com.translate.manga.front.composant.VerticalLayoutPerso;
import com.translate.manga.front.service.DrawPageService;
import com.translate.manga.front.util.UnzipFile;
import com.translate.manga.persistance.PageJson;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


@Push
@StyleSheet("css/accueil.css")
@HtmlImport("styles/shared-styles.html")
@Route("accueil")
public class AccueilView extends VerticalLayoutPerso {

    @Autowired
    public DrawPageService drawPageService;

    @Autowired
    public UnzipFile unzipFile;

    public AccueilView(){

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        getContent().add(upload);

        upload.addSucceededListener(event -> {


            if((buffer.getFileName().substring(buffer.getFileName().lastIndexOf(".") + 1).equals("zip"))){

                List<byte[]> bytes=null;

                try {
                    bytes=unzipFile.readZipStream(buffer.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int i=0;
                for(byte[] b:bytes){
                    test(i+""+buffer.getFileName(),b);
                    i++;
                }

            }else {
                try {
                    test(buffer.getFileName(),IOUtils.toByteArray(buffer.getInputStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        });
    }

    public  void test(String filename, byte[] bytes){

        PageJson pageJson;
        if(filename.substring(filename.lastIndexOf(".") + 1).equals("zip")){
            pageJson=new PageJson("projectTest"+filename,
                    filename,
                    "png",
                    bytes);
        }else {
            pageJson=new PageJson("projectTest"+filename,
                    filename,
                    filename.substring(filename.lastIndexOf(".") + 1),
                    bytes);
        }

        if(pageJson!=null){
            PageJson pageJsonTranslated=drawPageService.getPageJsonTranslateAndDraw(pageJson);

            ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(pageJsonTranslated.getContentTraducted());


            StreamResource resource = new StreamResource(pageJsonTranslated.getFilename(), new InputStreamFactory() {
                @Override
                public InputStream createInputStream() {
                    return byteArrayInputStream;
                }
            });

            getContent().add(new Anchor(resource, "Download!"));


        }
    }

}
