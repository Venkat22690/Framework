package Pages;

import org.json.simple.JSONObject;
import org.testng.asserts.SoftAssert;

import static io.restassured.RestAssured.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;


public class APIMethods {
	
	static String auth_token;
	static String AccountId;
	static String PlanId;
	static Properties props = new Properties();
	static String propsFilepath = "/src/test/resources/Config.properties";
	static SoftAssert softAssert = new SoftAssert();
	
	public APIMethods() {
		try {
			FileInputStream fis = new FileInputStream(System.getProperty("user.dir") + propsFilepath);
			props.load(fis);
		} catch (IOException e) {
		}
	}
	
	public static void AccessToken()
	{
		   System.out.println("********************************************************************************");
		   System.out.println("******* Authentication Token Generation *******");
		   System.out.println("********************************************************************************");
		   RequestSpecification httpRequest = given().auth().preemptive().basic(props.getProperty("UserName"), props.getProperty("Password")); 
	       Response res = httpRequest.post(props.getProperty("url")+"/auth/token");
	       int statuscode = res.getStatusCode();
	       System.out.println(" Access Token Status Code ========>>>  "+statuscode);

	        // verify the value of key
	       softAssert.assertEquals(statuscode, 200, "Access Token NOT generated");
	    	  
	       //Converting the response body to string
	       	res.prettyPrint();
	       // Getting the Token alone
	       auth_token = res.path("access_token").toString();
	      // System.out.println(auth_token);
	       // return auth_token;
	}

	public static void AddNewCustomer()
	{
		System.out.println("********************************************************************************");
		System.out.println("******* Add Customer *******");
		System.out.println("********************************************************************************");
		// System.out.println(auth_token);
		
		LocalDate currentdate = LocalDate.now();
		int currentDay = currentdate.getDayOfMonth();
		// Month currentMonth = currentdate.getMonth();
		
		SimpleDateFormat simpleformat = new SimpleDateFormat("MMM");
	    String strMonth= simpleformat.format(new Date());
	      
		// creating an object of Random class   
		Random random = new Random();     
		// Generates random integers 0 to 999  
		int RandomNumber = random.nextInt(1000); 
		
		String emailAddress = "venkat+api+"+strMonth +"+"+currentDay+"+"+RandomNumber+"@uprise.us";
		System.out.println("Created Account is   ======>  "+emailAddress);
		
		JSONObject requestParams = new JSONObject(); 
		requestParams.put("firstName", "Venkat Test"); 
		requestParams.put("lastName", "Auto"); 
		requestParams.put("email", emailAddress);
		
		String payload = requestParams.toJSONString();
		
		RequestSpecification request = given()
				.header("Authorization", "Bearer "+auth_token)
				.contentType("application/json")
				.body(payload);
		
		
		Response response = request.post("https://beta-api.uprise.us/users");
		int statuscode = response.getStatusCode();
	    System.out.println("New Customer Addition Status Code ========>>>  "+statuscode);
	    
	    // verifying the Status Code
	    softAssert.assertEquals(statuscode, 200, "Customer NOT Added");
		
		response.prettyPrint();		
		AccountId = response.path("id").toString();
		// System.out.println(AccountId);
		// return AccountId;
	}

	public static void ListCustomer()
	{
		System.out.println("********************************************************************************");
		System.out.println("******* List Customer - Checking the created customer *******");
		System.out.println("********************************************************************************");
		int Successflag = 0;
		
		RequestSpecification request = given()
				.header("Authorization", "Bearer "+auth_token)
				.contentType("application/json");
		
		Response response = request.get("https://beta-api.uprise.us/users");
		int statuscode = response.getStatusCode();
	    System.out.println("List Customer Status Code ========>>>  "+statuscode);
	    
	    // verifying the Status Code
	    softAssert.assertEquals(statuscode, 200, "List Customer is NOT Working");
		
		// response.prettyPrint();		
		String AccountIdList = response.path("id").toString();
		
		// Checking the Accountid from the list of customers displayed
		String[] AccountDetails = AccountIdList.split(",");
		System.out.println("Total Records Listed =======>  " +AccountDetails.length);
		
		for(int i=0;i<AccountDetails.length; i++)
		{
			if(AccountDetails[i].trim().equalsIgnoreCase(AccountId.trim()))
			{
				System.out.println("******* Created Account Successfully Listed *******");
				Successflag = 1;
				break;
			}
		}
		
		// Fail Condition check 
		softAssert.assertNotEquals(Successflag, 0, "Created Account NOT Listed");
	}
	
	public static void NewPlans()
	{
		System.out.println("********************************************************************************");
		System.out.println("******* New Plans *******");
		System.out.println("********************************************************************************");

		JSONObject requestParams = new JSONObject(); 
		requestParams.put("customerId", AccountId); 
		requestParams.put("subscriptionPlanCode", "Core"); 
		
		String payload = requestParams.toJSONString();
		
		RequestSpecification request = given()
				.header("Authorization", "Bearer "+auth_token)
				.contentType("application/json")
				.body(payload);
		
		Response response = request.post("https://beta-api.uprise.us/plans");
		int statuscode = response.getStatusCode();
	    System.out.println("New Plans Status Code ========>>>  "+statuscode);

	    // verifying the Status Code
	    softAssert.assertEquals(statuscode, 200, "New Plans is NOT Working");
		
		
		response.prettyPrint();		
		String NewPlanMessage = response.path("message").toString();
		System.out.println(NewPlanMessage);
	}

	public static void GetPlans()
	{
		System.out.println("********************************************************************************");
		System.out.println("******* Get Plans Status *******");
		System.out.println("********************************************************************************");

		RequestSpecification request = given()
				.header("Authorization", "Bearer "+auth_token)
				.contentType("application/json");
		

		String BaseURI = "https://beta-api.uprise.us/users/"+AccountId+"/plans";
		// System.out.println(BaseURI);
		
		Response response = request.get(BaseURI);
		int statuscode = response.getStatusCode();
	    System.out.println("Get Plan Status Code ========>>>  "+statuscode);

	    // verifying the Status Code
	    softAssert.assertEquals(statuscode, 200, "Get Plans is NOT Working");
		
		
		response.prettyPrint();		
		String NewPlanMessage = response.path("planState").toString();
		PlanId = response.path("id").toString().replace("[","").replace("]", "").trim();
		System.out.println(NewPlanMessage);
		System.out.println("Newly Created PlanId ======>  "+PlanId);
	}

	public static void NewMessage()
	{
		System.out.println("********************************************************************************");
		System.out.println("******* New Message *******");
		System.out.println("********************************************************************************");
		
		JSONObject requestParams = new JSONObject(); 
		requestParams.put("subject", "Automation API Subject"); 
		requestParams.put("content", "Just Checking is the Message available?"); 
		
		String payload = requestParams.toJSONString();
		
		RequestSpecification request = given()
				.header("Authorization", "Bearer "+auth_token)
				.contentType("application/json")
				.body(payload);
		
		
		Response response = request.post("https://beta-api.uprise.us/users/"+AccountId+"/messages");
		int statuscode = response.getStatusCode();
	    System.out.println("New Message Status Code ========>>>  "+statuscode);

	    // verifying the Status Code
	    softAssert.assertEquals(statuscode, 200, "New Message is NOT Working");
		
		
		response.prettyPrint();		
		String NewPlanMessage = response.path("message").toString();
		System.out.println(NewPlanMessage);
		
	}
	
	public static void GetMessage()
	{
		System.out.println("********************************************************************************");
		System.out.println("******* Get Message Details *******");
		System.out.println("********************************************************************************");

		RequestSpecification request = given()
				.header("Authorization", "Bearer "+auth_token)
				.contentType("application/json");
		

		String BaseURI = "https://beta-api.uprise.us/users/"+AccountId+"/messages";
		// System.out.println(BaseURI);
		
		Response response = request.get(BaseURI);
		int statuscode = response.getStatusCode();
	    System.out.println("Get Message Details Status Code ========>>>  "+statuscode);

	    // verifying the Status Code
	    softAssert.assertEquals(statuscode, 200, "Get Mesage is NOT Working");
		
		
		response.prettyPrint();		
		String Message = response.path("message").toString();
		System.out.println(Message);
	}
	
	public static void GetPlanDocument()
	{
		System.out.println("********************************************************************************");
		System.out.println("******* Get Plan Document *******");
		System.out.println("********************************************************************************");

		RequestSpecification request = given()
				.header("Authorization", "Bearer "+auth_token)
				.contentType("application/json");
		

		String BaseURI = "https://beta-api.uprise.us/plans/"+PlanId+"/documents";
		// System.out.println(BaseURI);
		
		Response response = request.get(BaseURI);
		int statuscode = response.getStatusCode();
	    System.out.println("Get Plan Document Status Code ========>>>  "+statuscode);

	    // verifying the Status Code
	    softAssert.assertEquals(statuscode, 200, "get Plan Document is NOT Working");
		
		
		response.prettyPrint();		
		// String NewPlanMessage = response.path("planState").toString();
		// System.out.println(NewPlanMessage);
	}
	
	public static void GetOptimizations()
	{
		System.out.println("********************************************************************************");
		System.out.println("******* Get Optimization *******");
		System.out.println("********************************************************************************");

		RequestSpecification request = given()
				.header("Authorization", "Bearer "+auth_token)
				.contentType("application/json");
		

		String BaseURI = "https://beta-api.uprise.us/users/"+AccountId+"/optimizations";
		// System.out.println(BaseURI);
		
		Response response = request.get(BaseURI);
		int statuscode = response.getStatusCode();
	    System.out.println("Get Optimization Status Code ========>>>  "+statuscode);

	    // verifying the Status Code
	    softAssert.assertEquals(statuscode, 200, "Get Optimization is NOT Working");
		
		response.prettyPrint();		
		String NewPlanMessage = response.path("message").toString();
		System.out.println(NewPlanMessage);
	}
	
	public static void GetPlanQuestionnaire()
	{
		System.out.println("********************************************************************************");
		System.out.println("******* Get Questionnaire *******");
		System.out.println("********************************************************************************");

		RequestSpecification request = given()
				.header("Authorization", "Bearer "+auth_token)
				.contentType("application/json");
		

		String BaseURI = "https://beta-api.uprise.us/plans/"+PlanId+"/questionnaire";
		// System.out.println(BaseURI);
		
		Response response = request.get(BaseURI);
		int statuscode = response.getStatusCode();
	    System.out.println("Get Questionnaire Status Code ========>>>  "+statuscode);

	    // verifying the Status Code
	    softAssert.assertEquals(statuscode, 200, "Get Questionnaire is NOT Working");
		
		response.prettyPrint();		
		// String NewPlanMessage = response.path("planState").toString();
		// System.out.println(NewPlanMessage);
	}
	
	public static void UpdatePlanResponse()
	{
		System.out.println("********************************************************************************");
		System.out.println("******* Update Plan Response *******");
		System.out.println("********************************************************************************");
		
		JSONObject requestParams = new JSONObject(); 
		requestParams.put("How confident do you feel in managing your finances?", 6); 
		
		String payload = requestParams.toJSONString();
		
		RequestSpecification request = given()
				.header("Authorization", "Bearer "+auth_token)
				.contentType("application/json")
				.body(payload);
		
		
		Response response = request.put("https://beta-api.uprise.us/plans/"+PlanId+"/response");
		int statuscode = response.getStatusCode();
	    System.out.println("Update Plan Response Status Code ========>>>  "+statuscode);

	    // verifying the Status Code
	    softAssert.assertEquals(statuscode, 200, "Update Plan Response is NOT Working");
		
		response.prettyPrint();		
		String PlanResponse = response.path("id").toString().trim();
		//System.out.println(PlanResponse);
		
		if(PlanResponse.equalsIgnoreCase(PlanId))
		{
			System.out.println("******* Plan Id and Plan Response ID are Matching Perfect *******");
		}
		else
		{
			System.out.println("Plan Id and Plan Response ID are NOT Matching");
		}
	}
	
	public static void AddSubscription()
	{
		System.out.println("********************************************************************************");
		System.out.println("******* Add Subscription *******");
		System.out.println("********************************************************************************");
		
		JSONObject requestParams = new JSONObject(); 
		requestParams.put("subscriptionPlanCode", "all_access"); 
		requestParams.put("subscriptionFee", "1000"); 
		requestParams.put("comment", "Partner subscription"); 
		
		String payload = requestParams.toJSONString();
		
		RequestSpecification request = given()
				.header("Authorization", "Bearer "+auth_token)
				.contentType("application/json")
				.body(payload);
		
		
		Response response = request.post("https://beta-api.uprise.us/users/"+AccountId+"/subscription");
		int statuscode = response.getStatusCode();
	    System.out.println("Add Subscription Status Code ========>>>  "+statuscode);
	    
	    // verifying the Status Code
	    softAssert.assertEquals(statuscode, 200, "Add Subscription is NOT Working");
		
		response.prettyPrint();		
		//String NewPlanMessage = response.path("message").toString();
		// System.out.println(NewPlanMessage);
		System.out.println("********************************************************************************");
		
	}
	
	public static void AssertionCalling()
	{
		softAssert.assertAll();
	}
	public static void test()
	{
		LocalDate currentdate = LocalDate.now();
		SimpleDateFormat simpleformat = new SimpleDateFormat("MMM");
		
	      String strMonth= simpleformat.format(new Date());
	      System.out.println(strMonth);
		  int currentDay = currentdate.getDayOfMonth();
			
		// creating an object of Random class   
		Random random = new Random();     
		// Generates random integers 0 to 999  
		int y = random.nextInt(1000);  
	}
	
	
	// public static void main(String[] args) {
	

}
