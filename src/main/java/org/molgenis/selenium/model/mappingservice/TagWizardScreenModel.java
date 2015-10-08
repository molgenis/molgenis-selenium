package org.molgenis.selenium.model.mappingservice;

import java.util.Objects;

import org.molgenis.selenium.util.TagWizardUtil;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TagWizardScreenModel
{
	private WebDriver driver;

	private static final String HEIGHT_ONTOLOGY_TERM_LABEL = "Height";
	private static final String WEIGHT_ONTOLOGY_TERM_LABEL = "Weight";
	private static final String SEARCH_TAG_INPUTFIELD_ID = "tag-dropdown";

	public static final Logger LOG = LoggerFactory.getLogger(TagWizardScreenModel.class);

	public TagWizardScreenModel(WebDriver webDriver)
	{
		this.driver = Objects.requireNonNull(webDriver);
	}

	public void tagAllBodyMassIndexAttributesManually() throws InterruptedException
	{
		TagWizardUtil.openTagWizard(driver);

		TagWizardUtil.selectEntityName(driver);

		addTagsForBodyMadIndexAttributes(9);

		addTagsForBodyMadIndexAttributes(10);

		addTagsForBodyMadIndexAttributes(11);
	}

	public void addTagsForBodyMadIndexAttributes(int index) throws InterruptedException
	{
		TagWizardUtil.clickOnCellByRowIndex(index, driver);

		TagWizardUtil.sendTextToMultiSelectionElement(SEARCH_TAG_INPUTFIELD_ID, HEIGHT_ONTOLOGY_TERM_LABEL, driver);

		TagWizardUtil.sendTextToMultiSelectionElement(SEARCH_TAG_INPUTFIELD_ID, WEIGHT_ONTOLOGY_TERM_LABEL, driver);

		TagWizardUtil.saveSearchedTags(driver);
	}
}
