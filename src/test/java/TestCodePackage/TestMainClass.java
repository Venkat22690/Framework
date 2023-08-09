package TestCodePackage;

import org.testng.annotations.Test;
@Test
public class TestMainClass extends APIMethods{


	public void MainMethod()
	{
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
