package org.molgenis.selenium.model;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a model of the MOLGENIS login user interface
 */
public class SignInAppModel
{
	private static final Logger LOG = LoggerFactory.getLogger(SignInAppModel.class);
	private WebDriver driver;

	// IDs of user interface elements
	// TODO: ease testing by giving all relevant elements an ID value
	public static String OPEN_BUTTON = "open-button";
	public static String USERNAME_FIELD = "username-field";
	public static String PASSWORD_FIELD = "password-field";
	public static String SIGNIN_BUTTON = "signin-button";
	public static String SIGNOUT_BUTTON = "signout-button";

	public SignInAppModel(WebDriver driver)
	{
		this.driver = driver;
	}

	public void open()
	{
		// click the sign in button on home page
		driver.findElement(By.id(OPEN_BUTTON)).click();
	}

	public void signIn(String user, String password)
	{
		// fill in the user name
		driver.findElement(By.id(USERNAME_FIELD)).clear();
		driver.findElement(By.id(USERNAME_FIELD)).sendKeys(user);

		// fill in the password
		driver.findElement(By.id(PASSWORD_FIELD)).clear();
		driver.findElement(By.id(PASSWORD_FIELD)).sendKeys(password);

		// click the sign in button on login page
		driver.findElement(By.id(SIGNIN_BUTTON)).click();
	}

	public boolean shows(String s)
	{
		return driver.getPageSource().contains(s);
	}

	public void signOut()
	{
		driver.findElement(By.id(SIGNOUT_BUTTON)).click();
	}

	public static void login(WebDriver driver, String baseURL, String uid, String pwd)
	{
		LOG.info("login");
		driver.get(baseURL + "/");
		SignInAppModel signin = new SignInAppModel(driver);
		signin.open();
		signin.signIn(uid, pwd);
	}
}

