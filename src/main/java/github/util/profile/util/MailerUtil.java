package github.util.profile.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

/**
 * Created by drewcheng on 1/27/17.
 */
public class MailerUtil {

    private static final Logger LOG = LogManager.getLogger(MailerUtil.class);

    protected Session session;

    public MailerUtil() {

    }

    public MailerUtil(final String username, final String password, String host, String port, String auth, String starttls) {
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", auth);
        prop.put("mail.smtp.starttls.enable", starttls);
        prop.put("mail.smtp.host", host);
        prop.put("mail.smtp.port", port);
        session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    public void sendMail(String from,
                         String to,
                         String subject,
                         String body) {
        Message message = new MimeMessage(session);
        MimeMultipart cover = new MimeMultipart("alternative");
        MimeMultipart content = new MimeMultipart("related");
        MimeBodyPart wrapper = new MimeBodyPart();
        MimeBodyPart htmlPart = new MimeBodyPart();
        MimeBodyPart textPart = new MimeBodyPart();

        try {
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setSentDate(new Date());

            textPart.setText(body, "utf-8");
            cover.addBodyPart(textPart);

            htmlPart.setContent(new String((body).getBytes(), "iso-8859-1"), "text/html; charset=iso-8859-1");
            cover.addBodyPart(htmlPart);

            wrapper.setContent(cover);
            content.addBodyPart(wrapper);

            message.setContent(content);

            LOG.debug(message.toString());

            Transport.send(message);
            LOG.debug("Successfully sent email to: " + to);
        } catch (MessagingException e) {
            LOG.error("MessagingException: " + to, e);
        } catch (UnsupportedEncodingException e) {
            LOG.error("UnsupportedEncodingExcpetion: " + to, e);
        } catch (Exception e) {
            LOG.error("Exception: " + to, e);
        }
    }
}
