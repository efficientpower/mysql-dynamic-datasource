package org.wjh;

import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.*;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.FileInputStream;

/**
 * @Author wangjihui
 * @Date 2025/5/21
 */
public class Hello {
    public static void main(String[] args) throws Exception{

//        //加载 pdf 文档
//        PDFParser parser = new PDFParser(new RandomAccessReadBuffer(input));
//        parser.parse();
//        document = parser.getPDDocument();
//        // 获取页码
//        int pages = document.getNumberOfPages();
//        // 读文本内容
//        PDFTextStripper stripper = new PDFTextStripper();
//        // 设置按顺序输出
//        stripper.setSortByPosition(true);
//        stripper.setStartPage(1);
//        stripper.setEndPage(pages);
//        String content = stripper.getText(document);

        File fontFile = new File("C:/Windows/Fonts/msyhbd.ttc");
        PDDocument doc = new PDDocument();

        for(int i=0; i<3; i++){
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            PDPageContentStream stream = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true);
            float margin = 50;

            // 添加页眉
            stream.beginText();
            stream.setFont(new PDType1Font(Standard14Fonts.FontName.TIMES_ROMAN), 12);
            stream.newLineAtOffset(margin, page.getMediaBox().getHeight() - margin + 10); // 调整Y位置以适应页眉
            stream.showText("hello");
            stream.endText();

            if(i < 2){
                // 添加正文
                stream.beginText();
                PDType0Font font = PDType0Font.load(doc, fontFile);
                stream.setFont(font, 20);
                stream.newLineAtOffset(margin, page.getMediaBox().getHeight() - margin * 2); // 开始正文的位置
                stream.showText("计算");
                stream.endText();
            }else{
                PDImageXObject image = PDImageXObject.createFromFile("C:\\Users\\wangjihui\\project\\4o28b0625501ad13015501ad2bfc0045.jpg", doc);
                stream.drawImage(image, 0,0,image.getWidth(), image.getHeight());
            }
            stream.close();
        }

        String path = "C:\\Users\\wangjihui\\project\\hello.pdf";
        doc.save(path);
        doc.close();

        PDFParser parser =  new PDFParser(new RandomAccessReadBuffer(new FileInputStream(new File(path))));
        doc = parser.parse();
        int pageSize = doc.getNumberOfPages();
        for(int i= 0; i<pageSize; i++){
            PDPage pg = doc.getPage(i);
            pg.getBBox();
        }

        PDFTextStripper stripper = new PDFTextStripper();
        stripper.setStartPage(0);
        stripper.setEndPage(pageSize);
        stripper.setSortByPosition(true);
        String text = stripper.getText(doc);
        doc.close();
    }
}
