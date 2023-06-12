package org.dreamcat.common.conv;

/**
 * Create by tuke on 2021/3/7
 */
public interface Pdf2imgConverter {

    void pdf2jpg(String inputPath, String outputPath) throws Exception;

    void pdf2png(String inputPath, String outputPath) throws Exception;
}
