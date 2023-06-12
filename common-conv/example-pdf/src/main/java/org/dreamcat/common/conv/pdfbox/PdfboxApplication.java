package org.dreamcat.common.conv.pdfbox;

import java.io.File;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Jerry Will
 * @since 2021-07-03
 */
@Slf4j
@SpringBootApplication
public class PdfboxApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(PdfboxApplication.class, args).close();
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("you give args: {}", Arrays.toString(args));
        if (args.length < 2) {
            log.warn("you didn't specify the directory, so I just skip it.");
            return;
        }

        File inputDir = new File(args[0]);
        File outputDir = new File(args[1]);
        if (!inputDir.isDirectory()) {
            log.error("input is not a directory or not found: {}", inputDir);
            return;
        }
        if (!outputDir.isDirectory() || (!outputDir.exists() && !outputDir.mkdirs())) {
            log.error("output is not a dir or failed to create: {}", outputDir);
            return;
        }
    }

}
