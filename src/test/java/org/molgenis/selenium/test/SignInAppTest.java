package org.molgenis.selenium.test;

import org.molgenis.DriverType;
import org.molgenis.JenkinsConfig;
import org.molgenis.selenium.model.SignInModel;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@ContextConfiguration(classes = JenkinsConfig.class)
public class SignInAppTest extends AbstractTestNGSpringContextTests
{
	private static final Logger LOG = LoggerFactory.getLogger(AnnotatorTest.class);
	private WebDriver driver;
	private SignInModel model;

	@Value("${test.baseurl}")
	private String baseURL;

	@Value("${test.uid}")
	private String uid;

	@Value("${test.pwd}")
	private String pwd;

	@BeforeClass
	public void beforeSuite() throws InterruptedException
	{
		driver = DriverType.FIREFOX.getDriver();
		driver.get(baseURL);
		model = PageFactory.initElements(driver, SignInModel.class);
	}

	@Test
	public void testLoginLogout() throws InterruptedException
	{
		// open the signin
		model.open();
		// should result in a popup where we type username and password

		model.signIn(uid, "blaat");

		// should show error messages
		Assert.assertTrue(model.shows("The username or password you entered is incorrect"));

		model.signIn(uid, pwd);

		// should show sign out button
		Assert.assertTrue(model.shows("Sign out"));

		model.signOut();

		// should show sign in button again
		Assert.assertTrue(model.shows("Sign in"));
	}

	@AfterClass
	public void afterClass() throws InterruptedException
	{
		// Clear cookies
		this.driver.manage().deleteAllCookies();

		// Close driver
		this.driver.close();
	}
}
