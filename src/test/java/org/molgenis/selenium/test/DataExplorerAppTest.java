package org.molgenis.selenium.test;

import junit.framework.Assert;

import org.molgenis.DriverType;
import org.molgenis.JenkinsConfig;
import org.molgenis.selenium.model.DataExplorerAppModel;
import org.molgenis.selenium.util.SignInUtil;
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
public class DataExplorerAppTest extends AbstractTestNGSpringContextTests
{
	private static final Logger LOG = LoggerFactory.getLogger(DataExplorerAppTest.class);
	private DataExplorerAppModel model;
	private WebDriver driver;

	@Value("${test.baseurl}")
	private String baseUrl;

	@Value("${test.uid}")
	private String uid;

	@Value("${test.pwd}")
	private String pwd;

	@BeforeClass
	public void beforeClass()
	{
		this.driver = DriverType.FIREFOX.getDriver();
		this.model = new DataExplorerAppModel(this.driver);
	}

	@BeforeMethod
	public void beforeMethod() throws InterruptedException
	{
		SignInUtil.signIn(this.driver, baseUrl, uid, pwd, LOG);
	}

	@Test
	public void test1() throws InterruptedException
	{
		this.driver.get(baseUrl);
		model.openDataExplorerPageByClickOnMenu();
		model.selectEntityFromSelect2PullDown("TypeTest");
		Assert.assertEquals(model.getSelectedEntityTitle(), "TypeTest");
	}

	@Test
	public void test2_openViaUrl() throws InterruptedException
	{
		model.selectEntityFromUrl("org_molgenis_test_TypeTest", baseUrl);
		Assert.assertEquals("TypeTest", model.getSelectedEntityTitle());
	}

	@AfterMethod
	public void clearCookies() throws InterruptedException
	{
		// Clear cookies
		this.driver.manage().deleteAllCookies();

		// Sign out
		SignInUtil.signOut(this.driver, LOG);
	}

	@AfterClass
	public void closeDriverObject()
	{
		// Close driver
		driver.close();
	}
}