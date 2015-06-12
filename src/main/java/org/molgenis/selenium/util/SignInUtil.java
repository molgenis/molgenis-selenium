package org.molgenis.selenium.util;

import org.molgenis.selenium.model.SignInAppModel;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;

/**
 * This is a model of the MOLGENIS login user interface
 */
public class SignInUtil
{
	public static void signIn(WebDriver driver, String baseURL, String uid, String pwd, Logger logger)
			throws InterruptedException
	{
		logger.info("SignInUtil -- login");
		driver.get(baseURL + "/");
		SignInAppModel signin = new SignInAppModel(driver);
		signin.open();
		signin.signIn(uid, pwd);
	}

	public static void signOut(WebDriver driver, Logger logger) throws InterruptedException
	{
		logger.info("SignInUtil -- logout");
		SignInAppModel signin = new SignInAppModel(driver);
		signin.signOut();
	}
}

