package org.molgenis.selenium.model;

import java.util.List;

import org.molgenis.selenium.model.component.Select2Model;
import org.molgenis.selenium.util.MenuModel;
import org.molgenis.selenium.util.SeleniumUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a model of the MOLGENIS Data Explorer user interface
 */
public class DataExplorerAppModel
{
	public static String MENU_ITEM_TEXT = "Data Explorer";
	private static final Logger LOG = LoggerFactory.getLogger(DataExplorerAppModel.class);

	public static enum DeleteOption
	{
		DATA,
		DATA_AND_METADATA;
	}
	
	private final Select2Model entityModel;
	private final WebDriver driver;

	public DataExplorerAppModel(WebDriver driver)
	{
		this.driver = driver;
		entityModel = new Select2Model(driver, "dataset-select");
	}

	public void open() throws InterruptedException
	{
		MenuModel.openPageByClickOnMenuItem(MENU_ITEM_TEXT, driver);
	}

	public void selectEntityFromUrl(String baseUrl, String entityFullName)
	{
		driver.get(baseUrl + "/menu/main/dataexplorer?entity=" + entityFullName);
	}

	public static void deleteEntity(WebDriver driver, String baseURL, String entityFullName, DeleteOption deleteOption)
			throws InterruptedException
	{
		Thread.sleep(3000);
		driver.get(baseURL + "/menu/main/dataexplorer?entity=" + entityFullName);
		
		By deleteButtonSelector = By.id("dropdownMenu1");
		SeleniumUtils.waitForElement(deleteButtonSelector, driver);
		WebElement deleteButton = driver.findElement(deleteButtonSelector);
		deleteButton.click();
		
		switch (deleteOption)
		{
			case DATA:
				By deleteDataBtnOptionsSelector = By.id("delete-data-btn");
				SeleniumUtils.waitForElement(deleteDataBtnOptionsSelector, driver);
				WebElement deleteDataBtnOption = driver.findElement(deleteDataBtnOptionsSelector);
				deleteDataBtnOption.click();
				break;
			case DATA_AND_METADATA:
				By deleteDataMetadataBtnOptionsSelector = By.id("delete-data-metadata-btn");
				SeleniumUtils.waitForElement(deleteDataMetadataBtnOptionsSelector, driver);
				WebElement deleteDataMetadataBtnOption = driver.findElement(deleteDataMetadataBtnOptionsSelector);
				deleteDataMetadataBtnOption.click();
				break;
			default:
				break;
		}

		By apply = By.cssSelector("[data-bb-handler=confirm]");
		SeleniumUtils.waitForElement(apply, driver);
		WebElement applyButton = driver.findElement(apply);
		applyButton.click();
	}

	public static WebElement getDeleteEntityButton(WebDriver driver) throws InterruptedException
	{
		By selector = By.id("dropdownMenu1");
		SeleniumUtils.waitForElement(selector, driver);
		return driver.findElement(selector);
	}

	public void selectEntity(String entityLabel)
			throws InterruptedException
	{
		entityModel.select(entityLabel);
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

	public void deleteEntityByFullName(WebDriver driver, String baseURL, String fullName, Logger logger)
	{
		try
		{
			DataExplorerAppModel.deleteEntity(driver, baseURL, fullName, DeleteOption.DATA_AND_METADATA);
		}
		catch (InterruptedException e)
		{
			logger.info("deleting " + fullName + " data and metadata failed");
		}
	}

	public void deleteEntitiesByFullName(WebDriver driver, String baseURL, List<String> fullNames, Logger logger)
			throws InterruptedException
	{
		fullNames.stream().forEachOrdered(e -> this.deleteEntityByFullName(driver, baseURL, e, logger));
	}
}
