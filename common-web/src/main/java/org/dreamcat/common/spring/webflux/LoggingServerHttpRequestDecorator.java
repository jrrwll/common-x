package org.dreamcat.common.spring.webflux;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import reactor.core.publisher.Flux;

/**
 * Create by tuke on 2020/3/22
 */
@Getter
@Slf4j
public class LoggingServerHttpRequestDecorator extends ServerHttpRequestDecorator {

    private final boolean saveBody;
    private long startTime;
    private String requestBody;

    public LoggingServerHttpRequestDecorator(ServerHttpRequest delegate, boolean saveBody) {
        super(delegate);
        this.saveBody = saveBody;
    }

    @Override
    public Flux<DataBuffer> getBody() {
        return super.getBody().doOnNext(dataBuffer -> {
            this.startTime = System.currentTimeMillis();
            if (!saveBody) return;

            MediaType mediaType = getHeaders().getContentType();
            if (mediaType == null) return;

            if (!mediaType.isCompatibleWith(MediaType.APPLICATION_JSON) &&
                    (!mediaType.isCompatibleWith(MediaType.APPLICATION_FORM_URLENCODED))) {
                return;
            }

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                Channels.newChannel(baos).write(dataBuffer.asByteBuffer().asReadOnlyBuffer());

                Charset charset = mediaType.getCharset();
                if (charset == null) charset = StandardCharsets.UTF_8;
                this.requestBody = new String(baos.toByteArray(), charset);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        });
    }
}
