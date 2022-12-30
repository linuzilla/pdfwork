package ncu.cc.pdfwork.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("application.pdf-manipulate")
public class PDFManipulateProperties {
    public enum AlignmentEnum {
        LEFT, CENTER, RIGHT;
    }
    public static class Coordinate {
        private AlignmentEnum alignment;
        private float x;
        private float y;
        private float fontSize;

        public AlignmentEnum getAlignment() {
            return alignment;
        }

        public void setAlignment(AlignmentEnum alignment) {
            this.alignment = alignment;
        }

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }

        public float getFontSize() {
            return fontSize;
        }

        public void setFontSize(float fontSize) {
            this.fontSize = fontSize;
        }
    }
    public static class Header extends Coordinate {
        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
    public static class Doi extends Coordinate {
        private String format;

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }
    }

    public static class PageNumber extends Coordinate {
        private String format;

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }
    }
    private String chineseFont;
    private Header header;
    private Doi doi;
    private PageNumber pageNumber;

    public String getChineseFont() {
        return chineseFont;
    }

    public void setChineseFont(String chineseFont) {
        this.chineseFont = chineseFont;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Doi getDoi() {
        return doi;
    }

    public void setDoi(Doi doi) {
        this.doi = doi;
    }

    public PageNumber getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(PageNumber pageNumber) {
        this.pageNumber = pageNumber;
    }
}
