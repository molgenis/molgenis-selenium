package org.molgenis.selenium.util;

import org.molgenis.selenium.model.SignInAppModel;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a model of the MOLGENIS login user interface
 */
public class SignUtil
{
	private static final Logger LOG = LoggerFactory.getLogger(SignUtil.class);

	public static void signIn(WebDriver driver, String baseURL, String uid, String pwd)
			throws InterruptedException
	{
		LOG.info("Sign in");
		driver.get(baseURL + "/");
		SignInAppModel signin = new SignInAppModel(driver);
		signin.open();
		signin.signIn(uid, pwd);
	}

	public static void signOut(WebDriver driver) throws InterruptedException
	{
		LOG.info("Sign out");
		SignInAppModel signin = new SignInAppModel(driver);
		signin.signOut();
	}
}

