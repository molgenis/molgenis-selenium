package org.molgenis.selenium.util;

import static org.testng.Assert.fail;

import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

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

	public static void waitFor(BooleanSupplier p)
	{
		SeleniumUtils.waitFor(p, 60);
	}

	public static void waitFor(BooleanSupplier p, int timeout)
	{
		for (int second = 0;; second++)
		{
			if (second >= timeout) fail("timeout");
			try
			{
				if (p.getAsBoolean()) break;
				Thread.sleep(1000);
			}
			catch (Exception e)
			{
			}
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

	public static boolean isElementEnabled(By by, WebDriver webDriver)
	{
		try
		{
			WebElement webElement = webDriver.findElement(by.cssSelector("not:([disabled])"));
			return webElement.isEnabled();
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
