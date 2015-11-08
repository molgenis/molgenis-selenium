package org.molgenis.selenium.test.mappingservice;

import static java.util.Arrays.asList;

import java.util.List;

import org.molgenis.JenkinsConfig;
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
public class MappingProjectsOverviewTest extends AbstractSeleniumTest
{
	private static final Logger LOG = LoggerFactory.getLogger(MappingProjectsOverviewTest.class);
	private MappingProjectsModel model;

	@BeforeClass
	public void beforeClass()
	{
		token = restClient.login(uid, pwd).getToken();
		tryDeleteEntities("HOP_selenium", "HOP_GENDER_Ref_selenium", "FOOD_POTATOES_Ref_selenium",
				"DIS_HBP_Ref_selenium", "lifelines_test", "test_GENDER_Ref_test", "test_NUCHTER1_Ref_test",
				"test_FOOD59A1_Ref_test", "test_HEALTH351_Ref_test", "prevend_test", "test_SEX_Ref_test");
		restClient.logout(token);
		importFiles("org/molgenis/selenium/mappingservice/mappingservice-test.xlsx");
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
		model = homepage.selectMappingService();
		tryDeleteData("MappingProject", "MappingTarget", "EntityMapping", "AttributeMapping");
	}

	@AfterMethod
	public void afterMethod()
	{
		tryDeleteData("MappingProject", "MappingTarget", "EntityMapping", "AttributeMapping");
	}

	@Test
	public void testCreateMappingProject()
	{
		List<List<String>> mappingProjectsTable = model.addNewMappingProject("Hop hop hop", "HOP_selenium")
				.backToMappingProjectsOverview().getMappingProjectsTable();
		Assert.assertEquals(mappingProjectsTable, asList(asList("", "Hop hop hop", "admin", "HOP_selenium", "")));
	}

	@Test
	public void testCopyAndDeleteMappingProject()
	{
		List<List<String>> mappingProjectsTable = model.addNewMappingProject("Hop hop hop", "HOP_selenium")
				.backToMappingProjectsOverview().getMappingProjectsTable();
		Assert.assertEquals(mappingProjectsTable, asList(asList("", "Hop hop hop", "admin", "HOP_selenium", "")));
		mappingProjectsTable = model.copyMappingProject("Hop hop hop").getMappingProjectsTable();
		Assert.assertEquals(mappingProjectsTable, asList(asList("", "Hop hop hop", "admin", "HOP_selenium", ""),
				asList("", "Hop hop hop - Copy", "admin", "HOP_selenium", "")));
		model.deleteMappingProject("Hop hop hop");
		Assert.assertEquals(mappingProjectsTable,
				asList(asList("", "Hop hop hop - Copy", "admin", "HOP_selenium", "")));
	}
}
