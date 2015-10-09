package org.molgenis.selenium.model.mappingservice;

import static org.molgenis.selenium.util.TagWizardUtil.clealAllTagsConfirmationModalAndCancel;
import static org.molgenis.selenium.util.TagWizardUtil.clealAllTagsConfirmationModalAndOK;
import static org.molgenis.selenium.util.TagWizardUtil.clickAutomaticTaggingButton;
import static org.molgenis.selenium.util.TagWizardUtil.clickOnEditTagButtonByRowIndex;
import static org.molgenis.selenium.util.TagWizardUtil.clickOnTheExistingTagBasedOnName;
import static org.molgenis.selenium.util.TagWizardUtil.getExistingTagNamesByRowIndex;
import static org.molgenis.selenium.util.TagWizardUtil.getSelectedEntityName;
import static org.molgenis.selenium.util.TagWizardUtil.openTagWizard;
import static org.molgenis.selenium.util.TagWizardUtil.saveSearchedTags;
import static org.molgenis.selenium.util.TagWizardUtil.selectEntityName;
import static org.molgenis.selenium.util.TagWizardUtil.sendTextToMultiSelectionElement;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.List;
import java.util.Objects;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TagWizardScreenModel
{
	private WebDriver driver;

	private static final String HEIGHT_ONTOLOGY_TERM_LABEL = "Height";
	private static final String WEIGHT_ONTOLOGY_TERM_LABEL = "Weight";
	private static final String SEARCH_TAG_INPUTFIELD_ID = "tag-dropdown";
	private static final String GENDER_TAG_NAME = "Gender";
	private static final String HEIGHT_TAG_NAME = "(Height and meter)";
	private static final int GENDER_ROW_INDEX = 2;
	private static final int HEIGHT_ROW_INDEX = 3;
	private static final int WEIGHT_ROW_INDEX = 5;

	public static final Logger LOG = LoggerFactory.getLogger(TagWizardScreenModel.class);

	public TagWizardScreenModel(WebDriver webDriver)
	{
		this.driver = Objects.requireNonNull(webDriver);
	}

	public void tagAllBodyMassIndexAttributesManually() throws InterruptedException
	{
		openTagWizard(driver);

		// Select the test entity
		selectEntityName(driver);

		// Clear all existing tags
		clealAllTagsConfirmationModalAndOK(driver);

		// Check if the selected entity name is same as the test one
		assertEquals(getSelectedEntityName(driver), AbstractMappingServiceAppModel.TARGET_ENTITY_NAME);

		// Run automatic tagging
		clickAutomaticTaggingButton(driver);

		// Check the name of the automatic tag for the Gender attribute
		checkGenderTagContent();

		// Check the name of the automatic tag for the Height attribute
		checkHeightTagContent();

		// Check the weight content after removing the weight tag
		checkWeightTagContentAfterRemovingOneTag();

		// Cancel the remove tags modal
		clealAllTagsConfirmationModalAndCancel(driver);

		// confirm the remove tags modal
		clealAllTagsConfirmationModalAndOK(driver);

		// Manually tag body mass index 1
		addTagsForBodyMadIndexAttributes(9);

		// Manually tag body mass index 2
		addTagsForBodyMadIndexAttributes(10);

		// Manually tag body mass index 3
		addTagsForBodyMadIndexAttributes(11);
	}

	private void checkGenderTagContent() throws InterruptedException
	{
		List<String> genderTagNames = getExistingTagNamesByRowIndex(GENDER_ROW_INDEX, driver);
		// There should be only one tag
		assertTrue(genderTagNames.size() == 1);
		// The tag name should be gender
		assertEquals(genderTagNames.get(0), GENDER_TAG_NAME);
	}

	private void checkHeightTagContent() throws InterruptedException
	{
		List<String> heightTagNames = getExistingTagNamesByRowIndex(HEIGHT_ROW_INDEX, driver);
		// There should be only one tag
		assertTrue(heightTagNames.size() == 1);
		// The tag name should be height
		assertEquals(heightTagNames.get(0), HEIGHT_TAG_NAME);
	}

	private void checkWeightTagContentAfterRemovingOneTag() throws InterruptedException
	{
		// Remove the Weight tag from the Weight attribute
		clickOnTheExistingTagBasedOnName(WEIGHT_ROW_INDEX, WEIGHT_ONTOLOGY_TERM_LABEL, driver);

		List<String> weighTagNames = getExistingTagNamesByRowIndex(WEIGHT_ROW_INDEX, driver);
		// There should be only one tag
		assertTrue(weighTagNames.size() == 0);
	}

	public void addTagsForBodyMadIndexAttributes(int rowIndex) throws InterruptedException
	{
		clickOnEditTagButtonByRowIndex(rowIndex, driver);

		sendTextToMultiSelectionElement(SEARCH_TAG_INPUTFIELD_ID, HEIGHT_ONTOLOGY_TERM_LABEL, driver);

		sendTextToMultiSelectionElement(SEARCH_TAG_INPUTFIELD_ID, WEIGHT_ONTOLOGY_TERM_LABEL, driver);

		saveSearchedTags(driver);
	}
}
