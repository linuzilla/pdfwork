package ncu.cc.pdfwork.services;

import java.io.IOException;

public interface PdfService {
    int processing(String source, int fromPage, int doiNumber) throws IOException;
    void maskingPageNumber(String file, int x, int y, int width, int height) throws IOException;
}
