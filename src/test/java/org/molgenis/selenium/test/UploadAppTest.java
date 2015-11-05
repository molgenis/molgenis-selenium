package org.molgenis.selenium.test;

import java.io.IOException;

import org.molgenis.DriverType;
import org.molgenis.JenkinsConfig;
import org.molgenis.data.rest.client.MolgenisClient;
import org.molgenis.selenium.model.SignInModel;
import org.molgenis.selenium.model.UploadAppModel;
import org.molgenis.selenium.model.UploadAppModel.EntitiesOptions;
import org.molgenis.selenium.util.RestApiV1Util;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(classes = JenkinsConfig.class)
public class UploadAppTest extends AbstractTestNGSpringContextTests
{
	private static final Logger LOG = LoggerFactory.getLogger(UploadAppTest.class);
	private WebDriver driver;
	private UploadAppModel model;
	private SignInModel signInModel;

	@Value("${test.baseurl}")
	private String baseURL;

	@Value("${test.uid}")
	private String uid;

	@Value("${test.pwd}")
	private String pwd;

	@BeforeClass
	public void beforeClass() throws InterruptedException
	{
		this.driver = DriverType.FIREFOX.getDriver();
		driver.get(baseURL + "/");
		MolgenisClient molgenisClient = RestApiV1Util.createMolgenisClientApiV1(baseURL, LOG);
		signInModel = new SignInModel(driver);
		this.model = new UploadAppModel(driver, molgenisClient);
	}

	@BeforeMethod
	public void beforeMethod() throws InterruptedException
	{
		signInModel.open();
		signInModel.signIn(uid, pwd);
	}

	@AfterMethod
	public void afterMethod() throws InterruptedException
	{
		signInModel.signOut();
	}

	@Test
	public void xlsx() throws IOException, InterruptedException
	{
		model.deleteXlsxEmxAllDatatypes(uid, pwd);

		model.open();

		model.uploadXlsxEmxAllDatatypes(EntitiesOptions.ADD, LOG);

		model.uploadXlsxEmxAllDatatypes(EntitiesOptions.ADD_UPDATE, LOG);

		model.uploadXlsxEmxAllDatatypes(EntitiesOptions.UPDATE, LOG);

		model.deleteXlsxEmxAllDatatypes(uid, pwd);
	}

	@Test
	public void csvZip() throws IOException, InterruptedException
	{
		model.deleteCsvZipEmxAllDatatypes(uid, pwd);

		model.open();

		model.uploadCsvZipEmxAllDatatypes(EntitiesOptions.ADD, LOG);

		model.uploadCsvZipEmxAllDatatypes(EntitiesOptions.ADD_UPDATE, LOG);

		model.uploadCsvZipEmxAllDatatypes(EntitiesOptions.UPDATE, LOG);

		model.deleteCsvZipEmxAllDatatypes(uid, pwd);
	}

	@AfterClass
	public void afterClass() throws InterruptedException
	{
		// Clear cookies
		this.driver.manage().deleteAllCookies();

		// Close driver
		this.driver.close();

		// Close driver
		this.driver.quit();
	}
}
