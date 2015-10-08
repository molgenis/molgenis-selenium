package org.molgenis.selenium.util;

import static org.molgenis.selenium.model.MappingServiceAppModel.MAPPING_SERVICE_BASE_URL;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class MappingServiceUtil
{
	private static final String MAIN_MENU = "Data Integration";
	private static final String SUB_MENU = "Mapping Service";
	private static final int BUTTON_CLICK_SLEEP_TIME = 3000;

	// ##################################### Mapping Project Overview related event handlers
	public static void openMappingService(WebDriver driver) throws InterruptedException
	{
		MenuUtil.openPageByClickOnSubMenuItem(MAIN_MENU, SUB_MENU, driver);
	}

	public static WebElement clickButonById(String buttonId, WebDriver driver) throws InterruptedException
	{
		WebElement buttonElement = driver.findElement(By.id(buttonId));
		buttonElement.click();
		Thread.sleep(BUTTON_CLICK_SLEEP_TIME);
		return buttonElement;
	}

	public static WebElement clickButonByCssSelector(String tagName, String attributeName, String Value,
			WebDriver driver) throws InterruptedException
	{
		WebElement buttonElement = driver.findElement(By.cssSelector(createCssSelector(tagName, attributeName, Value)));
		buttonElement.click();
		Thread.sleep(BUTTON_CLICK_SLEEP_TIME);
		return buttonElement;
	}

	public static WebElement clickCancelButonInAddNewMappingProjectModal(WebDriver driver) throws InterruptedException
	{
		WebElement buttonElement = driver.findElement(By
				.xpath("//div[@id='create-new-mapping-project-modal']//button[contains(text(), 'Cancel')]"));
		buttonElement.click();
		Thread.sleep(BUTTON_CLICK_SLEEP_TIME);
		return buttonElement;
	}

	public static WebElement clickOKButonByXpathExpression(WebDriver driver) throws InterruptedException
	{
		WebElement confirmButton = driver.findElement(By.xpath("//button[contains(text(), 'OK')]"));
		confirmButton.click();
		Thread.sleep(BUTTON_CLICK_SLEEP_TIME);
		return confirmButton;
	}

	public static WebElement clickButonWithInSpecifiedElementByClassName(WebElement webElement, String className,
			WebDriver driver) throws InterruptedException
	{
		WebElement buttonElement = webElement.findElement(By.className("btn-danger"));
		buttonElement.click();
		Thread.sleep(BUTTON_CLICK_SLEEP_TIME);
		return buttonElement;
	}

	public static WebElement clickButonWithInSpecifiedElementByTagName(WebElement webElement, String tagName,
			WebDriver driver) throws InterruptedException
	{
		WebElement buttonElement = webElement.findElement(By.tagName(tagName));
		buttonElement.click();
		Thread.sleep(BUTTON_CLICK_SLEEP_TIME);
		return buttonElement;
	}

	public static WebElement clickGoBackButton(WebDriver driver) throws InterruptedException
	{
		WebElement goBackToProjectOverviewButton = driver.findElement(By
				.xpath("//a[contains(@class, 'btn-default') and contains(@href, '" + MAPPING_SERVICE_BASE_URL + "')]"));
		goBackToProjectOverviewButton.click();
		Thread.sleep(BUTTON_CLICK_SLEEP_TIME);
		return goBackToProjectOverviewButton;
	}

	public static WebElement clickCancelButtonForRemoveSourceColumms(WebDriver driver) throws InterruptedException
	{
		WebElement cancelButton = driver.findElement(By
				.xpath("//button[contains(text(), 'Cancel') and contains(@data-bb-handler, 'cancel')]"));
		cancelButton.click();
		Thread.sleep(BUTTON_CLICK_SLEEP_TIME);
		return cancelButton;
	}

	public static WebElement clickRemoveButtonForRemoveSourceColumns(WebElement webElement, WebDriver driver)
			throws InterruptedException
	{
		WebElement removeSourceColumnButton = webElement.findElement(By.tagName("button"));
		removeSourceColumnButton.click();
		Thread.sleep(BUTTON_CLICK_SLEEP_TIME);
		return removeSourceColumnButton;
	}

	public static WebElement setValueToTextFieldByName(String textFieldName, String value, WebDriver driver)
	{
		WebElement textFieldElement = driver.findElement(By.name(textFieldName));
		textFieldElement.sendKeys(value);
		return textFieldElement;
	}

	public static WebElement getAnWebElementById(String elementId, WebDriver driver)
	{
		WebElement webElement = driver.findElement(By.id(elementId));
		return webElement;
	}

	public static WebElement getAnWebElementByName(String elementName, WebDriver driver)
	{
		WebElement webElement = driver.findElement(By.name(elementName));
		return webElement;
	}

	private static String createCssSelector(String tagName, String attributeName, String Value)
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(tagName).append('[').append(attributeName).append('=').append("'").append(Value)
				.append("']");
		return stringBuilder.toString();
	}

	// ############################################################################################################
	// ############################################################################################################
	// ##################################### Attribute Mapping related event handlers
	public static WebElement clickToOpenOneMappingProject(String mappingProjectName, WebDriver driver)
			throws InterruptedException
	{
		openMappingService(driver);
		WebElement mappingProjectLinkButton = driver.findElement(By.xpath("//table/tbody/tr/td/a[text() = '"
				+ mappingProjectName + "']"));
		mappingProjectLinkButton.click();
		Thread.sleep(BUTTON_CLICK_SLEEP_TIME);
		return mappingProjectLinkButton;
	}

	public static WebElement clickCancelButonForAddingNewSourceToMappingProject(WebDriver driver)
			throws InterruptedException
	{
		WebElement cancelButtonElement = driver.findElement(By
				.xpath("//div[@id='create-new-source-column-modal']//button[contains(text(), 'Cancel')]"));
		cancelButtonElement.click();
		Thread.sleep(BUTTON_CLICK_SLEEP_TIME);
		return cancelButtonElement;
	}

	public static WebElement clickOnEditAttributeMappingTableByIndex(int rowNumber, int columnNumber, WebDriver driver)
			throws InterruptedException
	{
		WebElement columnElement = driver.findElement(By.xpath("//table[@id='attribute-mapping-table']/tbody/tr["
				+ rowNumber + "]/td[" + columnNumber + "]"));
		WebElement pencilButton = columnElement.findElement(By.cssSelector("span.glyphicon-pencil"));
		pencilButton.click();
		Thread.sleep(BUTTON_CLICK_SLEEP_TIME);
		return pencilButton;
	}

	public static WebElement clickOnRmoveAttributeMappingTableByIndex(int rowNumber, int columnNumber, WebDriver driver)
			throws InterruptedException
	{
		WebElement columnElement = driver.findElement(By.xpath("//table[@id='attribute-mapping-table']/tbody/tr["
				+ rowNumber + "]/td[" + columnNumber + "]"));
		WebElement trashButton = columnElement.findElement(By.cssSelector("span.glyphicon-remove"));
		trashButton.click();
		Thread.sleep(BUTTON_CLICK_SLEEP_TIME);
		return trashButton;
	}

	public static WebElement getOneCellFromAttributeMappingTableByIndex(int rowNumber, int columnNumber,
			WebDriver driver) throws InterruptedException
	{
		WebElement cellContentElement = driver.findElement(By.xpath("//table[@id='attribute-mapping-table']/tbody/tr["
				+ rowNumber + "]/td[" + columnNumber + "]/div[not(form)]"));
		return cellContentElement;
	}

	public static WebElement clickCancelButonForRemoveOneAttributeMapping(WebDriver driver) throws InterruptedException
	{
		WebElement cancelButtonElement = driver
				.findElement(By
						.xpath("//div[@class='modal-body']/div[text()='Are you sure?']/../../div[@class = 'modal-footer']/button[text()='Cancel']"));
		cancelButtonElement.click();
		Thread.sleep(BUTTON_CLICK_SLEEP_TIME);
		return cancelButtonElement;
	}

	public static WebElement clickOnGoBackToMappingProjectOverView(WebDriver driver) throws InterruptedException
	{
		WebElement goBackToMappingProjectButton = driver.findElement(By
				.xpath("//div[@id='attribute-mapping-toolbar']/a[@type='btn']"));
		goBackToMappingProjectButton.click();
		Thread.sleep(BUTTON_CLICK_SLEEP_TIME);
		return goBackToMappingProjectButton;
	}

	public static WebElement clickOnCreateIntegratedDataSetButton(WebDriver driver) throws InterruptedException
	{
		WebElement createIntegratedDataButton = driver.findElement(By
				.xpath("//a[@data-target='#create-integrated-entity-modal' and contains(@Class, btn)]"));
		createIntegratedDataButton.click();
		Thread.sleep(BUTTON_CLICK_SLEEP_TIME);
		return createIntegratedDataButton;
	}

	public static WebElement getPageTitleInAttributeMappingPage(WebDriver driver) throws InterruptedException
	{
		WebElement pageTitleElement = driver.findElement(By.xpath("//center/h4"));
		return pageTitleElement;
	}

	public static List<WebElement> getColumnHeadersInOneMappingProject(WebDriver driver) throws InterruptedException
	{
		return driver.findElements(By.xpath("//table[not(tbody)][1]/thead/tr/th"));
	}

	public static WebElement getToolTipElementInThePage(WebDriver driver) throws InterruptedException
	{
		return driver.findElement(By
				.xpath("//div[contains(@class, 'tooltip') and @role='tooltip']/div[@class='tooltip-inner']"));
	}
}