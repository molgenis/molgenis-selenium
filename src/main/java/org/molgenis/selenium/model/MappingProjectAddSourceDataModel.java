package org.molgenis.selenium.model;

import static org.molgenis.selenium.model.MappingServiceAppModel.INTEGRATED_DATASET_ENTITY_NAME;
import static org.molgenis.selenium.model.MappingServiceAppModel.MAPPING_PROJECT_NAME;
import static org.molgenis.selenium.util.MappingServiceUtil.clickButonById;
import static org.molgenis.selenium.util.MappingServiceUtil.clickCancelButonForAddingNewSourceToMappingProject;
import static org.molgenis.selenium.util.MappingServiceUtil.clickCancelButonForRemoveOneAttributeMapping;
import static org.molgenis.selenium.util.MappingServiceUtil.clickCancelButtonForRemoveSourceColumms;
import static org.molgenis.selenium.util.MappingServiceUtil.clickOKButonByXpathExpression;
import static org.molgenis.selenium.util.MappingServiceUtil.clickOnCreateIntegratedDataSetButton;
import static org.molgenis.selenium.util.MappingServiceUtil.clickOnEditAttributeMappingTableByIndex;
import static org.molgenis.selenium.util.MappingServiceUtil.clickOnGoBackToMappingProjectOverView;
import static org.molgenis.selenium.util.MappingServiceUtil.clickOnRmoveAttributeMappingTableByIndex;
import static org.molgenis.selenium.util.MappingServiceUtil.clickRemoveButtonForRemoveSourceColumns;
import static org.molgenis.selenium.util.MappingServiceUtil.clickToOpenOneMappingProject;
import static org.molgenis.selenium.util.MappingServiceUtil.getAnWebElementById;
import static org.molgenis.selenium.util.MappingServiceUtil.getColumnHeadersInOneMappingProject;
import static org.molgenis.selenium.util.MappingServiceUtil.getOneCellFromAttributeMappingTableByIndex;
import static org.molgenis.selenium.util.MappingServiceUtil.getPageTitleInAttributeMappingPage;
import static org.molgenis.selenium.util.MappingServiceUtil.setValueToTextFieldByName;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.molgenis.selenium.util.Select2Util;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

public class MappingProjectAddSourceDataModel
{
	private WebDriver driver;

	public static final Logger LOG = LoggerFactory.getLogger(MappingProjectAddSourceDataModel.class);

	public static final String LIFELINES_ENTITY_NAME = "lifelines_test";
	private static final String ADD_NEW_SOURCE_BUTTON_ID = "add-new-attr-mapping-btn";
	private static final String ADD_NEW_SOURCE_MODAL_CONTAINER = "create-new-source-column-modal";
	private static final String ADD_NEW_SOURCE_SUBMIT_BUTTON_ID = "submit-new-source-column-btn";
	private static final String SOURCE_LIFELINES_COLUMN_HEADER = "Source: lifelines_test";
	private static final String MAPPING_GENDER_TO_LIFELINES_TITLE_IN_ATTRIBUTE_MAPPING_SCREEN = "Mapping to HOP_selenium.HOP_GENDER from lifelines_test";
	private static final String INTEGRATED_DATASET_TEXTFIELD = "newEntityName";
	private static final String CREATED_INTEGRATED_DATA_CONFIRM_BUTTON = "create-integrated-entity-btn";

	private static final String DATA_EXPLORER_ENTITY_HEADER_ELEMENT_ID = "entity-class-name";

	public MappingProjectAddSourceDataModel(WebDriver webDriver)
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

		Assert.assertEquals(getPageTitleInAttributeMappingPage(driver).getText(),
				MAPPING_GENDER_TO_LIFELINES_TITLE_IN_ATTRIBUTE_MAPPING_SCREEN);

		clickOnGoBackToMappingProjectOverView(driver);
	}

	public void cancelRemoveGenderAttributeForLifeLinesSource() throws InterruptedException
	{
		clickOnRmoveAttributeMappingTableByIndex(1, 2, driver);

		clickCancelButonForRemoveOneAttributeMapping(driver);

		Assert.assertEquals(getOneCellFromAttributeMappingTableByIndex(1, 2, driver).getText(), "Sex");
	}

	public void removeGenderAttributeForLifeLinesSource() throws InterruptedException
	{
		clickOnRmoveAttributeMappingTableByIndex(1, 2, driver);

		clickOKButonByXpathExpression(driver);

		Assert.assertTrue(StringUtils.isBlank(getOneCellFromAttributeMappingTableByIndex(1, 2, driver).getText()));
	}

	public void addLifeLinesSourceToMappingProject() throws InterruptedException
	{
		clickButonById(ADD_NEW_SOURCE_BUTTON_ID, driver);

		Select2Util.select(ADD_NEW_SOURCE_MODAL_CONTAINER, LIFELINES_ENTITY_NAME, driver, LOG);

		clickButonById(ADD_NEW_SOURCE_SUBMIT_BUTTON_ID, driver);

		Assert.assertTrue(getColumnHeadersInOneMappingProject(driver).size() > 1);

		Assert.assertTrue(headerContainsSoucenEntity(LIFELINES_ENTITY_NAME));
	}

	public void cancelAddLifeLinesSourceToMappingProject() throws InterruptedException
	{
		clickButonById(ADD_NEW_SOURCE_BUTTON_ID, driver);

		Select2Util.select(ADD_NEW_SOURCE_MODAL_CONTAINER, LIFELINES_ENTITY_NAME, driver, LOG);

		clickCancelButonForAddingNewSourceToMappingProject(driver);

		Assert.assertFalse(headerContainsSoucenEntity(LIFELINES_ENTITY_NAME));
	}

	public void cancelRemoveLifeLinesSourceToMappingProject() throws InterruptedException
	{
		WebElement lifelinesColumnHeaderElement = getLifeLinesColumnHeaderElement();

		Assert.assertTrue(lifelinesColumnHeaderElement != null);

		clickRemoveButtonForRemoveSourceColumns(lifelinesColumnHeaderElement, driver);

		clickCancelButtonForRemoveSourceColumms(driver);

		Assert.assertTrue(headerContainsSoucenEntity(LIFELINES_ENTITY_NAME));
	}

	public void removeLifeLinesSourceToMappingProject() throws InterruptedException
	{
		WebElement lifelinesColumnHeaderElement = getLifeLinesColumnHeaderElement();

		Assert.assertTrue(lifelinesColumnHeaderElement != null);

		clickRemoveButtonForRemoveSourceColumns(lifelinesColumnHeaderElement, driver);

		clickOKButonByXpathExpression(driver);

		Assert.assertFalse(headerContainsSoucenEntity(LIFELINES_ENTITY_NAME));
	}

	public void integrateSourceData() throws InterruptedException
	{
		clickOnCreateIntegratedDataSetButton(driver);

		setValueToTextFieldByName(INTEGRATED_DATASET_TEXTFIELD, INTEGRATED_DATASET_ENTITY_NAME, driver);

		clickButonById(CREATED_INTEGRATED_DATA_CONFIRM_BUTTON, driver);

		Assert.assertEquals(getAnWebElementById(DATA_EXPLORER_ENTITY_HEADER_ELEMENT_ID, driver).getText(),
				INTEGRATED_DATASET_ENTITY_NAME);
	}

	// public void integrateSourceData() throws InterruptedException
	// {
	// clickOnCreateIntegratedDataSetButton(driver);
	//
	// setValueToTextFieldByName(INTEGRATED_DATASET_TEXTFIELD, INTEGRATED_DATASET_ENTITY_NAME, driver);
	//
	// clickButonById(CREATED_INTEGRATED_DATA_CONFIRM_BUTTON, driver);
	//
	// Assert.assertEquals(getAnWebElementById(DATA_EXPLORER_ENTITY_HEADER_ELEMENT_ID, driver).getText(),
	// INTEGRATED_DATASET_ENTITY_NAME);
	// }

	private boolean headerContainsSoucenEntity(String entityName) throws InterruptedException
	{
		return getColumnHeadersInOneMappingProject(driver).stream().anyMatch(
				column -> column.getText().contains(entityName));
	}

	private WebElement getLifeLinesColumnHeaderElement() throws InterruptedException
	{
		WebElement lifelinesColumnHeaderElement = getColumnHeadersInOneMappingProject(driver).stream()
				.filter(column -> column.getText().equals(SOURCE_LIFELINES_COLUMN_HEADER)).findFirst().orElseGet(null);
		return lifelinesColumnHeaderElement;
	}
}
