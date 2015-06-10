package org.molgenis;

import static org.testng.Assert.fail;

import java.util.function.BooleanSupplier;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;

public class MolegnisSeleniumUtils
{
	public static void waitForElement(By by, WebDriver webDriver) throws InterruptedException
	{
		MolegnisSeleniumUtils.waitFor(() -> MolegnisSeleniumUtils.isElementPresent(by, webDriver));
	}

	public static void waitForElementInvisible(By by, WebDriver webDriver) throws InterruptedException
	{
		MolegnisSeleniumUtils.waitFor(() -> !webDriver.findElement(by).isDisplayed());
	}

	public static void waitFor(BooleanSupplier p) throws InterruptedException
	{
		MolegnisSeleniumUtils.waitFor(p, 60);
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
}
