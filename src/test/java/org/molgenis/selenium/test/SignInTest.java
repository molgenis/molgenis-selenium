package org.molgenis.selenium.test;

import org.molgenis.JenkinsConfig;
import org.molgenis.selenium.model.HomepageModel;
import org.molgenis.selenium.model.SignInModel;
import org.molgenis.selenium.test.dataexplorer.annotators.AnnotatorTest;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
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
		LOG.info("Test that login with invalid credentials fails...");
		HomepageModel homepageModel = PageFactory.initElements(driver, HomepageModel.class);

		// open the signin
		SignInModel signinModel = homepageModel.menu().openSignInDialog().signInFails(uid, "blaat");

		// should show error messages
		Assert.assertTrue(signinModel.showsErrorText("The username or password you entered is incorrect"));

		// should show sign in button again!
		// See https://github.com/molgenis/molgenis/issues/4123, the close button is missing!
		// Assert.assertTrue(signinModel.close().isSignedOut());
	}

	@Test
	public void testLoginLogout() throws InterruptedException
	{
		LOG.info("Test login and logout...");
		HomepageModel homepageModel = PageFactory.initElements(driver, HomepageModel.class);

		// should show sign out button
		Assert.assertTrue(homepageModel.menu().openSignInDialog().signIn(uid, pwd).menu().isLoggedIn());

		// should show sign in button again
		Assert.assertTrue(homepageModel.menu().signOut().menu().isSignedOut());
	}
}
