package org.molgenis;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;

@ContextConfiguration(classes = JenkinsConfig.class)
public abstract class AbstractSeleniumTests extends AbstractTestNGSpringContextTests implements SeleniumTests
{
	@AfterMethod
	public void clearCookies()
	{
		getDriver().manage().deleteAllCookies();
	}

	@AfterClass
	public void closeDriverObject()
	{
		getDriver().close();
	}
}