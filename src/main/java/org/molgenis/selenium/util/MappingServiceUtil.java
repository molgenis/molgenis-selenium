package org.molgenis.selenium.util;

import static org.molgenis.selenium.model.mappingservice.AbstractMappingServiceAppModel.MAPPING_PROJECT_NAME;
import static org.molgenis.selenium.model.mappingservice.AbstractMappingServiceAppModel.MAPPING_SERVICE_BASE_URL;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;

import junit.framework.Assert;

public class MappingServiceUtil
{
	private static final String MAIN_MENU = "Data Integration";
	private static final String SUB_MENU = "Mapping Service";
	private final static String CREATE_NEW_MAPPING_PROJECT_MODAL = "create-new-mapping-project-modal";
	private static final int BUTTON_CLICK_SLEEP_TIME = 3000;
	private static final int WAIT_FOR_NON_BLANK_SECONDS = 3;
	
	private static final Logger LOG = LoggerFactory.getLogger(MappingServiceUtil.class);

	// ############################################################################################################
	// ############################################################################################################
	// ##################################### General helper functions
	public static String getAlertMessageInCurrentPage(WebDriver driver)
	{
		return driver.findElement(By.xpath("//div[@class='alerts']/div")).getText();
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

	public static void clickButonByElementId(String buttonId, WebDriver driver) throws InterruptedException
	{
		WebElement buttonElement = driver.findElement(By.id(buttonId));
		buttonElement.click();
		Thread.sleep(BUTTON_CLICK_SLEEP_TIME);
	}

	// ############################################################################################################
	// ############################################################################################################
	// ##################################### functions for manipulating the confirmation modal
	public static void clickOKButonInConfirmationModal(WebDriver driver) throws InterruptedException
	{
		WebElement confirmButton = driver.findElement(By.xpath(
				"//div[@class='modal-body']//div[text()='Are you sure?']/../../div[@class='modal-footer']/button[text()='OK']"));
		confirmButton.click();
		Thread.sleep(BUTTON_CLICK_SLEEP_TIME);
	}

	public static void clickCancelButonInConfirmationModal(WebDriver driver) throws InterruptedException
	{
		WebElement cancelButtonElement = driver.findElement(By.xpath(
				"//div[@class='modal-body']/div[text()='Are you sure?']/../../div[@class = 'modal-footer']/button[text()='Cancel']"));
		cancelButtonElement.click();
		Thread.sleep(BUTTON_CLICK_SLEEP_TIME);
	}

	public static String getModalHeaderTitle(String modalContainerId, WebDriver driver)
	{
		WebElement modalHeaderElement = driver
				.findElement(By.xpath("//div[@id='" + modalContainerId + "']//div[@class='modal-header']/h4"));
		return modalHeaderElement.getText();
	}

	public static String getModalBodyContent(String modalContainerId, WebDriver driver)
	{
		WebElement modalBodyElement = driver
				.findElement(By.xpath("//div[@id='" + modalContainerId + "']//div[@class='modal-body']"));
		return modalBodyElement.getText();
	}

	public static void clickOnCloseModalButton(String modalContainerId, WebDriver driver) throws InterruptedException
	{
		WebElement closeModalButtonElement = driver.findElement(
				By.xpath("//div[@id='" + modalContainerId + "']//div[@class='modal-header']/button[@class='close']"));
		closeModalButtonElement.click();
		Thread.sleep(BUTTON_CLICK_SLEEP_TIME);
	}

	// ############################################################################################################
	// ############################################################################################################
	// ##################################### View the Mapping Project Overview related event handlers
	public static void openMappingService(WebDriver driver) throws InterruptedException
	{
		MenuUtil.openPageByClickOnSubMenuItem(MAIN_MENU, SUB_MENU, driver);
	}

	public static List<WebElement> getRowWebElementsFromMappingProjectTable(WebDriver driver)
	{
		return driver.findElements(By.xpath("//table/tbody/tr"));
	}

	public static WebElement getTestMappingProjectFromTable(WebDriver driver)
	{
		for (WebElement webElement : getRowWebElementsFromMappingProjectTable(driver))
		{
			if (webElement.getText().contains(MAPPING_PROJECT_NAME))
			{
				return webElement;
			}
		}
		return null;
	}

	public static void clickCancelButonInAddNewMappingProjectModal(WebDriver driver) throws InterruptedException
	{
		WebElement buttonElement = driver.findElement(
				By.xpath("//div[@id='" + CREATE_NEW_MAPPING_PROJECT_MODAL + "']//button[contains(text(), 'Cancel')]"));
		buttonElement.click();
		Thread.sleep(BUTTON_CLICK_SLEEP_TIME);
	}

	public static void clickRemoveMappingProjectButtonForCurrentRowElement(WebElement webElement, WebDriver driver)
			throws InterruptedException
	{
		WebElement buttonElement = webElement.findElement(By.className("btn-danger"));
		buttonElement.click();
		Thread.sleep(BUTTON_CLICK_SLEEP_TIME);
	}

	public static void clickButonWithInSpecifiedElementByTagName(WebElement webElement, String tagName,
			WebDriver driver) throws InterruptedException
	{
		WebElement buttonElement = webElement.findElement(By.tagName(tagName));
		buttonElement.click();
		Thread.sleep(BUTTON_CLICK_SLEEP_TIME);
	}

	public static void clickGoBackButtonToMappingProjectOverView(WebDriver driver) throws InterruptedException
	{
		WebElement goBackToProjectOverviewButton = driver.findElement(By
				.xpath("//a[contains(@class, 'btn-default') and contains(@href, '" + MAPPING_SERVICE_BASE_URL + "')]"));
		goBackToProjectOverviewButton.click();
		Thread.sleep(BUTTON_CLICK_SLEEP_TIME);
	}

	public static String getFieldRequiredMessageFromCreateMappingProjectModal(WebDriver driver)
	{
		WebElement webElement = driver.findElement(
				By.xpath("//div[@id='" + CREATE_NEW_MAPPING_PROJECT_MODAL + "']//label[@for='mapping-project-name']"));
		return webElement.getText();
	}

	// ############################################################################################################
	// ############################################################################################################
	// ##################################### View One Mapping Project screen related event handlers
	public static void clickToOpenOneMappingProject(String mappingProjectName, WebDriver driver)
			throws InterruptedException
	{
		openMappingService(driver);
		WebElement mappingProjectLinkButton = driver
				.findElement(By.xpath("//table/tbody/tr/td/a[text() = '" + mappingProjectName + "']"));
		mappingProjectLinkButton.click();
		Thread.sleep(BUTTON_CLICK_SLEEP_TIME);
	}

	public static void clickCancelButtonForAddingNewSourceToMappingProject(WebDriver driver) throws InterruptedException
	{
		WebElement cancelButtonElement = driver.findElement(
				By.xpath("//div[@id='create-new-source-column-modal']//button[contains(text(), 'Cancel')]"));
		cancelButtonElement.click();
		Thread.sleep(BUTTON_CLICK_SLEEP_TIME);
	}

	public static void clickRemoveButtonForRemoveSourceColumns(WebElement webElement, WebDriver driver)
			throws InterruptedException
	{
		WebElement removeSourceColumnButton = webElement.findElement(By.tagName("button"));
		removeSourceColumnButton.click();
		Thread.sleep(BUTTON_CLICK_SLEEP_TIME);
	}

	public static void clickOnRmoveAttributeMappingTableByIndex(int rowNumber, int columnNumber, WebDriver driver)
			throws InterruptedException
	{
		WebElement columnElement = driver.findElement(By
				.xpath("//table[@id='attribute-mapping-table']/tbody/tr[" + rowNumber + "]/td[" + columnNumber + "]"));
		WebElement trashButton = columnElement.findElement(By.cssSelector("span.glyphicon-remove"));
		trashButton.click();
		Thread.sleep(BUTTON_CLICK_SLEEP_TIME);
	}

	public static WebElement getOneCellFromAttributeMappingTableByIndex(int rowNumber, int columnNumber,
			WebDriver driver) throws InterruptedException
	{
		WebElement cellContentElement = driver.findElement(By.xpath("//table[@id='attribute-mapping-table']/tbody/tr["
				+ rowNumber + "]/td[" + columnNumber + "]/div[not(form)]"));
		return cellContentElement;
	}

	public static void clickOnCreateIntegratedDataSetButton(WebDriver driver) throws InterruptedException
	{
		WebElement createIntegratedDataButton = driver
				.findElement(By.xpath("//a[@data-target='#create-integrated-entity-modal' and contains(@Class, btn)]"));
		createIntegratedDataButton.click();
		Thread.sleep(BUTTON_CLICK_SLEEP_TIME);
	}

	public static List<WebElement> getColumnHeadersInOneMappingProject(WebDriver driver) throws InterruptedException
	{
		return driver.findElements(By.xpath("//table[not(tbody)][1]/thead/tr/th"));
	}

	// ############################################################################################################
	// ############################################################################################################
	// ##################################### The Attribute Mapping related event handlers
	public static void clickOnGoBackToOneMappingProject(WebDriver driver) throws InterruptedException
	{
		WebElement goBackToMappingProjectButton = driver
				.findElement(By.xpath("//div[@id='attribute-mapping-toolbar']/a[@type='btn']"));
		goBackToMappingProjectButton.click();
		Thread.sleep(BUTTON_CLICK_SLEEP_TIME);
	}

	public static void clickOnEditAttributeMappingTableByIndex(int rowNumber, int columnNumber, WebDriver driver)
			throws InterruptedException
	{
		WebElement columnElement = driver.findElement(By
				.xpath("//table[@id='attribute-mapping-table']/tbody/tr[" + rowNumber + "]/td[" + columnNumber + "]"));
		WebElement pencilButton = columnElement.findElement(By.cssSelector("span.glyphicon-pencil"));
		pencilButton.click();
		Thread.sleep(BUTTON_CLICK_SLEEP_TIME);
	}

	public static void executeMouseEnterEventForHoverAttributeSection(String containerId, WebDriver driver)
			throws InterruptedException
	{
		Assert.assertTrue(driver instanceof JavascriptExecutor);

		((JavascriptExecutor) driver).executeScript("$('#" + containerId + " i:eq(0)').trigger('mouseenter')");

		Thread.sleep(BUTTON_CLICK_SLEEP_TIME);
	}

	public static void executeMouseOutEventForHoverAttributeSection(String containerId, WebDriver driver)
			throws InterruptedException
	{
		Assert.assertTrue(driver instanceof JavascriptExecutor);

		((JavascriptExecutor) driver).executeScript("$('#" + containerId + " i:eq(0)').trigger('mouseout')");

		Thread.sleep(BUTTON_CLICK_SLEEP_TIME);
	}

	public static void setValueToAlgorithmEditorInAttributeMapping(String value, WebDriver driver)
			throws InterruptedException
	{
		Assert.assertTrue(driver instanceof JavascriptExecutor);
		String script = "$('#ace-editor-text-area').data('ace').editor.setValue(\"" + value + "\")";
		((JavascriptExecutor) driver).executeScript(script);
		Thread.sleep(BUTTON_CLICK_SLEEP_TIME);
	}

	public static String getValueFromAlgorithmEditorInAttributeMapping(WebDriver driver) throws InterruptedException
	{
		Stopwatch sw = Stopwatch.createStarted();
		SeleniumUtils.isElementPresent(By.xpath("//textarea[@id='ace-editor-text-area']"), driver);
		WebElement aceEditorValueContainerElements = driver
				.findElement(By.xpath("//textarea[@id='ace-editor-text-area']"));
		while (StringUtils.isBlank(aceEditorValueContainerElements.getAttribute("value")) 
				&& sw.elapsed(TimeUnit.SECONDS) < WAIT_FOR_NON_BLANK_SECONDS)
		{
			Assert.assertTrue(driver instanceof JavascriptExecutor);
			String script = "var editor = $('#ace-editor-text-area').data('ace').editor; $('#ace-editor-text-area').val(editor.getValue());";
			((JavascriptExecutor) driver).executeScript(script);
			aceEditorValueContainerElements = driver.findElement(By.xpath("//textarea[@id='ace-editor-text-area']"));
		}
		Thread.sleep(BUTTON_CLICK_SLEEP_TIME);
		return aceEditorValueContainerElements.getAttribute("value");
	}

	public static void toggleCheckBoxInSuggestedAttributeByRowIndex(int index, WebDriver driver)
			throws InterruptedException
	{
		WebElement checkBox = driver.findElement(
				By.xpath("//table[@id='attribute-mapping-table']/tbody/tr[" + index + "]//input[@type='checkbox']"));
		checkBox.click();
		Thread.sleep(BUTTON_CLICK_SLEEP_TIME);
	}

	public static boolean isCheckBoxSelectedInSuggestedAttributeByRowIndex(int index, WebDriver driver)
			throws InterruptedException
	{
		WebElement checkBox = driver.findElement(
				By.xpath("//table[@id='attribute-mapping-table']/tbody/tr[" + index + "]//input[@type='checkbox']"));
		return checkBox.isSelected();
	}

	public static boolean isResultContainerVisiableInAttributeMapping(WebDriver driver) throws InterruptedException
	{
		WebElement previewResultTableContainer = driver.findElement(By.id("result-container"));
		Thread.sleep(BUTTON_CLICK_SLEEP_TIME);
		return previewResultTableContainer.isDisplayed();
	}

	public static WebElement getCellFromThePreviewResultTableInAttributeMappingScreen(int row, int column,
			WebDriver driver) throws InterruptedException
	{
		WebElement previewTableCellElement = driver.findElement(By.xpath(
				"//div[@id='algorithm-result-feedback-container']//table/tbody/tr[" + row + "]/td[" + ++column + "]"));
		Thread.sleep(BUTTON_CLICK_SLEEP_TIME);
		return previewTableCellElement;
	}

	public static List<WebElement> getMappingCategoryEditorElement(WebDriver driver) throws InterruptedException
	{
		return driver.findElements(By.id("map-tab"));
	}

	public static WebElement getPageTitleInAttributeMappingPage(WebDriver driver) throws InterruptedException
	{
		WebElement pageTitleElement = driver.findElement(By.xpath("//center/h4"));
		return pageTitleElement;
	}

	public static List<WebElement> getRowsFromAttributeMappingTable(WebDriver driver) throws InterruptedException
	{
		List<WebElement> rowElementsFromAttributeMappingTable = driver
				.findElements(By.xpath("//table[@id='attribute-mapping-table']/tbody/tr"));
		return rowElementsFromAttributeMappingTable;
	}

	public static WebElement getToolTipElementInThePage(WebDriver driver) throws InterruptedException
	{
		return driver.findElement(
				By.xpath("//div[contains(@class, 'tooltip') and @role='tooltip']/div[@class='tooltip-inner']"));
	}

	public static void switchToAlgorithmCategoryMappingEditor(WebDriver driver) throws InterruptedException
	{
		WebElement categoryMappingEditorButton = driver.findElement(By.xpath("//li[@id='map-tab']/a"));
		categoryMappingEditorButton.click();
		Thread.sleep(BUTTON_CLICK_SLEEP_TIME);
	}

	public static void switchToAlgorithmScriptEditor(WebDriver driver) throws InterruptedException
	{
		WebElement categoryMappingEditorButton = driver.findElement(By.xpath("//li[@id='script-tab']/a"));
		categoryMappingEditorButton.click();
		Thread.sleep(BUTTON_CLICK_SLEEP_TIME);
	}

	public static void mapCategoriesForGenderinLifeLines(WebDriver driver) throws InterruptedException
	{
		SeleniumUtils.waitForElement(By.xpath("//table[@id='advanced-mapping-table']/tbody/tr[1]//select"), driver);
		WebElement selectInFirstSourceCategoryElement = driver
				.findElement(By.xpath("//table[@id='advanced-mapping-table']/tbody/tr[1]//select"));
		selectInFirstSourceCategoryElement.sendKeys("Male");
		Thread.sleep(BUTTON_CLICK_SLEEP_TIME);

		while (driver
				.findElements(By
						.xpath("//table[@id='advanced-mapping-table']/tbody/tr[2]//select/option[text()='Female' and @selected]"))
				.size() == 0)
		{
			WebElement selectInSecondSourceCategoryElement = driver
					.findElement(By.xpath("//table[@id='advanced-mapping-table']/tbody/tr[2]//select"));
			selectInSecondSourceCategoryElement.sendKeys("Female");
		}
		Thread.sleep(BUTTON_CLICK_SLEEP_TIME);
	}

	public static void clickOnSaveButtonInAttributeMapping(WebDriver driver) throws InterruptedException
	{
		WebElement saveAlgorithmButton = driver.findElement(By.id("save-mapping-btn"));
		saveAlgorithmButton.click();
		Thread.sleep(BUTTON_CLICK_SLEEP_TIME);
	}

	public static void clickOnSaveToDiscussButtonInAttributeMapping(WebDriver driver) throws InterruptedException
	{
		WebElement saveAlgorithmButton = driver.findElement(By.id("save-discuss-mapping-btn"));
		saveAlgorithmButton.click();
		Thread.sleep(BUTTON_CLICK_SLEEP_TIME);
	}

	public static String getAlgorithmStateInAttributeMapping(WebDriver driver) throws InterruptedException
	{
		return getAnWebElementById("algorithmState", driver).getText();
	}

	public static boolean isNextButtonToUncuratedAttributeMappingVisible(WebDriver driver)
	{
		List<WebElement> findElements = driver.findElements(By.id("find-first-to-curate-attribute-btn"));
		return findElements.size() > 0;
	}

	public static void clickOnNextButtonToUncuratedAttributeMapping(WebDriver driver) throws InterruptedException
	{
		WebElement NextButtonToUncuratedAttributeMapping = driver
				.findElement(By.id("find-first-to-curate-attribute-btn"));
		NextButtonToUncuratedAttributeMapping.click();
		Thread.sleep(BUTTON_CLICK_SLEEP_TIME);
	}
}