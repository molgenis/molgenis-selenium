package org.molgenis.selenium.test;

import java.io.IOException;

import org.molgenis.DriverType;
import org.molgenis.JenkinsConfig;
import org.molgenis.data.rest.client.MolgenisClient;
import org.molgenis.selenium.model.UploadAppModel;
import org.molgenis.selenium.model.UploadAppModel.EntitiesOptions;
import org.molgenis.selenium.util.RestApiV1Util;
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
		this.driver = DriverType.FIREFOX.getDriver();
		MolgenisClient molgenisClient = RestApiV1Util.createMolgenisClientApiV1(baseURL, LOG);
		this.model = new UploadAppModel(driver, molgenisClient);
	}

	/**
	 * Test the upload plugin
	 * 
	 * Upload and remove files
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void testTheUploadPlugin() throws IOException, InterruptedException
	{
		// Xlsx
		this.xlsx();

		// csvZip
		this.csvZip();
	}

	public void xlsx() throws IOException, InterruptedException
	{
		model.deleteXlsxEmxAllDatatypes(uid, pwd);

		SignUtil.signIn(driver, baseURL, uid, pwd);

		model.open();

		model.uploadXlsxEmxAllDatatypes(EntitiesOptions.ADD, LOG);

		model.uploadXlsxEmxAllDatatypes(EntitiesOptions.ADD_UPDATE, LOG);

		model.uploadXlsxEmxAllDatatypes(EntitiesOptions.UPDATE, LOG);

		SignUtil.signOut(driver);

		model.deleteXlsxEmxAllDatatypes(uid, pwd);
	}

	public void csvZip() throws IOException, InterruptedException
	{
		model.deleteCsvZipEmxAllDatatypes(uid, pwd);

		SignUtil.signIn(driver, baseURL, uid, pwd);

		model.open();

		model.uploadCsvZipEmxAllDatatypes(EntitiesOptions.ADD, LOG);

		model.uploadCsvZipEmxAllDatatypes(EntitiesOptions.ADD_UPDATE, LOG);

		model.uploadCsvZipEmxAllDatatypes(EntitiesOptions.UPDATE, LOG);

		SignUtil.signOut(driver);

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
