package org.molgenis.selenium.test.forms;

import static org.testng.Assert.assertFalse;
//import static org.testng.Assert.assertEquals;
//import static org.testng.Assert.assertFalse;
//import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.molgenis.selenium.forms.FormsModel;
import org.molgenis.selenium.model.dataexplorer.data.DataModel;
import org.molgenis.selenium.test.AbstractSeleniumTest;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

public class FormsTest extends AbstractSeleniumTest
{
	private static final Logger LOG = LoggerFactory.getLogger(FormsTest.class);

	/**
	 * A list of all non compound attributes. Removed from this list: "xcompound", xcompound_int", "xcompound_string"
	 */
	private static final List<String> TESTTYPE_NONCOMPOUND_ATTRIBUTES = Lists.<String> newArrayList("id", "xbool",
			"xboolnillable", "xcategorical_value", "xcategoricalnillable_value", "xcategoricalmref_value",
			"xcatmrefnillable_value", "xdate", "xdatenillable", "xdatetime", "xdatetimenillable", "xdecimal",
			"xdecimalnillable", "xemail", "xemailnillable", "xenum", "xenumnillable",
			"xhtml", "xhtmlnillable", "xhyperlink", "xhyperlinknillable", "xint", "xintnillable", "xintrange",
			"xintrangenillable", "xlong", "xlongnillable", "xlongrange", "xlongrangenillable", "xmref_value",
			"xmrefnillable_value", "xstring", "xstringnillable", "xtext", "xtextnillable", "xxref_value",
			"xxrefnillable_value", "xstring_hidden", "xstringnillable_hidden", "xstring_unique", "xint_unique",
			"xxref_unique", "xcomputedxref", "xcomputedint");

	/**
	 * A list of all <b>nillable</b> noncompound attributes. Removed from this list: "xcompound"
	 */
	private static final List<String> TESTTYPE_NONCOMPOUND_NILLABLE_ATTRIBUTES = Lists.<String> newArrayList("id",
			"xbool",
			"xcategorical_value", "xcategoricalmref_value", "xdate", "xdatetime", "xdecimal", "xemail", "xenum",
			"xhtml", "xhyperlink", "xint", "xintrange", "xlong", "xlongrange", "xmref_value", "xstring", "xtext",
			"xxref_value", "xstring_hidden", "xstring_unique", "xint_unique");

	/**
	 * A list of all nonnillable attributes
	 * 
	 * Removed from this list: "xcompound_int", "xcompound_string"
	 */
	private static final List<String> TESTTYPE_NONCOMPOUND_NONNILLABLE_ATTRIBUTES = Lists.<String> newArrayList(
			"xboolnillable",
			"xcategoricalnillable_value", "xcatmrefnillable_value",
			"xdatenillable", "xdatetimenillable", "xdecimalnillable", "xemailnillable", "xenumnillable",
			"xhtmlnillable", "xhyperlinknillable", "xintnillable", "xintrangenillable", "xlongnillable",
			"xlongrangenillable", "xmrefnillable_value", "xstringnillable", "xtextnillable", "xxrefnillable_value",
			"xstringnillable_hidden", "xxref_unique", "xcomputedxref", "xcomputedint");

	@BeforeClass
	public void beforeClass() throws InterruptedException
	{
		// super.token = super.restClient.login(uid, pwd).getToken();
		// // tryDeleteEntities("org_molgenis_test_TypeTest", "TypeTestRef", "Location", "Person");
		// new SettingsModel(super.restClient, super.token).updateDataExplorerSettings("mod_data", true);
		// super.restClient.logout(token);
		// super.importEMXFiles("org/molgenis/selenium/emx/xlsx/emx_all_datatypes.xlsx");
	}

	// @Test
	/**
	 * Action: Click on 'edit' icon of first row.
	 * Result: Form should be visible.
	 * 
	 * TODO test if the values are correct
	 * TODO test if the labels are correct
	 */
	public void testVisibilityFirstRow()
	{
		final DataModel dataModel = homepage.menu().selectDataExplorer().selectEntity("TypeTest").selectDataTab();
		final FormsModel model = dataModel.clickOnEditFirstRowButton();
		
		// TODO test values
		// assertEquals(model.getIdLabel().getText(), "id *");
		// assertEquals(model.getIdInput().getAttribute("value"), "1");
		
		Map<String, WebElement> noncompoundAttrbutes = model.findAttributesContainerWebElement(
				TESTTYPE_NONCOMPOUND_ATTRIBUTES, false);
		noncompoundAttrbutes.values().forEach(e -> assertTrue(e.isDisplayed()));
		LOG.info("Test that noncompound attributes are found: {}", TESTTYPE_NONCOMPOUND_ATTRIBUTES);

		Map<String, WebElement> compoundAttrbutes = model.findAttributesContainerWebElement(Arrays.asList("xcompound"),
				true);
		compoundAttrbutes.values().forEach(e -> assertTrue(e.isDisplayed()));
		LOG.info("Test that compound attributes are found: {}", "xcompound");

		model.clickOnCloseButton();
	}

	// @Test
	/**
	 * Action: Click on 'eye' icon.
	 * Result: Nillable fields should be hidden.
	 */
	public void testNillableFieldsShouldBeHidden()
	{
		final DataModel dataModel = homepage.menu().selectDataExplorer().selectEntity("TypeTest").selectDataTab();
		FormsModel model = dataModel.clickOnEditFirstRowButton();
		model = model.clickEyeButton();

		Map<String, WebElement> noncompoundNillableAttrbutes = model.findAttributesContainerWebElement(
				TESTTYPE_NONCOMPOUND_NILLABLE_ATTRIBUTES, false);
		noncompoundNillableAttrbutes.values().forEach(e -> assertTrue(e.isDisplayed()));
		LOG.info("Test that noncompound and nonnillable attributes are displayed: {}",
				TESTTYPE_NONCOMPOUND_NILLABLE_ATTRIBUTES);

		assertFalse(model.exists("xcompound", true));
		LOG.info("Test that xcompound is not displayed");

		Map<String, WebElement> noncompoundNonnillableAttrbutes = model.findAttributesContainerWebElement(
				TESTTYPE_NONCOMPOUND_NONNILLABLE_ATTRIBUTES, false);
		noncompoundNonnillableAttrbutes.values().forEach(e -> assertFalse(e.isDisplayed()));
		LOG.info("Test that noncompound and nillable attributes are hidden: {}",
				TESTTYPE_NONCOMPOUND_NONNILLABLE_ATTRIBUTES);

		model.clickOnCloseButton();
	}
	

	// @Test
	/**
	 * Action: Click 'Save changes'.
	 * Result: Form should be saved without errors and give a 'saved' message.
	 * 
	 * This test will fail because of this issue:
	 * "Save changes message in forms is not shown #4273"
	 */
	public void testSaveChanges()
	{
		DataModel dataModel = homepage.menu().selectDataExplorer().selectEntity("TypeTest").selectDataTab();
		final FormsModel model = dataModel.clickOnEditFirstRowButton();
		dataModel = model.clickOnSaveChangesButton();
		assertTrue(dataModel.existAlertMessage("", ""));
		assertFalse(model.isFormOpen());
		LOG.info("Tested save changes button");
	}

	@Test
	/**
	 * Action: Edit some values and save changes.
	 * Result: Values should be updated
	 */
	public void testEditValuesAndSaveChanges()
	{
		DataModel dataModel = homepage.menu().selectDataExplorer().selectEntity("TypeTest").selectDataTab();
		final FormsModel model = dataModel.clickOnEditFirstRowButton();

		model.changeValueNoncompoundAttribute("xbool", "false"); // No
		model.changeValueNoncompoundAttribute("xboolnillable", ""); // N/A
		model.changeValueCompoundAttribute("xcompound", "xcompound_int", "30");
		model.changeValueCompoundAttribute("xcompound", "xcompound_string", "selenium test");
		model.changeValueNoncompoundAttribute("xcategorical_value", "ref4"); // label4
		model.changeValueNoncompoundAttribute("xcategoricalnillable_value", ""); // N/A
		model.changeValueNoncompoundAttribute("xcategoricalmref_value", "ref1", "ref2"); // label1, label2
		model.changeValueNoncompoundAttribute("xcatmrefnillable_value", ""); // label1, lzabel2
		model.changeValueNoncompoundAttribute("xdate", "2015-12-31");
		model.changeValueNoncompoundAttribute("xdatenillable", "");

		// TODO change more values
		// TODO test changed results in table

		assertFalse(model.formHasErrors());

		model.clickOnSaveChangesButton();

		assertFalse(model.isFormOpen());
		LOG.info("Tested editing some values and pushing the save changes button");
	}

	@AfterClass
	public void afterClass() throws InterruptedException
	{
		// super.token = super.restClient.login(uid, pwd).getToken();
		// tryDeleteEntities("org_molgenis_test_TypeTest", "TypeTestRef", "Location", "Person");
		// super.restClient.logout(token);
	}
}
