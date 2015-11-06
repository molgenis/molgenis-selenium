package org.molgenis.selenium.test;

import java.io.File;
import java.io.IOException;

import org.molgenis.DriverType;
import org.molgenis.JenkinsConfig;
import org.molgenis.data.rest.client.MolgenisClient;
import org.molgenis.selenium.model.HomepageModel;
import org.molgenis.selenium.model.ImporterModel;
import org.molgenis.selenium.model.ImporterModel.EntitiesOptions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(classes =
{ JenkinsConfig.class, Config.class })
public class UploadAppTest extends AbstractTestNGSpringContextTests
{
	private static final Logger LOG = LoggerFactory.getLogger(UploadAppTest.class);
	private WebDriver driver;
	private String token;

	@Value("${test.baseurl}")
	private String baseURL;

	@Value("${test.uid}")
	private String uid;

	@Value("${test.pwd}")
	private String pwd;

	private ImporterModel model;
	@Autowired
	private MolgenisClient restClient;

	@BeforeClass
	public void beforeClass()
	{
		driver = DriverType.FIREFOX.getDriver();
	}

	@AfterClass
	public void afterClass()
	{
		this.driver.close();
	}

	@BeforeMethod
	public void beforeMethod()
	{
		driver.get(baseURL);
		HomepageModel homePage = PageFactory.initElements(driver, HomepageModel.class);
		model = homePage.openSignInDialog().signIn(uid, pwd).selectUpload();
		token = restClient.login(uid, pwd).getToken();
	}

	@AfterMethod
	public void afterMethod()
	{
		model.signOut();
		restClient.logout(token);
	}

	@Test
	public void xlsx() throws IOException, InterruptedException
	{
		LOG.info("Test XLSX upload...");
		tryDeleteEntities("org_molgenis_test_TypeTest", "TypeTestRef", "Person", "Location");
		File xlsxFile = ImporterModel.getFile("org/molgenis/selenium/emx/xlsx/emx_all_datatypes.xlsx");

		LOG.info("Import XLSX file in ADD mode...");
		model.importFile(xlsxFile, EntitiesOptions.ADD);
		Assert.assertEquals(model.getMessageHeader(), "Import success");
		Assert.assertEquals(model.getMessage(),
				"Imported 5 TypeTestRef entities.\nImported 38 org_molgenis_test_TypeTest entities.");
		model.finish();

		LOG.info("Import XLSX file in ADD_UPDATE mode...");
		model.importFile(xlsxFile, EntitiesOptions.ADD_UPDATE);
		Assert.assertEquals(model.getMessageHeader(), "Import success");
		Assert.assertEquals(model.getMessage(),
				"Imported 5 TypeTestRef entities.\nImported 38 org_molgenis_test_TypeTest entities.");
		model.finish();

		LOG.info("Import XLSX file in UPDATE mode...");
		model.importFile(xlsxFile, EntitiesOptions.UPDATE);
		Assert.assertEquals(model.getMessageHeader(), "Import success");
		Assert.assertEquals(model.getMessage(),
				"Imported 5 TypeTestRef entities.\nImported 38 org_molgenis_test_TypeTest entities.");
		model.finish();

		tryDeleteEntities("org_molgenis_test_TypeTest", "TypeTestRef", "Person", "Location");

	}

	@Test
	public void csvZip() throws IOException, InterruptedException
	{
		tryDeleteEntities("org_molgenis_test_TypeTestCSV", "org_molgenis_test_TypeTestRefCSV",
				"org_molgenis_test_PersonCSV", "org_molgenis_test_LocationCSV");
		File csvZipFile = ImporterModel.getFile("org/molgenis/selenium/emx/csv.zip/emx_all_datatypes_csv.zip");
		LOG.info("Import CSV Zipfile in ADD mode...");
		model.importFile(csvZipFile, EntitiesOptions.ADD);
		Assert.assertEquals(model.getMessageHeader(), "Import success");
		Assert.assertEquals(model.getMessage(),
				"Imported 5 org_molgenis_test_TypeTestRefCSV entities.\nImported 38 org_molgenis_test_TypeTestCSV entities.");
		model.finish();

		LOG.info("Import CSV Zipfile in ADD_UPDATE mode...");
		model.importFile(csvZipFile, EntitiesOptions.ADD_UPDATE);
		Assert.assertEquals(model.getMessageHeader(), "Import success");
		Assert.assertEquals(model.getMessage(),
				"Imported 5 org_molgenis_test_TypeTestRefCSV entities.\nImported 38 org_molgenis_test_TypeTestCSV entities.");
		model.finish();

		LOG.info("Import CSV Zipfile in UPDATE mode...");
		model.importFile(csvZipFile, EntitiesOptions.UPDATE);
		Assert.assertEquals(model.getMessageHeader(), "Import success");
		Assert.assertEquals(model.getMessage(),
				"Imported 5 org_molgenis_test_TypeTestRefCSV entities.\nImported 38 org_molgenis_test_TypeTestCSV entities.");
		model.finish();
		tryDeleteEntities("org_molgenis_test_TypeTestCSV", "org_molgenis_test_TypeTestRefCSV",
				"org_molgenis_test_PersonCSV", "org_molgenis_test_LocationCSV");
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
