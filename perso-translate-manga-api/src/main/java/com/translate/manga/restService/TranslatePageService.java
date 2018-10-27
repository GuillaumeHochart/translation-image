package com.translate.manga.restService;

import com.translate.manga.draw.UtilsDrawService;
import com.translate.manga.googleService.GoogleTranslateService;
import com.translate.manga.googleService.GoogleVisionService;
import com.translate.manga.persistance.PageJson;
import com.translate.manga.persistance.Return;
import com.translate.manga.persistance.repository.PageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api/translate")/*connected*/
public class TranslatePageService {

    @Autowired
    private UtilsDrawService utilsDrawService;

    @Autowired
    private GoogleTranslateService googleTranslateService;

    @Autowired
    private GoogleVisionService googleVisionService;

    @Autowired
    private PageRepository pageRepository;

    @RequestMapping(value = "/page", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
    public Return<PageJson> createBrokerUser(@RequestBody PageJson pageJson, HttpServletRequest request, HttpServletResponse response) throws IOException {

        PageJson visionEtape=googleVisionService.getTextDetectedOnImage(pageJson);

        PageJson translateEtape=googleTranslateService.getTextTranslated(visionEtape);

        PageJson drawEtape=utilsDrawService.placeComposant(translateEtape);

        pageRepository.save(drawEtape);


        Return<PageJson> pageJsonReturn=new Return<>();
        pageJsonReturn.setResults(drawEtape);
        pageJsonReturn.setSuccess(true);
        pageJsonReturn.setError_code("200");
        return pageJsonReturn;
    }
}
