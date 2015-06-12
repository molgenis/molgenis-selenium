package org.molgenis.selenium.model;

import org.molgenis.selenium.util.MenuUtil;
import org.molgenis.selenium.util.Select2Util;
import org.molgenis.selenium.util.SeleniumUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
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
	public static String ENTITY_TITLE_ID = "entity-class-name";
	public static String MENU_ITEM_TEXT = "Data Explorer";

	public DataExplorerAppModel(WebDriver driver)
	{
		this.driver = driver;
	}

	public void openDataExplorerPageByClickOnMenu() throws InterruptedException
	{
		MenuUtil.openPageByClickOnMenuItem(MENU_ITEM_TEXT, driver);
	}

	public void selectEntityFromUrl(String fullNameEntity, String baseUrl)
	{
		driver.get(baseUrl + "/menu/main/dataexplorer?entity=org_molgenis_test_TypeTest");
	}

	public void selectEntityFromSelect2PullDown(String entityLabel)
			throws InterruptedException
	{
		Select2Util.select("dataset-select-container", entityLabel, driver, LOG);
	}

	public String getSelectedEntityTitle() throws InterruptedException
	{
		SeleniumUtils.waitForElement(By.id(ENTITY_TITLE_ID), driver);
		String datasetTitle = driver.findElement(By.id(ENTITY_TITLE_ID)).getText();
		LOG.info("Entity title: " + datasetTitle);
		return datasetTitle;
	}

	/**
	 * @return the driver
	 */
	public WebDriver getDriver()
	{
		return driver;
	}
}
