package org.molgenis.selenium.test;

import org.molgenis.DriverType;
import org.molgenis.JenkinsConfig;
import org.molgenis.data.rest.client.MolgenisClient;
import org.molgenis.selenium.model.AnnotatorModel;
import org.molgenis.selenium.util.RestApiV1Util;
import org.molgenis.selenium.util.SignUtil;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@ContextConfiguration(classes = JenkinsConfig.class)
public class AnnotatorTest extends AbstractTestNGSpringContextTests
{
	private static final Logger LOG = LoggerFactory.getLogger(AnnotatorTest.class);

	private AnnotatorModel model;
	private WebDriver driver;

	@Value("${test.baseurl}")
	private String baseURL;

	@Value("${test.uid}")
	private String uid;

	@Value("${test.pwd}")
	private String pwd;

	@BeforeClass
	public void beforeSuite() throws InterruptedException
	{
		MolgenisClient molgenisClient = RestApiV1Util.createMolgenisClientApiV1(baseURL, LOG);
		driver = DriverType.FIREFOX.getDriver();
		this.model = new AnnotatorModel(driver, molgenisClient, RestApiV1Util.loginRestApiV1(molgenisClient, uid, pwd,
				LOG));
		SignUtil.signIn(driver, baseURL, uid, pwd, LOG);
	}

	@Test
	public void testAnnotateAll() throws Exception
	{
		model.enableAnnotatorsOnDataExplorer();
		model.deleteTestEntity();
		model.uploadDataFile(baseURL);
		model.openDataset(baseURL);
		model.clickAnnotators();
		model.clickHGNC();
		model.clickOMIM();
		model.clickAnnotateButton();
		model.clickShowResults();
		model.checkResults();
	}

	@AfterMethod
	public void clearCookies() throws InterruptedException
	{
		// Clear cookies
		this.driver.manage().deleteAllCookies();

		// Sign out
		SignUtil.signOut(this.driver, LOG);
	}

	@AfterClass
	public void closeDriverObject()
	{
		// Close driver
		driver.close();
	}
}
