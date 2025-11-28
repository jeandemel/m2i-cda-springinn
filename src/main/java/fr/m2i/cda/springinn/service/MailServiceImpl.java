package fr.m2i.cda.springinn.service;


import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import fr.m2i.cda.springinn.entity.Booking;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class MailServiceImpl implements MailService{
    private JavaMailSender mailSender;

    public MailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendExample() {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

            helper.setTo("example@test.com");
            helper.setFrom("springinn@m2i.fr");
            helper.setSubject("Example mail");
            helper.setText("""
                This is a test email with a bit of <a href="#">HTML</a>
            """,true);

            mailSender.send(mimeMessage);
        } catch (MailException | MessagingException e) {
            throw new RuntimeException("Error while sending mail",e);
        }
    }

    @Override
    public void sendBookingCreation(Booking booking) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sendBookingCreation'");
    }

    @Override
    public void sendBookingConfirmation(Booking booking) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sendBookingConfirmation'");
    }

    @Override
    public void sendBookingRefused(Booking booking) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sendBookingRefused'");
    }


}
