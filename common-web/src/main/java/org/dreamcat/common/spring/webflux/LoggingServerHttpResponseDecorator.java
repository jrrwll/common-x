package org.dreamcat.common.spring.webflux;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Create by tuke on 2020/3/22
 */
@Slf4j
@Getter
public class LoggingServerHttpResponseDecorator extends ServerHttpResponseDecorator {

    private final boolean saveBody;
    private long endTime;
    private String responseBody;

    public LoggingServerHttpResponseDecorator(ServerHttpResponse delegate, boolean saveBody) {
        super(delegate);
        this.saveBody = saveBody;
    }

    @Override
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        Flux<DataBuffer> buffer = Flux.from(body);
        return super.writeWith(buffer.doOnNext(dataBuffer -> {
            this.endTime = System.currentTimeMillis();
            if (!saveBody) return;

            MediaType mediaType = getHeaders().getContentType();
            if (mediaType == null || !mediaType.isCompatibleWith(
                    MediaType.APPLICATION_JSON)) {
                return;
            }

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                Channels.newChannel(baos).write(dataBuffer.asByteBuffer().asReadOnlyBuffer());

                Charset charset = mediaType.getCharset();
                if (charset == null) charset = StandardCharsets.UTF_8;
                this.responseBody = baos.toString(charset.name());
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }));
    }
}
