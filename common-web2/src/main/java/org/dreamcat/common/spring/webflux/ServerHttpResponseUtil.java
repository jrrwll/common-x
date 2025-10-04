package org.dreamcat.common.spring.webflux;

import java.io.File;
import org.dreamcat.common.util.ObjectUtil;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.ZeroCopyHttpOutputMessage;
import org.springframework.http.server.reactive.HttpHeadResponseDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * Create by tuke on 2020/4/11
 */
public final class ServerHttpResponseUtil {

    private ServerHttpResponseUtil() {
    }

    public static Mono<ServerResponse> download(File file, String filename) {
        Resource resource = new FileSystemResource(file);
        return ServerResponse.ok()
                // Content-Disposition
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" + filename)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .body(BodyInserters.fromResource(resource));
    }

    public static Mono<Void> download(
            ServerHttpResponse response, File file, boolean asAttachment) {
        return download(response, file, file.getName(), asAttachment);
    }

    public static Mono<Void> download(
            ServerHttpResponse response, File file, String filename, boolean asAttachment) {
        return download(response, file, filename, null, asAttachment);
    }

    public static Mono<Void> download(
            ServerHttpResponse response, File file, String filename, String type,
            boolean asAttachment) {
        long length = file.length();

        HttpHeaders headers = response.getHeaders();
        if (asAttachment) {
            headers.setContentDisposition(
                    ContentDisposition.parse("attachment; filename=" + filename));
        }

        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        if (ObjectUtil.isNotBlank(type)) {
            try {
                mediaType = MediaType.parseMediaType(type);
            } catch (InvalidMediaTypeException ignored) {
            }
        }
        headers.setContentType(mediaType);
        headers.setContentLength(length);

        // handle HEAD request
        if (response instanceof HttpHeadResponseDecorator) {
            HttpHeadResponseDecorator headResponse = (HttpHeadResponseDecorator) response;
            return headResponse.writeWith(Mono.empty());
        }

        ZeroCopyHttpOutputMessage zeroCopyResponse = (ZeroCopyHttpOutputMessage) response;
        return zeroCopyResponse.writeWith(file, 0, length);
    }
}
