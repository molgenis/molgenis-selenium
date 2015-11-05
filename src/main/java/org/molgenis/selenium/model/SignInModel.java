package org.molgenis.selenium.model;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 * This is a model of the MOLGENIS login user interface.
 * To create it, use the {@link PageFactory#initElements(WebDriver, Class) method.}
 */
public class SignInModel
{
	@FindBy(id = "open-button")
	WebElement openButton;

	@FindBy(id = "username-field")
	WebElement usernameField;

	@FindBy(id = "password-field")
	WebElement passwordField;

	@FindBy(id = "signin-button")
	WebElement signinButton;

	@FindBy(id = "signout-button")
	WebElement signoutButton;

	private WebDriver webDriver;

	public SignInModel(WebDriver webDriver)
	{
		this.webDriver = webDriver;
	}

	public void open() throws InterruptedException
	{
		// click the sign in button on home page
		openButton.click();
	}

	public void signIn(String user, String password)
	{
		// fill in the user name
		usernameField.clear();
		usernameField.sendKeys(user);

		// fill in the password
		passwordField.clear();
		passwordField.sendKeys(password);

		// click the sign in button on login page
		signinButton.click();
	}

	public boolean shows(String s)
	{
		return webDriver.getPageSource().contains(s);
	}

	public void signOut() throws InterruptedException
	{
		signoutButton.click();
	}
}
