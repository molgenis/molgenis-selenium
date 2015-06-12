package org.molgenis.selenium.util;

import static org.testng.Assert.fail;

import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;

public class SeleniumUtils
{
	public static void waitForElement(By by, WebDriver webDriver) throws InterruptedException
	{
		SeleniumUtils.waitFor(() -> SeleniumUtils.isElementPresent(by, webDriver));
	}

	public static void waitForElementInvisible(By by, WebDriver webDriver) throws InterruptedException
	{
		SeleniumUtils.waitFor(() -> !webDriver.findElement(by).isDisplayed());
	}

	public static void waitFor(BooleanSupplier p) throws InterruptedException
	{
		SeleniumUtils.waitFor(p, 60);
	}

	public static void waitFor(BooleanSupplier p, int timeout) throws InterruptedException
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

	public static boolean isElementPresent(By by, WebDriver webDriver)
	{
		try
		{
			webDriver.findElement(by);
			return true;
		}
		catch (NoSuchElementException e)
		{
			return false;
		}
	}

	public static void setPageTimeout(WebDriver webDriver, long seconds)
	{
		// Set loading pages timeout
		webDriver.manage().timeouts().pageLoadTimeout(seconds, TimeUnit.SECONDS);
	}

	public static void setScriptTimeout(WebDriver webDriver, long seconds)
	{
		// Set asynchronous scripts timeout
		webDriver.manage().timeouts().setScriptTimeout(seconds, TimeUnit.SECONDS);
	}

	public static void setImplicitlyWaitTimeout(WebDriver webDriver, long seconds)
	{
		// Set asynchronous scripts timeout
		webDriver.manage().timeouts().implicitlyWait(seconds, TimeUnit.SECONDS);
	}
}
