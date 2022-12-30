package ncu.cc.pdfwork;

import ncu.cc.pdfwork.services.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PdfworkApplication implements CommandLineRunner {
	@Autowired
	private PdfService pdfService;

	public static void main(String[] args) {
		SpringApplication.run(PdfworkApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("Converting PDF file(s)");

		String file = "/home/saber/Downloads/AN-0008.pdf";

		pdfService.maskingPageNumber(file, 292, 48, 10, 10);
//		if (args.length == 0) {
//			int numberOfPages = pdfService.processing("../../Downloads/doc.pdf", 1, 1);
//			System.out.println(numberOfPages);
//		} else {
//			int fromPage = 1;
//			int doiNumber = 1;
//			for (String f: args) {
//				int numberOfPages = pdfService.processing(f, fromPage, doiNumber);
//				doiNumber++;
//				fromPage += numberOfPages;
//			}
//		}
	}
}
