package samplePackage;

import org.json.simple.JSONObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import static io.restassured.RestAssured.*;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.Date;
import java.util.Random;

import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;


public class MainClass {
	
	static String auth_token;
	static String AccountId;
	static String PlanId;
	
	public static void AccessToken()
	{
		   System.out.println("********************************************************************************");
		   System.out.println("******* Authentication Token Generation *******");
		   System.out.println("********************************************************************************");
		   RequestSpecification httpRequest = given().auth().preemptive().basic("BeD-JV4bxtlmlrn3ttfKo", "2r6b6IH49HBNYtd2U6l_E"); 
	       Response res = httpRequest.post("https://beta-api.uprise.us/auth/token");
	       int statuscode = res.getStatusCode();
	       System.out.println(" Access Token Status Code ========>>>  "+statuscode);
	       if(statuscode == 200)
	       {
	    	   System.out.println("******* Access Token generated Successfully *******");
	       }
	       else
	       {
	    	   System.out.println("Access Token NOT generated");
	       }
	       //Converting the response body to string
	       String token = res.prettyPrint();
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
	    if(statuscode == 200)
	       {
	    	   System.out.println("******* Customer Added Successfully *******");
	       }
	       else
	       {
	    	   System.out.println("Customer NOT Added");
	       }
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
	    if(statuscode == 200)
	       {
	    	   System.out.println("******* List Customer is Working Successfully *******");
	       }
	       else
	       {
	    	   System.out.println("List Customer is NOT Working");
	       }
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
		if(Successflag == 0)
		{
			System.out.println("Created Account NOT Listed");
		}
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
	    if(statuscode == 200)
	       {
	    	   System.out.println("******* New Plans is Working Successfully *******");
	       }
	       else
	       {
	    	   System.out.println("New Plans is NOT Working");
	       }
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
	    if(statuscode == 200)
	       {
	    	   System.out.println("******* Get Plans is Working Successfully *******");
	       }
	       else
	       {
	    	   System.out.println("Get Plans is NOT Working");
	       }
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
	    if(statuscode == 200)
	       {
	    	   System.out.println("******* New Message is Working Successfully *******");
	       }
	       else
	       {
	    	   System.out.println("New Message is NOT Working");
	       }
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
	    if(statuscode == 200)
	       {
	    	   System.out.println("******* Get Message Details is Working Successfully *******");
	       }
	       else
	       {
	    	   System.out.println("Get Message Details is NOT Working");
	       }
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
	    if(statuscode == 200)
	       {
	    	   System.out.println("******* Get Plan Document is Working Successfully *******");
	       }
	       else
	       {
	    	   System.out.println("Get Plan Document is NOT Working");
	       }
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
	    if(statuscode == 200)
	       {
	    	   System.out.println("******* Get Optimization is Working Successfully *******");
	       }
	       else
	       {
	    	   System.out.println("Get Optimization is NOT Working");
	       }
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
	    System.out.println("Get Optimization Status Code ========>>>  "+statuscode);
	    if(statuscode == 200)
	       {
	    	   System.out.println("******* Get Questionnaire is Working Successfully *******");
	       }
	       else
	       {
	    	   System.out.println("Get Questionnaire is NOT Working");
	       }
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
	    if(statuscode == 200)
	       {
	    	   System.out.println("******* Update Plan Response is Working Successfully *******");
	       }
	       else
	       {
	    	   System.out.println("Update Plan Response is NOT Working");
	       }
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
	    if(statuscode == 200)
	       {
	    	   System.out.println("******* Add Subscription is Working Successfully *******");
	       }
	       else
	       {
	    	   System.out.println("Add Subscription is NOT Working");
	       }
		response.prettyPrint();		
		//String NewPlanMessage = response.path("message").toString();
		// System.out.println(NewPlanMessage);
		System.out.println("********************************************************************************");
		
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
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
			System.out.println("******* Test Execution Started *******");

		 	AccessToken();
		 	AddNewCustomer();
			ListCustomer();
			NewPlans();
		 	GetPlans();
			NewMessage();
			GetMessage();
			GetPlanDocument();
			GetOptimizations();
			GetPlanQuestionnaire();
			UpdatePlanResponse();
			AddSubscription();
			
		 	System.out.println("******* Test Execution Completed *******");
		
	}

}
