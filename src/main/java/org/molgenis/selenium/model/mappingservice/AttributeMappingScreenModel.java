package org.molgenis.selenium.model.mappingservice;

import static org.molgenis.selenium.model.MappingServiceAppModel.MAPPING_PROJECT_NAME;
import static org.molgenis.selenium.util.MappingServiceUtil.clickButonById;
import static org.molgenis.selenium.util.MappingServiceUtil.clickOnCloseModalButton;
import static org.molgenis.selenium.util.MappingServiceUtil.clickOnEditAttributeMappingTableByIndex;
import static org.molgenis.selenium.util.MappingServiceUtil.clickOnGoBackToMappingProjectOverView;
import static org.molgenis.selenium.util.MappingServiceUtil.clickOnNextButtonToUncuratedAttributeMapping;
import static org.molgenis.selenium.util.MappingServiceUtil.clickOnSaveButtonInAttributeMapping;
import static org.molgenis.selenium.util.MappingServiceUtil.clickOnSaveToDiscussButtonInAttributeMapping;
import static org.molgenis.selenium.util.MappingServiceUtil.clickToOpenOneMappingProject;
import static org.molgenis.selenium.util.MappingServiceUtil.executeMouseEnterEventForHoverAttributeSection;
import static org.molgenis.selenium.util.MappingServiceUtil.executeMouseOutEventForHoverAttributeSection;
import static org.molgenis.selenium.util.MappingServiceUtil.getAlertMessageInCurrentPage;
import static org.molgenis.selenium.util.MappingServiceUtil.getAlgorithmStateInAttributeMapping;
import static org.molgenis.selenium.util.MappingServiceUtil.getAnWebElementById;
import static org.molgenis.selenium.util.MappingServiceUtil.getCellFromThePreviewResultTableInAttributeMappingScreen;
import static org.molgenis.selenium.util.MappingServiceUtil.getMappingCategoryEditorElement;
import static org.molgenis.selenium.util.MappingServiceUtil.getModalBodyContent;
import static org.molgenis.selenium.util.MappingServiceUtil.getModalHeaderTitle;
import static org.molgenis.selenium.util.MappingServiceUtil.getPageTitleInAttributeMappingPage;
import static org.molgenis.selenium.util.MappingServiceUtil.getRowsFromAttributeMappingTable;
import static org.molgenis.selenium.util.MappingServiceUtil.getToolTipElementInThePage;
import static org.molgenis.selenium.util.MappingServiceUtil.getValueToAlgorithmEditorInAttributeMapping;
import static org.molgenis.selenium.util.MappingServiceUtil.isCheckBoxSelectedInSuggestedAttributeByRowIndex;
import static org.molgenis.selenium.util.MappingServiceUtil.isNextButtonToUncuratedAttributeMappingVisible;
import static org.molgenis.selenium.util.MappingServiceUtil.isResultContainerVisiableInAttributeMapping;
import static org.molgenis.selenium.util.MappingServiceUtil.mapCategoriesForGenderinLifeLines;
import static org.molgenis.selenium.util.MappingServiceUtil.refreshCurrentPage;
import static org.molgenis.selenium.util.MappingServiceUtil.setValueToAlgorithmEditorInAttributeMapping;
import static org.molgenis.selenium.util.MappingServiceUtil.switchToAlgorithmCategoryMappingEditor;
import static org.molgenis.selenium.util.MappingServiceUtil.switchToAlgorithmScriptEditor;
import static org.molgenis.selenium.util.MappingServiceUtil.toggleCheckBoxInSuggestedAttributeByRowIndex;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

public class AttributeMappingScreenModel
{
	private WebDriver driver;
	private static final String ATTRIBUTE_SECTION_CONTAINER = "attribute-mapping-table-container";
	private static final String ALGORITHM_SECTION_CONTAINER = "attribute-mapping-container";
	private static final String PREVIEW_SECTION_CONTAINER = "result-container";
	private static final String JS_FUNCTION_MODEL_BUTTON_ID = "js-function-modal-btn";
	private static final String JS_FUNCTION_MODEL_ID = "js-function-modal";
	private static final String PREVIEW_ALGORITHM_RESULT_VALIDATION_ERROR_ID = "validation-errors";
	private static final String JS_FUNCTION_MODEL_TITLE = "Javascript function examples";
	private static final String JS_MAP_FUNCTION_NAME = ".map()";
	private static final String MAPPING_GENDER_TO_LIFELINES_TITLE_IN_ATTRIBUTE_MAPPING_SCREEN = "Mapping to HOP_selenium.HOP_GENDER from lifelines_test";
	private static final String MAPPING_GLUCOSE_TO_LIFELINES_TITLE_IN_ATTRIBUTE_MAPPING_SCREEN = "Mapping to HOP_selenium.LAB_GLUC_FASTING from lifelines_test";
	private static final String MAPPING_NEXT_ATTRIBUTE_FOR_LIFELINES_TITLE_IN_ATTRIBUTE_MAPPING_SCREEN = "Mapping to HOP_selenium.PM_HEIGHT_MEASURE from lifelines_test";
	private static final String ATTRIBUTE_QUESTION_MARK_TOOL_TIP_MESSAGE = "Select attribute(s) to map to HOP_GENDER. By checking one of the attributes below, an algorithm will be generated and the result of your selection will be shown.";
	private static final String ALGORITHM_QUESTION_MARK_TOOL_TIP_MESSAGE = "Use one of the methods below to map the values of the selected attribute(s) to the target attribute. The script editor offers large control over your algorithm, but javascript knowledge is needed. The Map tab allows you to map the various categorical values or strings to the categorical values of the target attribute.";
	private static final String PREVIEW_QUESTION_MARK_TOOL_TIP_MESSAGE = "The most right column contains the results of applying the algorithm over the values of the selected source attributes.";
	private static final String LIFELINES_SOURCE_FIRST_ROW_FASTING_VALUE = "1 = Yes";
	private static final String LIFELINES_SOURCE_FIRST_ROW_GLUCOSE_VALUE = "6.6";
	private static final String ALGORITHM_FOR_LIFELINES_FASTING = "$('NUCHTER1').value();";
	private static final String ALGORITHM_FOR_LIFELINES_GLUCOSE = "$('GLU').value();";
	private static final String INVALID_ALGORITHM_SYNTAX = "3nf";
	private static final String PREVIEW_ALGORITHM_RESULT_TARGET_ERROR_MESSAGE = "Invalid script";
	private static final String PREVIEW_ALGORITHM_RESULT_NUMBER_OF_ERRORS = "1";
	private static final String ALGORITHM_FOR_MAPPING_GENDER_IN_LIFELINES = "$('GENDER').map({\"1\":\"0\",\"2\":\"1\"}, null, null).value();";
	private static final String ALGORITHM_STATE_DISCUSS = "DISCUSS";
	private static final String ALGORITHM_STATE_CURATED = "CURATED";
	private static final String ALERT_MESSAGE_FOR_ALGORITHM_STATE_DISSCUSS = "Success! This attribute mapping is saved with the state DISCUSS";
	private static final String ALERT_MESSAGE_FOR_ALGORITHM_STATE_CURATED = "Success! This attribute mapping is saved with the state CURATED";

	public static final Logger LOG = LoggerFactory.getLogger(AttributeMappingScreenModel.class);

	public AttributeMappingScreenModel(WebDriver webDriver)
	{
		this.driver = Objects.requireNonNull(webDriver);
	}

	public void openOneMappingProject() throws InterruptedException
	{
		clickToOpenOneMappingProject(MAPPING_PROJECT_NAME, driver);
	}

	public void clickGenderAttributeForLifeLinesSource() throws InterruptedException
	{
		clickOnEditAttributeMappingTableByIndex(1, 2, driver);

		// Check if the next attribute mapping button exists, now it should not
		Assert.assertFalse(isNextButtonToUncuratedAttributeMappingVisible(driver));

		// Check the title of the attribute mapping screen
		Assert.assertEquals(getPageTitleInAttributeMappingPage(driver).getText(),
				MAPPING_GENDER_TO_LIFELINES_TITLE_IN_ATTRIBUTE_MAPPING_SCREEN);

		// There is one suggested attribute mapping
		Assert.assertTrue(getRowsFromAttributeMappingTable(driver).size() == 1);

		// The category mapping editor should be visible
		Assert.assertTrue(getMappingCategoryEditorElement(driver).size() == 1);

		// Check the tooltip message in the Attribute section
		hoverAttributeQuestionMarkInAttributeMapping();

		// Check the tooltip message in the Algorithm section
		hoverAlgorithmQuestionMarkInAttributeMapping();

		// Check the tooltip message in the Preview section
		hoverPreviewQuestionMarkInAttributeMapping();

		// Check the javascript modal content
		checkAvailableJavascriptFunctions();

		// Set an empty value to the ace editor
		setValueToAlgorithmEditorInAttributeMapping(StringUtils.EMPTY, driver);

		// Now the selected source attribute should not be selected anymore
		isCheckBoxSelectedInSuggestedAttributeByRowIndex(1, driver);

		// Set a new illegal value to the ace editor
		setValueToAlgorithmEditorInAttributeMapping(INVALID_ALGORITHM_SYNTAX, driver);

		// There should be one error in the preview validation
		Assert.assertEquals(getAnWebElementById(PREVIEW_ALGORITHM_RESULT_VALIDATION_ERROR_ID, driver).getText(),
				PREVIEW_ALGORITHM_RESULT_NUMBER_OF_ERRORS);

		// An error message should be shown in the preview result table for the target attribute
		Assert.assertEquals(getCellFromThePreviewResultTableInAttributeMappingScreen(1, 1, driver).getText(),
				PREVIEW_ALGORITHM_RESULT_TARGET_ERROR_MESSAGE);

		// Refresh the page to get back the previous algorithm
		refreshCurrentPage(driver);

		// Switch to the category mapping editor
		switchToAlgorithmCategoryMappingEditor(driver);

		// Map the categories for the target attribute Gender in Lifelines
		mapCategoriesForGenderinLifeLines(driver);

		// Switch to back the script editor
		switchToAlgorithmScriptEditor(driver);

		// Check if the algorithm generated from mapping categories is same as the correct algorithm
		Assert.assertEquals(getValueToAlgorithmEditorInAttributeMapping(driver),
				ALGORITHM_FOR_MAPPING_GENDER_IN_LIFELINES);

		// Click save to discuss button to save the algorithm
		clickOnSaveToDiscussButtonInAttributeMapping(driver);
		// Check if the algorithm state is changed to Discuss
		Assert.assertEquals(getAlgorithmStateInAttributeMapping(driver), ALGORITHM_STATE_DISCUSS);
		// Check if the alert message is currently matched
		Assert.assertEquals(replaceIllegalCharsInString(getAlertMessageInCurrentPage(driver)),
				ALERT_MESSAGE_FOR_ALGORITHM_STATE_DISSCUSS);

		// Click save button to save the algorithm
		clickOnSaveButtonInAttributeMapping(driver);
		// Check if the algorithm state is changed to Curated
		Assert.assertEquals(getAlgorithmStateInAttributeMapping(driver), ALGORITHM_STATE_CURATED);
		// Check if the alert message is currently matched
		Assert.assertEquals(replaceIllegalCharsInString(getAlertMessageInCurrentPage(driver)),
				ALERT_MESSAGE_FOR_ALGORITHM_STATE_CURATED);

		// Check if the next attribute mapping button exists, now it should
		Assert.assertTrue(isNextButtonToUncuratedAttributeMappingVisible(driver));

		clickOnNextButtonToUncuratedAttributeMapping(driver);

		// Check the title of the attribute mapping screen
		Assert.assertEquals(getPageTitleInAttributeMappingPage(driver).getText(),
				MAPPING_NEXT_ATTRIBUTE_FOR_LIFELINES_TITLE_IN_ATTRIBUTE_MAPPING_SCREEN);
	}

	public void clickFastingGlucoseAttributeForLifeLinesSource() throws InterruptedException
	{
		clickOnEditAttributeMappingTableByIndex(6, 2, driver);

		// Check the title of the attribute mapping screen
		Assert.assertEquals(getPageTitleInAttributeMappingPage(driver).getText(),
				MAPPING_GLUCOSE_TO_LIFELINES_TITLE_IN_ATTRIBUTE_MAPPING_SCREEN);

		// The algorithm here should be empty
		Assert.assertEquals(getValueToAlgorithmEditorInAttributeMapping(driver), StringUtils.EMPTY);

		// There are two suggested attribute mappings
		Assert.assertTrue(getRowsFromAttributeMappingTable(driver).size() == 2);

		// The category mapping editor should be hidden
		Assert.assertTrue(getMappingCategoryEditorElement(driver).size() == 0);

		// The preview result table should be hidden
		Assert.assertFalse(isResultContainerVisiableInAttributeMapping(driver));

		// Select the first suggested source attribute
		toggleCheckBoxInSuggestedAttributeByRowIndex(1, driver);

		// The preview result table now should be visible
		Assert.assertTrue(isResultContainerVisiableInAttributeMapping(driver));

		// Check the value of the first row for the first suggested source attribute (fasting) in the preview result
		// table
		Assert.assertEquals(getCellFromThePreviewResultTableInAttributeMappingScreen(1, 1, driver).getText(),
				LIFELINES_SOURCE_FIRST_ROW_FASTING_VALUE);

		// Check the algorithm in the ace editor
		Assert.assertEquals(getValueToAlgorithmEditorInAttributeMapping(driver), ALGORITHM_FOR_LIFELINES_FASTING);

		// Select the second suggested source attribute
		toggleCheckBoxInSuggestedAttributeByRowIndex(2, driver);

		// Check the value of the first row for the second suggested source attribute (glucose) in the preview result
		// table
		Assert.assertEquals(getCellFromThePreviewResultTableInAttributeMappingScreen(1, 2, driver).getText(),
				LIFELINES_SOURCE_FIRST_ROW_GLUCOSE_VALUE);

		// Check the value of the first row for the algorithm result generated for the target attribute
		Assert.assertEquals(getCellFromThePreviewResultTableInAttributeMappingScreen(1, 3, driver).getText(),
				LIFELINES_SOURCE_FIRST_ROW_GLUCOSE_VALUE);

		// Check the algorithm in the ace editor
		Assert.assertEquals(getValueToAlgorithmEditorInAttributeMapping(driver), ALGORITHM_FOR_LIFELINES_FASTING
				+ ALGORITHM_FOR_LIFELINES_GLUCOSE);

		// Deselect the first suggested source attribute
		toggleCheckBoxInSuggestedAttributeByRowIndex(1, driver);

		// The second column of the preview result in the first row should not be equal to the glucose level anymore
		Assert.assertNotEquals(getCellFromThePreviewResultTableInAttributeMappingScreen(1, 1, driver).getText(),
				LIFELINES_SOURCE_FIRST_ROW_FASTING_VALUE);

		// Check the algorithm in the ace editor
		Assert.assertEquals(getValueToAlgorithmEditorInAttributeMapping(driver), ALGORITHM_FOR_LIFELINES_GLUCOSE);

		// Deselect the second suggested source attribute
		toggleCheckBoxInSuggestedAttributeByRowIndex(2, driver);

		// The first column of the preview result in the first row should not be equal to the fasting status anymore
		Assert.assertNotEquals(getCellFromThePreviewResultTableInAttributeMappingScreen(1, 1, driver).getText(),
				LIFELINES_SOURCE_FIRST_ROW_GLUCOSE_VALUE);

		// Check the algorithm in the ace editor
		Assert.assertEquals(getValueToAlgorithmEditorInAttributeMapping(driver), StringUtils.EMPTY);

		// The preview result table now should be hidden now
		Assert.assertFalse(isResultContainerVisiableInAttributeMapping(driver));
	}

	public void hoverAttributeQuestionMarkInAttributeMapping() throws InterruptedException
	{
		executeMouseEnterEventForHoverAttributeSection(ATTRIBUTE_SECTION_CONTAINER, driver);

		Assert.assertEquals(getToolTipElementInThePage(driver).getText(), ATTRIBUTE_QUESTION_MARK_TOOL_TIP_MESSAGE);

		executeMouseOutEventForHoverAttributeSection(ATTRIBUTE_SECTION_CONTAINER, driver);
	}

	public void hoverAlgorithmQuestionMarkInAttributeMapping() throws InterruptedException
	{
		executeMouseEnterEventForHoverAttributeSection(ALGORITHM_SECTION_CONTAINER, driver);

		Assert.assertEquals(getToolTipElementInThePage(driver).getText(), ALGORITHM_QUESTION_MARK_TOOL_TIP_MESSAGE);

		executeMouseOutEventForHoverAttributeSection(ALGORITHM_SECTION_CONTAINER, driver);
	}

	public void hoverPreviewQuestionMarkInAttributeMapping() throws InterruptedException
	{
		executeMouseEnterEventForHoverAttributeSection(PREVIEW_SECTION_CONTAINER, driver);

		Assert.assertEquals(getToolTipElementInThePage(driver).getText(), PREVIEW_QUESTION_MARK_TOOL_TIP_MESSAGE);

		executeMouseOutEventForHoverAttributeSection(PREVIEW_SECTION_CONTAINER, driver);
	}

	public void goBackToMappingOneMappingProject() throws InterruptedException
	{
		clickOnGoBackToMappingProjectOverView(driver);
	}

	private void checkAvailableJavascriptFunctions() throws InterruptedException
	{
		clickButonById(JS_FUNCTION_MODEL_BUTTON_ID, driver);

		Assert.assertEquals(getModalHeaderTitle(JS_FUNCTION_MODEL_ID, driver), JS_FUNCTION_MODEL_TITLE);

		Assert.assertTrue(getModalBodyContent(JS_FUNCTION_MODEL_ID, driver).contains(JS_MAP_FUNCTION_NAME));

		clickOnCloseModalButton(JS_FUNCTION_MODEL_ID, driver);
	}

	private String replaceIllegalCharsInString(String string)
	{
		return string.replaceAll("[^a-zA-Z0-9! ]", StringUtils.EMPTY).trim();
	}
}