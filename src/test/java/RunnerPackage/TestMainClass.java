package RunnerPackage;

import java.io.IOException;

import org.testng.annotations.Test;

import Pages.APIMethods;
@Test
public class TestMainClass extends APIMethods{


	public void MainMethod() throws IOException
	{
		// TODO Auto-generated method stub
		
			System.out.println("******* Test Execution Started *******");
			
		 	AccessToken();
		 	/* AddNewCustomer();
			ListCustomer();
			NewPlans();
		 	GetPlans();
			NewMessage();
			GetMessage();
			GetPlanDocument();
			GetOptimizations();
			GetPlanQuestionnaire();
			UpdatePlanResponse();
			AddSubscription(); */
			AssertionCalling(); 
			
		 	System.out.println("******* Test Execution Completed *******");
		
	}
}
