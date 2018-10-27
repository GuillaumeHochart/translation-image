package com.translate.manga.draw;

import com.translate.manga.persistance.PageJson;
import com.translate.manga.persistance.PhraseJson;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

@Service
public class UtilsDrawService {

    public PageJson placeComposant (PageJson pageJson) throws IOException {

        ByteArrayInputStream bis = new ByteArrayInputStream(pageJson.getContentNotModified());

        BufferedImage image= ImageIO.read(bis);


        for(PhraseJson p:pageJson.getPhraseJsonList()){

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

        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        ImageIO.write(image, pageJson.getFormatName(),byteArrayOutputStream);

        pageJson.setContentTraducted(byteArrayOutputStream.toByteArray());
        return pageJson;
    }

    private void drawStringMultiLine(Graphics2D g, String text, int lineWidth, int x, int y) {
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
