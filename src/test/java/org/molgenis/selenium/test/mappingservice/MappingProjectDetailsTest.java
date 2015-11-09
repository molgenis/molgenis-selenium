package org.molgenis.selenium.test.mappingservice;

import static java.util.Arrays.asList;

import java.util.List;

import org.molgenis.JenkinsConfig;
import org.molgenis.selenium.model.mappingservice.MappingProjectDetailsModel;
import org.molgenis.selenium.model.mappingservice.MappingProjectsModel;
import org.molgenis.selenium.test.AbstractSeleniumTest;
import org.molgenis.selenium.test.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(classes =
{ JenkinsConfig.class, Config.class })
public class MappingProjectDetailsTest extends AbstractSeleniumTest
{
	private static final Logger LOG = LoggerFactory.getLogger(MappingProjectDetailsTest.class);
	private MappingProjectDetailsModel model;

	@BeforeClass
	public void beforeClass()
	{
		token = restClient.login(uid, pwd).getToken();
		tryDeleteEntities("HOP_selenium", "HOP_GENDER_Ref_selenium", "FOOD_POTATOES_Ref_selenium",
				"DIS_HBP_Ref_selenium", "lifelines_test", "test_GENDER_Ref_test", "test_NUCHTER1_Ref_test",
				"test_FOOD59A1_Ref_test", "test_HEALTH351_Ref_test", "prevend_test", "test_SEX_Ref_test");
		restClient.logout(token);
		importFiles("org/molgenis/selenium/mappingservice/mappingservice-test.xlsx");
		homepage.menu().selectTagWizard().selectEntity("HOP_selenium")
				.tagAttributeWithTerms("Body_Mass_Index", "Height", "Weight")
				.tagAttributeWithTerms("Body_Mass_Index_1", "Height", "Weight")
				.tagAttributeWithTerms("Body_Mass_Index_2", "Height", "Weight");
	}

	@AfterClass
	public void afterClass()
	{
		token = restClient.login(uid, pwd).getToken();
		tryDeleteEntities("HOP_selenium", "HOP_GENDER_Ref_selenium", "FOOD_POTATOES_Ref_selenium",
				"DIS_HBP_Ref_selenium", "lifelines_test", "test_GENDER_Ref_test", "test_NUCHTER1_Ref_test",
				"test_FOOD59A1_Ref_test", "test_HEALTH351_Ref_test", "prevend_test", "test_SEX_Ref_test");
		restClient.logout(token);
	}

	@BeforeMethod
	public void beforeMethod() throws InterruptedException
	{
		tryDeleteData("MappingProject", "MappingTarget", "EntityMapping", "AttributeMapping");
		model = homepage.menu().selectMappingService().addNewMappingProject("Hop hop hop", "HOP_selenium");

	}

	@AfterMethod
	public void afterMethod()
	{
		tryDeleteData("MappingProject", "MappingTarget", "EntityMapping", "AttributeMapping");
	}

	@Test
	public void testAddSource()
	{
		model.addSource("lifelines_test");
		try
		{
			Thread.sleep(10000);
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
