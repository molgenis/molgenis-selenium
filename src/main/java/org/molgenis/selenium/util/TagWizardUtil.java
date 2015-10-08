package org.molgenis.selenium.util;

import org.molgenis.selenium.model.MappingServiceAppModel;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TagWizardUtil
{
	private static final String MAIN_MENU = "Data Integration";
	private static final String SUB_MENU = "Tag Wizard";
	private static final String TARGET_ENTITY_SELECT2_ELEMENT_ID = "s2id_select-target";

	private static final int BUTTON_CLICK_SLEEP_TIME = 3000;
	public static final Logger LOG = LoggerFactory.getLogger(TagWizardUtil.class);

	public static void openTagWizard(WebDriver driver) throws InterruptedException
	{
		MenuUtil.openPageByClickOnSubMenuItem(MAIN_MENU, SUB_MENU, driver);
	}

	public static void selectEntityName(WebDriver driver) throws InterruptedException
	{
		Select2Util.select(TARGET_ENTITY_SELECT2_ELEMENT_ID, MappingServiceAppModel.TARGET_ENTITY_NAME, driver, LOG);
		Thread.sleep(BUTTON_CLICK_SLEEP_TIME);
	}

	public static void clickOnCellByRowIndex(int i, WebDriver driver) throws InterruptedException
	{
		WebElement editTagButton = driver.findElement(By
				.xpath("//table[@id='tag-mapping-table']/tbody/tr[9]/td[3]/button"));
		editTagButton.click();
		Thread.sleep(BUTTON_CLICK_SLEEP_TIME);
	}

	public static void sendTextToMultiSelectionElement(String inputFieldId, String label, WebDriver driver)
			throws InterruptedException
	{
		WebElement select2InputText = driver
				.findElement(By
						.xpath("//input[@id='tag-dropdown']/../div[contains(@class, 'select2-container')]//input[@type='text']"));
		select2InputText.sendKeys(label);
		select2InputText.sendKeys(Keys.ENTER);
		Thread.sleep(BUTTON_CLICK_SLEEP_TIME);
	}

	public static void saveSearchedTags(WebDriver driver) throws InterruptedException
	{
		WebElement saveTagButton = driver.findElement(By.id("save-tag-selection-btn"));
		saveTagButton.click();
		Thread.sleep(BUTTON_CLICK_SLEEP_TIME);
	}
}
