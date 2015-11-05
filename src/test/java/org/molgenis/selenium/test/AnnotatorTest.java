package org.molgenis.selenium.test;

import org.molgenis.DriverType;
import org.molgenis.JenkinsConfig;
import org.molgenis.data.rest.client.MolgenisClient;
import org.molgenis.selenium.model.AnnotatorModel;
import org.molgenis.selenium.model.SignInModel;
import org.molgenis.selenium.util.RestApiV1Util;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@ContextConfiguration(classes = JenkinsConfig.class)
public class AnnotatorTest extends AbstractTestNGSpringContextTests
{
	private static final Logger LOG = LoggerFactory.getLogger(AnnotatorTest.class);

	private AnnotatorModel model;
	private SignInModel signInModel;
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
		driver.get(baseURL + "/");
		this.model = new AnnotatorModel(driver, molgenisClient, RestApiV1Util.loginRestApiV1(molgenisClient, uid, pwd,
				LOG));
		signInModel = PageFactory.initElements(driver, SignInModel.class);
		signInModel.open();
		signInModel.signIn(uid, pwd);
	}

	@Test
	public void testAnnotateAll() throws Exception
	{
		model.enableAnnotatorsOnDataExplorer();
		model.deleteTestEntity();
		model.uploadDataFile(baseURL);
		model.openDataset(baseURL);
		model.clickAnnotators();
		model.clickSnpEff();
		model.clickCADD();
		model.clickAnnotateButton();
		model.clickShowResults();
		model.checkResults();
	}

	@AfterClass
	public void afterClass() throws InterruptedException
	{
		// Sign out
		signInModel.signOut();

		// Clear cookies
		this.driver.manage().deleteAllCookies();

		// Clear cookies
		this.driver.close();
	}
}
