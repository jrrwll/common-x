package org.dreamcat.common.conv.cli;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.dreamcat.common.conv.Pdf2imgConverter;
import org.dreamcat.common.conv.PdfConverter;
import org.dreamcat.common.conv.PdfSplitter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Create by tuke on 2019-03-31
 */
@Slf4j
@SpringBootApplication
public class ImageMagickApplication implements CommandLineRunner {

    private final String outputDirectory =
            System.getenv("HOME") + "/data/tmp";
    private final PdfSplitter splitter = new PdfTKSplitter();

    public static void main(String[] args) {
        SpringApplication.run(ImageMagickApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("You give args: {}", Arrays.toString(args));
        if (args.length < 1) {
            log.info("You didn't specify the directory, so I just skip it.");
            return;
        }

        String pdfDirectory = args[0];
        log.info("Start to convert pdf files in {}", pdfDirectory);

        Map<String, Pdf2imgConverter> converterMap = new HashMap<>();
        //converterMap.put("imagemagick", new ImageMagickConverter());
        //converterMap.put("poppler", new PopplerConverter());
        converterMap.put("mutool200", MuToolConverter.builder()
                .resolution(200)
                .build());
        converterMap.put("mutool175", MuToolConverter.builder()
                .resolution(175)
                .build());
        converterMap.put("mutool150", MuToolConverter.builder()
                .resolution(150)
                .build());

        converterMap.forEach((name, converter) -> {
            try {
                convertBy(converter, pdfDirectory, outputDirectory + "/" + name);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
    }

    private void convertBy(Pdf2imgConverter pdfConverter, String pdfDir, String outputDir) {
        PdfConverter converter = new PdfConverter(splitter, pdfConverter::pdf2jpg);
        converter.splitAndConvertDir(pdfDir, outputDir);
    }

    @Bean
    public ImageMagickConverter imageMagickConvertor() {
        return new ImageMagickConverter();
    }

}
