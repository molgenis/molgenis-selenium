package org.molgenis.selenium.model.mappingservice;

import static org.molgenis.selenium.util.MappingServiceUtil.clickButonByElementId;
import static org.molgenis.selenium.util.MappingServiceUtil.clickCancelButonInAddNewMappingProjectModal;
import static org.molgenis.selenium.util.MappingServiceUtil.clickGoBackButtonToMappingProjectOverView;
import static org.molgenis.selenium.util.MappingServiceUtil.clickOKButonInConfirmationModal;
import static org.molgenis.selenium.util.MappingServiceUtil.clickRemoveMappingProjectButtonForCurrentRowElement;
import static org.molgenis.selenium.util.MappingServiceUtil.clickToOpenOneMappingProject;
import static org.molgenis.selenium.util.MappingServiceUtil.getFieldRequiredMessageFromCreateMappingProjectModal;
import static org.molgenis.selenium.util.MappingServiceUtil.getRowWebElementsFromMappingProjectTable;
import static org.molgenis.selenium.util.MappingServiceUtil.getTestMappingProjectFromTable;
import static org.molgenis.selenium.util.MappingServiceUtil.openMappingService;
import static org.molgenis.selenium.util.MappingServiceUtil.setValueToTextFieldByName;

import org.molgenis.data.rest.client.MolgenisClient;
import org.molgenis.selenium.util.Select2Util;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

public class MappingProjectOverviewModel extends AbstractMappingServiceAppModel
{
	private static final String MAPPING_PROJECT_TEXT_FIELD_NAME = "mapping-project-name";
	private static final String THIS_FIELD_IS_REQUIRED_MESSAGE = "This field is required.";
	private static final String CREATE_NEW_MAPPING_PROJECT_MODAL = "create-new-mapping-project-modal";
	private static final String ADD_NEW_MAPPING_PROJECT_BUTTON = "submit-new-source-column-btn";
	private static final String SUBMIT_NEW_MAPPING_PROJECT_CONFIRMATION_BUTTON = "submit-new-mapping-project-btn";
	public static final Logger LOG = LoggerFactory.getLogger(MappingProjectOverviewModel.class);

	public MappingProjectOverviewModel(WebDriver driver, MolgenisClient molgenisClient)
	{
		super(driver, molgenisClient);
	}

	public void openOneMappingProject() throws InterruptedException
	{
		clickToOpenOneMappingProject(MAPPING_PROJECT_NAME, driver);
	}

	public void cancelAddMappingProject() throws InterruptedException
	{
		openMappingService(driver);

		int previousNumberOfRows = getRowWebElementsFromMappingProjectTable(driver).size();

		clickButonByElementId(ADD_NEW_MAPPING_PROJECT_BUTTON, driver);

		clickCancelButonInAddNewMappingProjectModal(driver);

		int rowCount = getRowWebElementsFromMappingProjectTable(driver).size();

		Assert.assertEquals(rowCount, previousNumberOfRows);
	}

	public void addOneMappingProjectWithoutName() throws InterruptedException
	{
		openMappingService(driver);

		clickButonByElementId(ADD_NEW_MAPPING_PROJECT_BUTTON, driver);

		clickButonByElementId(SUBMIT_NEW_MAPPING_PROJECT_CONFIRMATION_BUTTON, driver);

		Assert.assertEquals(getFieldRequiredMessageFromCreateMappingProjectModal(driver),
				THIS_FIELD_IS_REQUIRED_MESSAGE);
	}

	public void addOneMappingProject() throws InterruptedException
	{
		openMappingService(driver);

		clickButonByElementId(ADD_NEW_MAPPING_PROJECT_BUTTON, driver);

		setValueToTextFieldByName(MAPPING_PROJECT_TEXT_FIELD_NAME, MAPPING_PROJECT_NAME, driver);

		Assert.assertTrue(driver instanceof JavascriptExecutor);

		Select2Util.select(CREATE_NEW_MAPPING_PROJECT_MODAL, TARGET_ENTITY_NAME, driver, LOG);

		clickButonByElementId(SUBMIT_NEW_MAPPING_PROJECT_CONFIRMATION_BUTTON, driver);

		clickGoBackButtonToMappingProjectOverView(driver);
	}

	public void removeTestMappingProject() throws InterruptedException
	{
		openMappingService(driver);

		WebElement webElement = getTestMappingProjectFromTable(driver);

		Assert.assertTrue(webElement != null);

		clickRemoveMappingProjectButtonForCurrentRowElement(webElement, driver);

		clickOKButonInConfirmationModal(driver);
	}
}
