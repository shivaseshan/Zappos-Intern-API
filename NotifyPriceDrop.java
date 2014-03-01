import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class NotifyPriceDrop {

	private static String productIDs;
	private static String eMailID;
	private static String name;
	Map<String,String> emailSentToProductID = new HashMap<String,String>();
	
	// Parse the json and if the discount goes above 20 % then send email notification with sendEmail 
	public void parseJson(String wholePage)
	{
		int percentOffInt = 0;
		boolean isEmailToBeSent = false;
		String percentOff = null;
		String percentOff_Email = null;
		String productID = null;
		List<String> styleIDs = new ArrayList<String>();
		List<String> imageUrls = new ArrayList<String>();
		
		Gson gson = new GsonBuilder().create();
		ResponseObect r = gson.fromJson(wholePage, ResponseObect.class);
		
		for (Product product : r.getProduct())
		{
			productID = product.getProductId();
			for(Styles style : product.getStyles())
			{
				percentOff = style.getPercentOff();
				percentOff = percentOff.substring(0, percentOff.length()-1);
				percentOffInt = Integer.parseInt(percentOff);
				if (percentOffInt >= 20)
				{	
					percentOff_Email = percentOff;
					styleIDs.add(style.getStyleId()) ;
					imageUrls.add(style.getImageUrl());
					isEmailToBeSent = true;					
				}
			}			
		}
		
		if (isEmailToBeSent)
		{
			// If email notification has not already been sent -> send email
			if (!emailSentToProductID.containsKey(productID))
			{
				EmailSender.sendEmail(name, eMailID, percentOff_Email, productID, imageUrls);
				emailSentToProductID.put(productID,percentOff_Email);
			}
			// If email notification has been sent but the discount price has changed -> send email
			else if (emailSentToProductID.containsKey(productID))
			{
				if (!(emailSentToProductID.get(productID).equals(percentOff_Email)))
					EmailSender.sendEmail(name, eMailID, percentOff_Email, productID, imageUrls);
			}
		}
	}
	
	// Read the product ID's one by one by makeRequest and parse and process it using parseJson
	public String readPorducts(String productIds, String key){
		String wholePage = "";
		String[] productIdArray = productIds.split(",");
		try 
		{
			System.out.println("\nProductID(s) added for notification.");
			while (true)
			{
				for (String productId : productIdArray)
				{
					productId = productId.trim();
					wholePage = makeRequest("GET", "http://api.zappos.com/Product/" + productId + "?includes=[\"styles\"]" + "&key=" + key, "");
					parseJson(wholePage);
				}
				Thread.sleep(60000);
				//System.out.println("Slept");
			}
		}
		catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		
		return wholePage;
	}
	
	// Form the HTTP request and return the JSON response as string
	public String makeRequest(String Method, String url, String PostData) {

		URLConnection conn = null;
		BufferedReader data = null;
		String line;
		StringBuffer buf = new StringBuffer();
		URL theURL = null;
		//check if the URL starts with http://
		if(!url.toLowerCase().startsWith("http://"))
			url = "http://"+url;
		try
		{
			theURL = new URL(url);
			conn = theURL.openConnection();
			if(Method == "GET") {
				data = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				while ((line = data.readLine()) != null) {
					buf.append(line + "\n");
				}
				//System.out.println(buf);
				data.close();
			}
		}
		catch ( MalformedURLException e)
		{
			System.out.println("Bad URL: " + theURL);
		}
		catch (IOException e) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.print("IO Error:" + e.getMessage()+" ");
			return "IOERROR";
		}
		return buf.toString();
	}

	public static void main(String[] args) {
		
		// Accept inputs from the user
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter the product id(s) comma separated : ");
		productIDs = sc.nextLine();
		System.out.println("Enter your name : ");
		name = sc.nextLine();
		System.out.println("Enter your email-id : ");
		eMailID = sc.nextLine();
		sc.close();
		
		NotifyPriceDrop npd = new NotifyPriceDrop();
		npd.readPorducts(productIDs,"a73121520492f88dc3d33daf2103d7574f1a3166");
	}
}

// Bean class for JSON response 
class ResponseObect
{
	private List<Product> product = new ArrayList<Product>();

	public List<Product> getProduct() {
		return product;
	}
}

// Bean class for Styles
class Styles
{
	private String percentOff;
	private String styleId;
	private String imageUrl;
	
	public String getImageUrl() {
		return imageUrl;
	}
	public String getPercentOff() {
		return percentOff;
	}
	public String getStyleId() {
		return styleId;
	}	
}

// Bean class for Product 
class Product
{
	String productId;
	public String getProductId() {
		return productId;
	}

	private List<Styles> styles = new ArrayList<Styles>();
	
	public List<Styles> getStyles() {
		return styles;
	}
}
