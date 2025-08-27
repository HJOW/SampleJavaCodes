package org.duckdns.hjow.samples.javamail;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/** 
 * <pre>
 * 별도의 도메인이나 메일서버 없이, SMTP 주소와 계정이 있으면 메일을 보낼 수 있는 예제입니다. 
 * 
 * SMTP 주소와 포트, 계정을 제공하는 서비스에 가입되어 있어야 합니다.
 * </pre>
 */
public class MailSender {
    /** 
     * <pre>
     * 메일을 발송합니다. 
     * 
     * smtpProp 에 담겨야 할 속성은 https://javadoc.io/doc/javax.mail/javax.mail-api/1.6.0/javax/mail/package-summary.html 를 참고하세요.
     * 
     * </pre>
     */
    public void send(String addressFrom, final Properties smtpProp, String addressTo, String subject,  String content) {
        Session session = Session.getDefaultInstance(smtpProp, new MailAuthenticator(smtpProp));
        try {
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(addressFrom));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(addressTo));
            msg.setSubject(subject);
            msg.setText(content);
            
            // 발송
            Transport.send(msg);
        } catch(Throwable t) {
            throw new RuntimeException(t.getMessage(), t);
        }
    }


    /** 실행 예제 */
    public static void main(String[] args) {
        MailSender sender = new MailSender();
        String addressFrom, addressTo, subject,  content;
        subject     = "메일 발송 테스트 01";
        content     = "텍스트 메일 테스트입니다.\n123456\nABCDEF";
        
        Properties smtpProp = new Properties();
        smtpProp.setProperty("mail.smtp.host", "********");
        smtpProp.setProperty("mail.user"     , "********");
        smtpProp.setProperty("mail.password" , "********");
        smtpProp.setProperty("mail.smtp.port" , "465");
        smtpProp.setProperty("mail.smtp.auth" , "true");
        smtpProp.setProperty("mail.transport.protocol" , "smtp");
        smtpProp.setProperty("mail.smtp.ssl.enable" , "true");
        smtpProp.setProperty("mail.smtp.ssl.trust" , "*");
        smtpProp.setProperty("mail.smtp.ssl.checkserveridentity" , "false");
        smtpProp.setProperty("mail.smtp.ssl.protocols" , "TLSv1.2");
        smtpProp.setProperty("mail.protocol.ssl.trust" , "*");
        
        addressFrom = "****@****.co.kr";
        addressTo   = "****@****.com";
        
        sender.send(addressFrom, smtpProp, addressTo, subject, content);
    }
}

/** SMTP 계정 인증값 전달을 위한 클래스입니다. */
class MailAuthenticator extends Authenticator {
    protected PasswordAuthentication pauth = null;
    public MailAuthenticator(Properties smtpProp) {
        pauth = new PasswordAuthentication(smtpProp.getProperty("mail.user"), smtpProp.getProperty("mail.password"));
    }
    public PasswordAuthentication getPasswordAuthentication() {
        return pauth;
    }
}
