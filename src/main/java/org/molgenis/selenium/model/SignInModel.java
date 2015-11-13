package org.molgenis.selenium.model;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 * This is a model of the MOLGENIS login user interface. To create it, use the
 * {@link PageFactory#initElements(WebDriver, Class) method.}
 */
public class SignInModel
{
	@FindBy(id = "username-field")
	WebElement usernameField;

	@FindBy(id = "password-field")
	WebElement passwordField;

	@FindBy(id = "signin-button")
	WebElement signinButton;

	@FindBy(css = "p.text-error")
	WebElement errorText;

	@FindBy(css = "button.close")
	WebElement closeButton;

	private WebDriver driver;

	public SignInModel(WebDriver driver)
	{
		this.driver = driver;
	}

	public HomepageModel signIn(String user, String password)
	{
		trySignIn(user, password);
		return PageFactory.initElements(driver, HomepageModel.class);
	}

	public SignInModel signInFails(String user, String password)
	{
		trySignIn(user, password);
		return this;
	}

	public HomepageModel close()
	{
		closeButton.click();
		return PageFactory.initElements(driver, HomepageModel.class);
	}

	private void trySignIn(String user, String password)
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

	public boolean showsErrorText(String s)
	{
		return errorText.getText().contains(s);
	}
}
