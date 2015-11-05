package org.molgenis.selenium.model.mappingservice;

import static org.molgenis.selenium.util.TagWizardUtil.clearAllTagsConfirmationModalAndCancel;
import static org.molgenis.selenium.util.TagWizardUtil.clearAllTagsConfirmationModalAndOK;
import static org.molgenis.selenium.util.TagWizardUtil.clickAutomaticTaggingButton;
import static org.molgenis.selenium.util.TagWizardUtil.clickOnEditTagButtonByRowIndex;
import static org.molgenis.selenium.util.TagWizardUtil.clickOnTheExistingTagBasedOnName;
import static org.molgenis.selenium.util.TagWizardUtil.getExistingTagNamesByRowIndex;
import static org.molgenis.selenium.util.TagWizardUtil.getSelectedEntityName;
import static org.molgenis.selenium.util.TagWizardUtil.openTagWizard;
import static org.molgenis.selenium.util.TagWizardUtil.saveSearchedTags;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.List;
import java.util.Objects;

import org.molgenis.selenium.model.component.MultiSelectModel;
import org.molgenis.selenium.model.component.Select2Model;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TagWizardScreenModel
{
	private WebDriver driver;

	private static final String HEIGHT_ONTOLOGY_TERM_LABEL = "Height";
	private static final String WEIGHT_ONTOLOGY_TERM_LABEL = "Weight";
	private static final String GENDER_TAG_NAME = "Gender";
	private static final String HEIGHT_TAG_NAME = "(Height and meter)";
	private static final String WEIGHT_TAG_NAME = "(Weight and gram)";
	private static final int GENDER_ROW_INDEX = 2;
	private static final int HEIGHT_ROW_INDEX = 3;
	private static final int WEIGHT_ROW_INDEX = 5;

	public static final Logger LOG = LoggerFactory.getLogger(TagWizardScreenModel.class);

	private final MultiSelectModel tagSelectionModel;

	private final MultiSelectModel ontologySelectionModel;
	
	private final MultiSelectModel entitySelectionModel;

	public TagWizardScreenModel(WebDriver webDriver)
	{
		this.driver = Objects.requireNonNull(webDriver);
		
		tagSelectionModel = new Select2Model(driver, "tag-dropdown");
		ontologySelectionModel = new Select2Model(driver, "ontology-select");
		entitySelectionModel = new Select2Model(driver, "select-target");
	}

	public void testAllTagFunctionalities() throws InterruptedException
	{
		openTagWizard(driver);

		// Select the test entity
		entitySelectionModel.select(AbstractMappingServiceAppModel.TARGET_ENTITY_NAME);

		// Clear all existing tags
		clearAllTagsConfirmationModalAndOK(driver);

		// Check if the selected entity name is same as the test one
		assertEquals(getSelectedEntityName(driver), AbstractMappingServiceAppModel.TARGET_ENTITY_NAME);

		ontologySelectionModel.clearSelection();
		ontologySelectionModel.select("uo_test", "biobank_ontology_test");
		
		// Run automatic tagging
		clickAutomaticTaggingButton(driver);

		// Check the name of the automatic tag for the Gender attribute
		checkGenderTagContent();

		// Check the name of the automatic tag for the Height attribute
		checkHeightTagContent();

		// Check the weight content after removing the weight tag
		checkWeightTagContentAfterRemovingOneTag();

		// Cancel the remove tags modal
		clearAllTagsConfirmationModalAndCancel(driver);

		// confirm the remove tags modal
		clearAllTagsConfirmationModalAndOK(driver);

		testBodyMassIndexAttributes();
	}

	public void testBodyMassIndexAttributes() throws InterruptedException
	{
		openTagWizard(driver);

		// Select the test entity
		entitySelectionModel.select(AbstractMappingServiceAppModel.TARGET_ENTITY_NAME);

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
		clickOnTheExistingTagBasedOnName(WEIGHT_ROW_INDEX, WEIGHT_TAG_NAME, driver);

		List<String> weighTagNames = getExistingTagNamesByRowIndex(WEIGHT_ROW_INDEX, driver);
		// There should be only one tag
		assertTrue(weighTagNames.size() == 0);
	}

	public void addTagsForBodyMadIndexAttributes(int rowIndex) throws InterruptedException
	{
		clickOnEditTagButtonByRowIndex(rowIndex, driver);

		tagSelectionModel.select(HEIGHT_ONTOLOGY_TERM_LABEL, WEIGHT_ONTOLOGY_TERM_LABEL);
		saveSearchedTags(driver);
	}
}
