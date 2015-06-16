package org.molgenis.selenium.util;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


/**
 * This is a util for the use of the Molgenis Menu
 */
public class MenuUtil
{
	/**
	 * Opens the page by clicking on a menu item
	 * 
	 * @param menuItemText
	 *            String: menu item text
	 * @param driver
	 *            WebDriver
	 * @throws InterruptedException
	 */
	public static void openPageByClickOnMenuItem(String menuItemText, WebDriver driver) throws InterruptedException
	{
		SeleniumUtils.waitForElement(By.linkText(menuItemText), driver);
		driver.findElement(By.linkText(menuItemText)).click();
	}
}

