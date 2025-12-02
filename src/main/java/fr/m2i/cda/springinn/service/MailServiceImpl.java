package fr.m2i.cda.springinn.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import fr.m2i.cda.springinn.entity.Booking;
import fr.m2i.cda.springinn.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class MailServiceImpl implements MailService{
    private JavaMailSender mailSender;
    private PasswordEncoder encoder;

    @Value("${server.url}")
    private String serverUrl;
    @Value("${mail.validation.secret}")
    private String validationSecret;

    @Override
    public void sendExample() {
        sendMail("test@test.com","Test Email", """
                The content of the test mail with <a href="#">HTML</a>
                """);
    }

    private void sendMail(String receiver, String subject, String content) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

            helper.setTo(receiver);
            helper.setFrom("springinn@m2i.fr");
            helper.setSubject(subject);
            helper.setText(content,true);

            mailSender.send(mimeMessage);
        } catch (MailException | MessagingException e) {
            throw new RuntimeException("Error while sending mail",e);
        }
    }

    @Override
    public void sendBookingCreation(Booking booking) {
       
         sendMail(booking.getCustomer().getEmail(),"SpringInn - You have placed a Booking", """
You're booking for %s persons on the %s for %s days has been taken in account and will be confirmed by our team.
                """.formatted(booking.getGuestCount(), booking.getStartDate(), booking.getDuration()));
    }

    @Override
    public void sendBookingConfirmation(Booking booking) {
       
         sendMail(booking.getCustomer().getEmail(),"SpringInn - Booking confirmed", """
You're booking for %s persons on the %s for %s days has been confirmed by our team.
                """.formatted(booking.getGuestCount(), booking.getStartDate(), booking.getDuration()));
    }

    @Override
    public void sendBookingRefused(Booking booking) {
       
       
         sendMail(booking.getCustomer().getEmail(),"SpringInn - Booking refused", """
We are sorry but you're booking for %s persons on the %s for %s days can't be honoured.
                """.formatted(booking.getGuestCount(), booking.getStartDate(), booking.getDuration()));
    }

    @Override
    public void sendEmailValidation(User user) {
        String hash = encoder.encode(user.getId()+validationSecret);
        String link = serverUrl+"/api/account/validate/"+user.getId()+"/"+hash;
         sendMail(user.getEmail(),"SpringInn - Email Validation", """
Please follow <a href="%s">this link</a> to validate your email.
                """.formatted(link));
    }

    public MailServiceImpl(JavaMailSender mailSender, PasswordEncoder encoder) {
        this.mailSender = mailSender;
        this.encoder = encoder;
    }


}
