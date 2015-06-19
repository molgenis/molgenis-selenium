package org.molgenis.selenium.test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.molgenis.DriverType;
import org.molgenis.JenkinsConfig;
import org.molgenis.selenium.model.DataExplorerAppModel;
import org.molgenis.selenium.model.UploadAppModel;
import org.molgenis.selenium.model.UploadAppModel.EntitiesOptions;
import org.molgenis.selenium.util.SignUtil;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@ContextConfiguration(classes = JenkinsConfig.class)
public class UploadAppTest extends AbstractTestNGSpringContextTests
{
	private static final Logger LOG = LoggerFactory.getLogger(UploadAppTest.class);
	private WebDriver driver;
	private UploadAppModel model;
	private DataExplorerAppModel dataExplorerAppModel;
	
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
		model = new UploadAppModel(driver);
		dataExplorerAppModel = new DataExplorerAppModel(driver);

		// Sign in UI
		SignUtil.signIn(driver, baseURL, uid, pwd, LOG);
	}

	@Test
	public void xlsx() throws IOException, InterruptedException
	{
		// get the upload app
		driver.get(baseURL);

		// Open upload app
		model.open();
	
		model.uploadXlsxEmxAllDatatypes(EntitiesOptions.ADD, LOG);
	
		model.uploadXlsxEmxAllDatatypes(EntitiesOptions.ADD_UPDATE, LOG);
	
		model.uploadXlsxEmxAllDatatypes(EntitiesOptions.UPDATE, LOG);
	
		// TODO implement remove package functionality remove all package when is available
		List<String> entitiesNames = Arrays.asList("org_molgenis_test_TypeTest", "org_molgenis_test_TypeTestRef",
				"org_molgenis_test_Person", "org_molgenis_test_Location");
		dataExplorerAppModel.deleteEntitiesByFullName(driver, baseURL, entitiesNames, LOG);
	}

	@Test
	public void csvZip() throws IOException, InterruptedException
	{
		// get the upload app
		driver.get(baseURL);

		// Open upload app
		model.open();

		model.uploadCsvZipEmxAllDatatypes(EntitiesOptions.ADD, LOG);

		model.uploadCsvZipEmxAllDatatypes(EntitiesOptions.ADD_UPDATE, LOG);

		model.uploadCsvZipEmxAllDatatypes(EntitiesOptions.UPDATE, LOG);

		List<String> entitiesNames = Arrays.asList("org_molgenis_test_TypeTestCSV", "org_molgenis_test_TypeTestRefCSV",
				"org_molgenis_test_PersonCSV", "org_molgenis_test_LocationCSV");
		dataExplorerAppModel.deleteEntitiesByFullName(driver, baseURL, entitiesNames, LOG);
	}

	@AfterClass
	public void afterClass() throws InterruptedException
	{
		// Clear cookies
		this.driver.manage().deleteAllCookies();

		// Sign out
		SignUtil.signOut(this.driver, LOG);

		// Close driver
		this.driver.close();
	}
}
