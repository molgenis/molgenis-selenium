package org.molgenis.selenium.config;

import static org.testng.Assert.fail;

import java.util.function.BooleanSupplier;

import org.molgenis.JenkinsConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
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

	protected void waitForElement(By by) throws InterruptedException
	{
		waitFor(() -> isElementPresent(by));
	}

	protected void waitForElementInvisible(By by) throws InterruptedException
	{
		waitFor(() -> !getDriver().findElement(by).isDisplayed());
	}

	protected static void waitFor(BooleanSupplier p) throws InterruptedException
	{
		waitFor(p, 60);
	}

	protected static void waitFor(BooleanSupplier p, int timeout) throws InterruptedException
	{
		for (int second = 0;; second++)
		{
			if (second >= timeout) fail("timeout");
			try
			{
				if (p.getAsBoolean()) break;
			}
			catch (Exception e)
			{
			}
			Thread.sleep(1000);
		}
	}

	protected boolean isElementPresent(By by)
	{
		try
		{
			getDriver().findElement(by);
			return true;
		}
		catch (NoSuchElementException e)
		{
			return false;
		}
	}
}