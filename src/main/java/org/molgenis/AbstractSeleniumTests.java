package org.molgenis;

import static org.testng.Assert.fail;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;

@ContextConfiguration(classes = JenkinsConfig.class)
public abstract class AbstractSeleniumTests extends AbstractTestNGSpringContextTests implements SeleniumTests
{
	private StringBuffer verificationErrors = new StringBuffer();

	@AfterMethod
	public void clearCookies()
	{
		getDriver().manage().deleteAllCookies();
	}

	@AfterSuite
	public void closeDriverObject()
	{
		getDriver().quit();
		String verificationErrorString = verificationErrors.toString();
		if (!"".equals(verificationErrorString))
		{
			fail(verificationErrorString);
		}
	}
}