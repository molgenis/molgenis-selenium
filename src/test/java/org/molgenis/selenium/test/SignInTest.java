package org.molgenis.selenium.test;

import org.molgenis.DriverType;
import org.molgenis.JenkinsConfig;
import org.molgenis.selenium.model.HomepageModel;
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
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(classes =
{ JenkinsConfig.class, Config.class })
public class SignInTest extends AbstractSeleniumTest
{
	private static final Logger LOG = LoggerFactory.getLogger(AnnotatorTest.class);

	@BeforeMethod
	public void abstractBeforeMethod()
	{
		driver.get(baseURL);
	}

	@AfterMethod
	public void abstractAfterMethod()
	{
		this.driver.manage().deleteAllCookies();
	}

	@Test
	public void testLoginFails() throws InterruptedException
	{
		LOG.info("testLoginFails()...");
		HomepageModel homepageModel = PageFactory.initElements(driver, HomepageModel.class);

		// open the signin
		SignInModel signinModel = homepageModel.openSignInDialog().signInFails(uid, "blaat");

		// should show error messages
		Assert.assertTrue(signinModel.showsErrorText("The username or password you entered is incorrect"));

		// should show sign in button again!
		// See https://github.com/molgenis/molgenis/issues/4123, the close button is missing!
		// Assert.assertTrue(signinModel.close().isSignedOut());
	}

	@Test
	public void testLoginLogout() throws InterruptedException
	{
		LOG.info("testLoginLogout()...");
		HomepageModel homepageModel = PageFactory.initElements(driver, HomepageModel.class);

		// should show sign out button
		Assert.assertTrue(homepageModel.openSignInDialog().signIn(uid, pwd).isLoggedIn());

		// should show sign in button again
		Assert.assertTrue(homepageModel.signOut().isSignedOut());
	}
}
