package org.molgenis.selenium.model;

import org.molgenis.selenium.util.MenuUtil;
import org.molgenis.selenium.util.Select2Util;
import org.molgenis.selenium.util.SeleniumUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a model of the MOLGENIS login user interface
 */
public class DataExplorerAppModel
{
	private static final Logger LOG = LoggerFactory.getLogger(DataExplorerAppModel.class);
	private final WebDriver driver;

	public static String ENTITY_SELECT_ID = "dataset-select";
	public static String MENU_ITEM_TEXT = "Data Explorer";

	public DataExplorerAppModel(WebDriver driver)
	{
		this.driver = driver;
	}

	public void openDataExplorerPlugin() throws InterruptedException
	{
		MenuUtil.openPageByClickOnMenuItem(MENU_ITEM_TEXT, driver);
	}

	public void selectEntityFromUrl(String fullNameEntity, String baseUrl)
	{
		driver.get(baseUrl + "/menu/main/dataexplorer?entity=org_molgenis_test_TypeTest");
	}

	public void selectEntity(String entityLabel)
			throws InterruptedException
	{
		Select2Util.select("dataset-select-container", entityLabel, driver, LOG);
	}

	public String getSelectedEntityTitle() throws InterruptedException
	{
		By selector = By.id("entity-class-name");
		SeleniumUtils.waitForElement(selector, driver);
		return driver.findElement(selector).getText();
	}

	public WebElement getPrevious() throws InterruptedException
	{
		By selector = By.cssSelector(".page-prev");
		SeleniumUtils.waitForElement(selector, driver);
		return driver.findElement(selector);
	}

	public WebElement getNext() throws InterruptedException
	{
		By selector = By.cssSelector(".page-next");
		SeleniumUtils.waitForElement(selector, driver);
		return driver.findElement(selector);
	}
}
