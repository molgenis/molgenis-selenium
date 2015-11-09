package org.molgenis.selenium.test.dataexplorer;

import static org.molgenis.selenium.model.importer.ImporterModel.EntitiesOptions.ADD;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.File;

import org.molgenis.JenkinsConfig;
import org.molgenis.selenium.model.HomepageModel;
import org.molgenis.selenium.model.dataexplorer.DataExplorerModel;
import org.molgenis.selenium.model.importer.ImporterModel;
import org.molgenis.selenium.test.AbstractSeleniumTest;
import org.molgenis.selenium.test.Config;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(classes =
{ JenkinsConfig.class, Config.class })
public class DataExplorerTest extends AbstractSeleniumTest
{
	private static final Logger LOG = LoggerFactory.getLogger(DataExplorerTest.class);
	private DataExplorerModel model;

	@BeforeClass
	public void beforeClass() throws InterruptedException
	{
		token = restClient.login(uid, pwd).getToken();
		tryDeleteEntities("org_molgenis_test_TypeTest", "TypeTestRef", "Person", "Location");
		restClient.logout(token);
		File emxAllDatatypes = ImporterModel.getFile("org/molgenis/selenium/emx/xlsx/emx_all_datatypes.xlsx");
		driver.get(baseURL);
		HomepageModel homePage = PageFactory.initElements(driver, HomepageModel.class);
		homePage.menu().openSignInDialog().signIn(uid, pwd).menu().selectImporter().importFile(emxAllDatatypes, ADD)
				.finish().menu().signOut();
	}

	@AfterClass
	public void afterClass()
	{
		tryDeleteEntities("org_molgenis_test_TypeTest", "TypeTestRef", "Person", "Location");
	}

	@BeforeMethod
	public void beforeMethod() throws InterruptedException
	{
		model = homepage.menu().selectDataExplorer();
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
}