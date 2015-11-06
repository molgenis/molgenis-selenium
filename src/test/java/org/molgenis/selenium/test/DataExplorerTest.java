package org.molgenis.selenium.test;

import static org.molgenis.selenium.model.ImporterModel.EntitiesOptions.ADD;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.File;

import org.molgenis.DriverType;
import org.molgenis.JenkinsConfig;
import org.molgenis.data.rest.client.MolgenisClient;
import org.molgenis.selenium.model.DataExplorerModel;
import org.molgenis.selenium.model.HomepageModel;
import org.molgenis.selenium.model.ImporterModel;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(classes =
{ JenkinsConfig.class, Config.class })
public class DataExplorerTest extends AbstractTestNGSpringContextTests
{
	private static final Logger LOG = LoggerFactory.getLogger(DataExplorerTest.class);
	private DataExplorerModel model;
	private WebDriver driver;
	@Autowired
	private MolgenisClient restClient;
	private String token;

	@Value("${test.baseurl}")
	private String baseURL;

	@Value("${test.uid}")
	private String uid;

	@Value("${test.pwd}")
	private String pwd;

	@BeforeClass
	public void beforeClass() throws InterruptedException
	{
		driver = DriverType.FIREFOX.getDriver();
		token = restClient.login(uid, pwd).getToken();
		tryDeleteEntities("org_molgenis_test_TypeTest", "TypeTestRef", "Person", "Location");
		File emxAllDatatypes = ImporterModel.getFile("org/molgenis/selenium/emx/xlsx/emx_all_datatypes.xlsx");
		driver.get(baseURL);
		HomepageModel homePage = PageFactory.initElements(driver, HomepageModel.class);
		homePage.openSignInDialog().signIn(uid, pwd).selectUpload().importFile(emxAllDatatypes, ADD).finish().signOut();
	}

	@AfterClass
	public void afterClass()
	{
		tryDeleteEntities("org_molgenis_test_TypeTest", "TypeTestRef", "Person", "Location");
		restClient.logout(token);
		this.driver.close();
	}

	@BeforeMethod
	public void beforeMethod() throws InterruptedException
	{
		driver.get(baseURL);
		model = PageFactory.initElements(driver, HomepageModel.class).openSignInDialog().signIn(uid, pwd)
				.selectDataExplorer();
	}

	@AfterMethod
	public void afterMethod()
	{
		model.signOut();
	}

	@Test
	public void test1() throws InterruptedException
	{
		// Test 1
		model.selectEntity("TypeTest");
		assertEquals(model.getSelectedEntityTitle(), "TypeTest");

		// TODO: What does this test?
		model.next().previous();
		assertTrue(driver.getCurrentUrl().endsWith("dataexplorer?entity=org_molgenis_test_TypeTest"));

	}

	@Test
	public void test2() throws InterruptedException
	{
		// Test 2
		driver.get(baseURL + "/menu/main/dataexplorer?entity=org_molgenis_test_TypeTest");
		assertEquals(model.getSelectedEntityTitle(), "TypeTest");
		model.next();
		assertTrue(driver.getCurrentUrl().endsWith("dataexplorer?entity=org_molgenis_test_TypeTest"));
	}

	private void tryDeleteEntities(String... names)
	{
		LOG.info("Delete entities if present...");
		for (String name : names)
		{
			try
			{
				LOG.info("Delete {}...", name);
				restClient.deleteMetadata(token, name);
			}
			catch (Exception ex)
			{
				LOG.info("Failed to delete entity {}. {}", name, ex.getMessage());
			}
		}
	}

}