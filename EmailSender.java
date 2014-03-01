import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class EmailSender {

	// Captializing the first character of the name
	public static String capitilzationName(String name)
	{
		char ch = name.charAt(0);
		ch = Character.toUpperCase(ch);
		name = name.toLowerCase();
		name = name.substring(1,name.length());
		name = ch + name;
		
		return name;
	}
	
	// Using JavaMail and sending Email 
	public static boolean sendEmail(String name, String to, String percentOff, String productID, List<String> imageUrls)
	{
		String[] nameArray = name.split(" ");
		String capitalizedString = null;
		for (String nameToCapitalize : nameArray)
		{
			capitalizedString += EmailSender.capitilzationName(nameToCapitalize) + " ";
		}
		capitalizedString = capitalizedString.substring(4);
		name = capitalizedString;
		
		final String userEmail = "internshipatzappos@gmail.com";
        final String password = "zappos2014";

        Properties props = new Properties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.password", password);
        props.put("mail.smtp.user", userEmail);
        
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                  protected PasswordAuthentication getPasswordAuthentication() {
                      return new PasswordAuthentication(userEmail, password);
                  }
                });
        try 
        {
            Message message = new MimeMessage(session);
            // set sender
            message.setFrom(new InternetAddress(userEmail));
            // set recipient
            InternetAddress toAddress = new InternetAddress(to);
            message.setRecipient(Message.RecipientType.TO, toAddress);
            // set subject
            message.setSubject("Notification for price drop");
            // set message body
            message.setText("Dear " + capitalizedString.trim() + ","
                + "\n\nThis is a notification for the product ID " + productID +". " + "The following style(s) are discounted " + percentOff + "% !!" + "\n" + imageUrls + "\n\nThanks and Regards \nZappos Team");

            Transport.send(message);

            System.out.println("Notification Sent");
        } 
        catch (MessagingException e) 
        {	
            throw new RuntimeException(e);
        }
        
        return true;
	}
}
