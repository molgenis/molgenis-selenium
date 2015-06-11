package org.molgenis;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

public interface DriverSetup
{
	WebDriver getWebDriverInstance(DesiredCapabilities desiredCapabilities);
	DesiredCapabilities getDesiredCapabilities();
}