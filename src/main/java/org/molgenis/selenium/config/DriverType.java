package org.molgenis.selenium.config;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

public enum DriverType implements DriverSetup
{

	FIREFOX
	{
		@Override
		public DesiredCapabilities getDesiredCapabilities()
		{
			return DesiredCapabilities.firefox();
		}

		@Override
		public WebDriver getWebDriverInstance(DesiredCapabilities capabilities)
		{
			FirefoxDriver driver = new FirefoxDriver(capabilities);
			driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
			return driver;
		}
	};

	public static final DriverType DEFAULT_DRIVERTYPE = FIREFOX;

	public WebDriver getDriver()
	{
		WebDriver driver = getWebDriverInstance(getDesiredCapabilities());
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		return driver;
	}
}