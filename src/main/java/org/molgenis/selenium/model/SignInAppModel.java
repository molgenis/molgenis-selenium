package org.molgenis.selenium.model;

import org.molgenis.selenium.util.SeleniumUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * This is a model of the MOLGENIS login user interface
 */
public class SignInAppModel
{
	private WebDriver webDriver;

	// Elements references
	public static String OPEN_BUTTON_ID = "open-button";
	public static String USERNAME_FIELD_ID = "username-field";
	public static String PASSWORD_FIELD_ID = "password-field";
	public static String SIGNIN_BUTTON_ID = "signin-button";
	public static String SIGNOUT_BUTTON_ID = "signout-button";

	public SignInAppModel(WebDriver webDriver)
	{
		this.webDriver = webDriver;
	}

	public void open() throws InterruptedException
	{
		// click the sign in button on home page
		SeleniumUtils.waitForElement(By.id(OPEN_BUTTON_ID), webDriver);
		webDriver.findElement(By.id(OPEN_BUTTON_ID)).click();
	}

	public void signIn(String user, String password)
	{
		// fill in the user name
		webDriver.findElement(By.id(USERNAME_FIELD_ID)).clear();
		webDriver.findElement(By.id(USERNAME_FIELD_ID)).sendKeys(user);

		// fill in the password
		webDriver.findElement(By.id(PASSWORD_FIELD_ID)).clear();
		webDriver.findElement(By.id(PASSWORD_FIELD_ID)).sendKeys(password);

		// click the sign in button on login page
		webDriver.findElement(By.id(SIGNIN_BUTTON_ID)).click();
	}

	public boolean shows(String s)
	{
		return webDriver.getPageSource().contains(s);
	}

	public void signOut() throws InterruptedException
	{
		SeleniumUtils.waitForElement(By.id(SIGNOUT_BUTTON_ID), webDriver);
		webDriver.findElement(By.id(SIGNOUT_BUTTON_ID)).click();
	}
}

