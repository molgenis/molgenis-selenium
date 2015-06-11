package org.molgenis.selenium.util;

import org.molgenis.selenium.model.SignInAppModel;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;

/**
 * This is a model of the MOLGENIS login user interface
 */
public class SignInUtil
{
	public static void login(WebDriver driver, String baseURL, String uid, String pwd, Logger logger)
	{
		logger.info("SignInUtil -- login");
		driver.get(baseURL + "/");
		SignInAppModel signin = new SignInAppModel(driver);
		signin.open();
		signin.signIn(uid, pwd);
	}
}

