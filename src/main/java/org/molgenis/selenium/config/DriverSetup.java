package org.molgenis.selenium.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

public interface DriverSetup
{
	WebDriver getWebDriverInstance(DesiredCapabilities desiredCapabilities);
	DesiredCapabilities getDesiredCapabilities();
}