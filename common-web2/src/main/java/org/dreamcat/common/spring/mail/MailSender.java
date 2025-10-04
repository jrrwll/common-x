package org.dreamcat.common.spring.mail;

import java.io.File;
import java.util.List;
import java.util.Map;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/**
 * Create by tuke on 2018/10/13
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MailSender {

    private final JavaMailSenderImpl javaMailSender;

    public Op newOp() {
        return new Op(javaMailSender);
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public static class Op {

        private final JavaMailSenderImpl javaMailSender;
        private final MimeMessage message;
        private final MimeMessageHelper messageHelper;

        private Op(JavaMailSenderImpl javaMailSender) {
            this.javaMailSender = javaMailSender;
            this.message = this.javaMailSender.createMimeMessage();
            this.messageHelper = new MimeMessageHelper(this.message, "UTF-8");
        }

        public Op from() throws MessagingException {
            return from(javaMailSender.getUsername());
        }

        public Op from(String from) throws MessagingException {
            messageHelper.setFrom(from);
            return this;
        }

        public Op to(String... to) throws MessagingException {
            messageHelper.setTo(to);

            return this;
        }

        public Op to(List<String> to) throws MessagingException {
            return to(to.toArray(new String[0]));
        }

        public Op cc(String... cc) throws MessagingException {
            messageHelper.setCc(cc);

            return this;
        }

        public Op cc(List<String> cc) throws MessagingException {
            return cc(cc.toArray(new String[0]));
        }

        public Op bcc(String... bcc) throws MessagingException {
            messageHelper.setBcc(bcc);

            return this;
        }

        public Op bcc(List<String> bcc) throws MessagingException {
            return bcc(bcc.toArray(new String[0]));
        }

        public Op replyTo(String replyTo) throws MessagingException {
            messageHelper.setReplyTo(replyTo);

            return this;
        }

        public Op subject(String subject) throws MessagingException {
            messageHelper.setSubject(subject);

            return this;
        }

        public Op content(String content) throws MessagingException {
            return content(content, true);
        }

        public Op content(String content, boolean html) throws MessagingException {
            messageHelper.setText(content, html);

            return this;
        }

        public Op fileAttachments(Map<String, File> fileAttachments) throws MessagingException {
            for (Map.Entry<String, File> entry : fileAttachments.entrySet()) {
                String attachmentFileName = entry.getKey();
                File file = entry.getValue();
                fileAttachment(attachmentFileName, file);
            }
            return this;
        }

        public Op bytesAttachments(Map<String, byte[]> bytesAttachments) throws MessagingException {
            for (Map.Entry<String, byte[]> entry : bytesAttachments.entrySet()) {
                String attachmentFileName = entry.getKey();
                byte[] bytes = entry.getValue();
                bytesAttachment(attachmentFileName, bytes);
            }
            return this;
        }

        public Op fileAttachment(String filename, File file) throws MessagingException {
            messageHelper.addAttachment(filename, file);

            return this;
        }

        public Op bytesAttachment(String filename, byte[] bytes) throws MessagingException {
            messageHelper.addAttachment(filename, new ByteArrayResource(bytes));

            return this;
        }

        public void send() {
            javaMailSender.send(message);
        }
    }

}
/*
spring:
  # Autowired JavaMailSender
  mail:
    host: smtp.example.com
    port: 465
    username: noreply@@example.com
    password:
    properties:
      smtp:
        auth: true
        starttls:
          enable: true
          required: true
      mail:
        smtp:
          ssl:
            enable: true
 */
