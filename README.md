There are two files for the application : NotifyPriceDrop.java and EmailSender.java. NotifyPriceDrop.java is the file where the main function is defined and all the logic of the requirement is handled. EmailSender.java is where the logic for composing  and sending an email is done.

The program works as follows: 
1) Program asks for the user to enter one or more products ID's he wishes to follow in comma separated format.
2) Program asks for the users name and E-Mail ID to which the email notification has to be sent.
3) Program then ouputs the message on the console "ProductID(s) added for notification."
4) Internally program keeps checking for the price drop by sending request every 1 min. When a price drop occurs an email is sent to the user notifying him and giving him information on the styles to which the discount is applicable. 
5) Once an user gets an email for a particular product ID he will not recieve further notification for the same product ID again, unless the discount value has changed while still remaining over 20%.