package org.molgenis.selenium.test;

import org.molgenis.AbstractSeleniumTests;
import org.molgenis.DriverType;
import org.molgenis.selenium.model.SignInAppModel;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SignInAppTest extends AbstractSeleniumTests
{
	private WebDriver driver = DriverType.FIREFOX.getDriver();

	@Value("${test.baseurl}")
	private String baseUrl;

	@Value("${test.uid}")
	private String uid;

	@Value("${test.pwd}")
	private String pwd;

	@Test
	public void test1()
	{
		driver.get(baseUrl);
		SignInAppModel signin = new SignInAppModel(driver);

		// open the signin
		signin.open();
		// should result in a popup where we type username and password

		signin.signIn(uid, "blaat");

		// should show error messages
		Assert.assertTrue(signin.shows("The username or password you entered is incorrect"));

		signin.signIn(uid, pwd);

		// should show sign out button
		Assert.assertTrue(signin.shows("Sign out"));

		signin.signOut();

		// should show sign in button again
		Assert.assertTrue(signin.shows("Sign in"));
	}

	@Override
	public WebDriver getDriver()
	{
		return this.driver;
	}
}
