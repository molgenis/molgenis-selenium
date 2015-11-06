package org.molgenis.selenium.test;

import org.molgenis.DriverType;
import org.molgenis.JenkinsConfig;
import org.molgenis.data.rest.client.MolgenisClient;
import org.molgenis.selenium.model.AnnotatorModel;
import org.molgenis.selenium.model.HomepageModel;
import org.molgenis.selenium.model.UploadAppModel;
import org.molgenis.selenium.util.RestApiV1Util;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
	private HomepageModel homepageModel;
	@Autowired
	private WebDriver driver;
	@Value("${test.uid}")
	private String uid;
	@Value("${test.pwd}")
	private String pwd;

	@BeforeClass
	public void beforeClass() throws InterruptedException
	{
		String token = RestApiV1Util.loginRestApiV1(molgenisClient, uid, pwd, LOG);
		this.model = new AnnotatorModel(driver, molgenisClient, token);
		homepageModel = PageFactory.initElements(driver, HomepageModel.class);
		homepageModel.clickSignIn().signIn(uid, pwd).selectUpload().upload("test-")
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
		homepageModel.signOut();

		// Clear cookies
		this.driver.manage().deleteAllCookies();

		this.driver.close();
	}
}
