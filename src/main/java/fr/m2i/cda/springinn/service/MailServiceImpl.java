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


}
