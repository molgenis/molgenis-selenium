package org.molgenis.selenium.util;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * This is a util for the use of the Molgenis Menu
 */
public class MenuModel
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

	public static void openPageByClickOnSubMenuItem(String menuItemText, String subMenuItemText, WebDriver driver)
			throws InterruptedException
	{
		SeleniumUtils.waitForElement(By.linkText(menuItemText), driver);
		driver.findElement(By.linkText(menuItemText)).click();

		SeleniumUtils.waitForElement(By.linkText(subMenuItemText), driver);
		driver.findElement(By.linkText(subMenuItemText)).click();
	}
}
