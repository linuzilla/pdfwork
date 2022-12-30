package ncu.cc.pdfwork.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import ncu.cc.pdfwork.properties.PDFManipulateProperties;
import ncu.cc.pdfwork.utils.ReadfileUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.util.Matrix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

@Service
@EnableConfigurationProperties(PDFManipulateProperties.class)
public class PdfServiceImpl implements PdfService {
    @Autowired
    private PDFManipulateProperties properties;

    static {
        System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
    }

    private static void print(Object o) {
        try {
            System.out.println(new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).writeValueAsString(o));
        } catch (JsonProcessingException e) {
        }
    }

    private String getTargetName(String fileName) {
        String basename = FilenameUtils.getBaseName(fileName);
        String extension = FilenameUtils.getExtension(fileName);
        String pathname = FilenameUtils.getPath(fileName);

        return FilenameUtils.getPrefix(fileName) + pathname + basename + "-new." + extension;
    }

    @Override
    public void maskingPageNumber(String fileName, int x, int y, int width, int height) throws IOException {
        PDDocument document = PDDocument.load(new File(fileName));

        PDPageTree pages = document.getDocumentCatalog().getPages();

        Iterator<PDPage> pageIterator = pages.iterator();

        while (pageIterator.hasNext()) {
            PDPage page = pageIterator.next();

            PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true,true);

            contentStream.addRect(x, y, width, height);
            contentStream.setNonStrokingColor(Color.WHITE);
            contentStream.fill();
            contentStream.close();
        }

        document.save(new FileOutputStream(getTargetName(fileName)));

        document.close();
    }

    @Override
    public int processing(String fileName, int fromPage, int doiNumber) throws IOException {

        String doiString = String.format(properties.getDoi().getFormat(), doiNumber);

        // System.out.println("Basename: " + basename + " , ext: " + extension + " , pathname: " + pathname);

        PDDocument document = PDDocument.load(new File(fileName));
        int numberOfPages = document.getNumberOfPages();

        System.out.println("Number of pages: " + numberOfPages + ", DOI string: " + doiString);

        PDPageTree pages = document.getDocumentCatalog().getPages();

        Iterator<PDPage> pageIterator = pages.iterator();

        PDFont englishFont = PDType1Font.HELVETICA;
//
        // String message = properties.getHeader().getText();
        PDFont font = PDType0Font.load(document, ReadfileUtil.readFrom(properties.getChineseFont()));
        int pageNo = 0;

        for (pageNo = 0; pageIterator.hasNext(); pageNo++) {
            PDPage page = pageIterator.next();

            PDRectangle pageSize = page.getMediaBox();

            if (pageNo == 0) {
                System.out.println("(" + pageSize.getLowerLeftX() + ", " + pageSize.getLowerLeftY() + ") -> (" +
                        pageSize.getUpperRightX() + ", " + pageSize.getUpperRightY() + "), height = " +
                        pageSize.getHeight() + ", width = " + pageSize.getWidth());
            }

            float fontSize = properties.getHeader().getFontSize();
            float stringWidth = font.getStringWidth(properties.getHeader().getText()) * fontSize / 1000f;
            // calculate to center of the page
            int rotation = page.getRotation();
            boolean rotate = rotation == 90 || rotation == 270;
            float pageWidth = rotate ? pageSize.getHeight() : pageSize.getWidth();
            float pageHeight = rotate ? pageSize.getWidth() : pageSize.getHeight();
            double centeredXPosition = rotate ? pageHeight/2f : (pageWidth - stringWidth)/2f;
            double centeredYPosition = rotate ? (pageWidth - stringWidth)/2f : pageHeight/2f;

            // append the content to the existing stream
            PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true,true);

            // writeString(fontSize, font, rotate, (float) centeredXPosition, (float) centeredYPosition, contentStream);

            writeString(font, properties.getHeader().getText(), properties.getHeader(), contentStream);
            writeString(englishFont, doiString, properties.getDoi(), contentStream);
            writeString(englishFont, String.format(properties.getPageNumber().getFormat(), fromPage + pageNo), properties.getPageNumber(), contentStream);

            contentStream.close();
        }

        document.save(new FileOutputStream(getTargetName(fileName)));

        document.close();

        Assert.isTrue(pageNo == numberOfPages, "Number of pages mismatch");

        return numberOfPages;
    }

    private void writeString(PDFont font, String text, PDFManipulateProperties.Coordinate coordinate, PDPageContentStream contentStream) throws IOException {
        float stringWidth = font.getStringWidth(text) * coordinate.getFontSize() / 1000f;
        float x = coordinate.getX();

        switch (coordinate.getAlignment()) {
            case CENTER:
                x -= stringWidth / 2.0f;
                break;
            case RIGHT:
                x -=  stringWidth;
                break;
        }

        contentStream.beginText();
        // set font and font size
        contentStream.setFont(font, coordinate.getFontSize());
        // set text color to red
        contentStream.setNonStrokingColor(0, 0, 0);
        contentStream.setTextMatrix(Matrix.getTranslateInstance(x, coordinate.getY()));
        contentStream.showText(text);
        contentStream.endText();
    }

//    private void writeString(float fontSize, PDFont font, boolean rotate, float centeredXPosition, float centeredYPosition, PDPageContentStream contentStream) throws IOException {
//        contentStream.beginText();
//        // set font and font size
//        contentStream.setFont(font, fontSize);
//        // set text color to red
//        contentStream.setNonStrokingColor(255, 0, 0);
//        if (rotate) {
//            // rotate the text according to the page rotation
//            contentStream.setTextMatrix(Matrix.getRotateInstance(Math.PI/2, centeredXPosition, centeredYPosition));
//        } else {
//            contentStream.setTextMatrix(Matrix.getTranslateInstance(centeredXPosition, centeredYPosition));
//        }
//        contentStream.showText(properties.getHeader().getText());
//        contentStream.endText();
//    }
}
