package org.dreamcat.common.conv.cli;

import org.dreamcat.common.conv.Pdf2imgConverter;
import org.dreamcat.common.io.FileUtil;
import org.dreamcat.common.util.ShellUtil;

/**
 * Create by tuke on 2019-04-02
 */
public class PopplerConverter implements Pdf2imgConverter {

    @Override
    public void pdf2jpg(String inputPath, String outputPath) throws Exception {
        pdf2img(inputPath, outputPath, "jpeg");

    }

    @Override
    public void pdf2png(String inputPath, String outputPath) throws Exception {
        pdf2img(inputPath, outputPath, "png");
    }

    private void pdf2img(String inputPath, String outputPath, String imageType) throws Exception {
        String prefixName = FileUtil.prefix(inputPath);
        String command = String.format("cd %s && pdftocairo -singlefile -%s %s %s",
                outputPath, imageType, inputPath, prefixName);
        ShellUtil.exec("bash", "-c", command);
    }
}
