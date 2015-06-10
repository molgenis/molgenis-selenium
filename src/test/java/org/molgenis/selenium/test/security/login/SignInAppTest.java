package org.molgenis.selenium.test.security.login;

import org.molgenis.AbstractSeleniumTests;
import org.molgenis.DriverType;
import org.molgenis.selenium.model.security.login.SignInAppModel;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SignInAppTest extends AbstractSeleniumTests
{
	private WebDriver driver = DriverType.FIREFOX.getDriver();

	@Test
	public void test1()
	{
		driver.get("http://localhost:8080");
		SignInAppModel signin = new SignInAppModel(driver);

		// open the signin
		signin.open();
		// should result in a popup where we type username and password

		signin.signIn("admin", "blaat");

		// should show error messages
		Assert.assertTrue(signin.shows("The username or password you entered is incorrect"));

		signin.signIn("admin", "admin");

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
