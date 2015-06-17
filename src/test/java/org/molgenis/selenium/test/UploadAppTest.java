package org.molgenis.selenium.test;

import java.io.IOException;

import org.molgenis.DriverType;
import org.molgenis.JenkinsConfig;
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

		model.uploadOrgMolgenisTestTypeTest(EntitiesOptions.ADD, LOG);

		model.uploadOrgMolgenisTestTypeTest(EntitiesOptions.ADD_UPDATE, LOG);

		model.uploadOrgMolgenisTestTypeTest(EntitiesOptions.UPDATE, LOG);
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
