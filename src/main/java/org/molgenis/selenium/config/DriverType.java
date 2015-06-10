package org.molgenis.selenium.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;

public enum DriverType implements DriverSetup
{

	FIREFOX
	{
		public DesiredCapabilities getDesiredCapabilities()
		{
			return DesiredCapabilities.firefox();
		}

		public WebDriver getWebDriverInstance(DesiredCapabilities capabilities)
		{
			FirefoxDriver driver = new FirefoxDriver(capabilities);
			driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
			return driver;
		}
	},
	CHROME
	{
		public DesiredCapabilities getDesiredCapabilities()
		{
			DesiredCapabilities capabilities = DesiredCapabilities.chrome();
			capabilities.setCapability("chrome.switches", Arrays.asList("--no-default-browser-check"));
			HashMap<String, String> chromePreferences = new HashMap<String, String>();
			chromePreferences.put("profile.password_manager_enabled", "false");
			capabilities.setCapability("chrome.prefs", chromePreferences);
			return capabilities;
		}

		public WebDriver getWebDriverInstance(DesiredCapabilities capabilities)
		{
			return new ChromeDriver(capabilities);
		}
	},
	SAFARI
	{
		public DesiredCapabilities getDesiredCapabilities()
		{
			DesiredCapabilities capabilities = DesiredCapabilities.safari();
			capabilities.setCapability("safari.cleanSession", true);
			return capabilities;
		}

		public WebDriver getWebDriverInstance(DesiredCapabilities capabilities)
		{
			return new SafariDriver(getDesiredCapabilities());
		}
	};

	public static final DriverType DEFAULT_DRIVERTYPE = FIREFOX;

	public WebDriver getDriver()
	{
		WebDriver driver = getWebDriverInstance(this.getDesiredCapabilities());
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		return driver;
	}
}