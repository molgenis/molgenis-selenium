package org.molgenis.selenium.test.forms;

import static org.testng.Assert.assertFalse;
//import static org.testng.Assert.assertEquals;
//import static org.testng.Assert.assertFalse;
//import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.molgenis.rest.model.SettingsModel;
import org.molgenis.selenium.forms.FormsModalModel;
import org.molgenis.selenium.forms.FormsUtils;
import org.molgenis.selenium.model.AbstractModel;
import org.molgenis.selenium.model.dataexplorer.data.DataModel;
import org.molgenis.selenium.test.AbstractSeleniumTest;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
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
		super.token = super.restClient.login(uid, pwd).getToken();
		tryDeleteEntities("org_molgenis_test_TypeTest", "TypeTestRef", "Location", "Person");
		new SettingsModel(super.restClient, super.token).updateDataExplorerSettings("mod_data", true);
		super.restClient.logout(token);
		super.importEMXFiles("org/molgenis/selenium/emx/xlsx/emx_all_datatypes.xlsx");
	}

	@Test
	/**
	 * Action: Click on 'edit' icon of first row.
	 * Result: Form should be visible.
	 */
	public void testVisibilityFirstRow()
	{
		final DataModel dataModel = homepage.menu().selectDataExplorer().selectEntity("TypeTest").selectDataTab();
		final FormsModalModel model = dataModel.clickOnEditFirstRowButton();
		
		Map<String, WebElement> noncompoundAttrbutes = FormsUtils.findAttributesContainerWebElement(model.getModal(),
				TESTTYPE_NONCOMPOUND_ATTRIBUTES, false);
		noncompoundAttrbutes.values().forEach(e -> assertTrue(e.isDisplayed()));
		LOG.info("Test that noncompound attributes are found: {}", TESTTYPE_NONCOMPOUND_ATTRIBUTES);

		Map<String, WebElement> compoundAttrbutes = FormsUtils.findAttributesContainerWebElement(model.getModal(),
				Arrays.asList("xcompound"),
				true);
		compoundAttrbutes.values().forEach(e -> assertTrue(e.isDisplayed()));
		LOG.info("Test that compound attributes are found: {}", "xcompound");

		model.clickOnCloseButton();
	}

	@Test
	/**
	 * Action: Click on 'eye' icon.
	 * Result: Nillable fields should be hidden.
	 */
	public void testNillableFieldsShouldBeHidden()
	{
		final DataModel dataModel = homepage.menu().selectDataExplorer().selectEntity("TypeTest").selectDataTab();
		FormsModalModel model = dataModel.clickOnEditFirstRowButton();
		model = model.clickEyeButton();

		Map<String, WebElement> noncompoundNillableAttrbutes = FormsUtils.findAttributesContainerWebElement(model.getModal(),
				TESTTYPE_NONCOMPOUND_NILLABLE_ATTRIBUTES, false);
		noncompoundNillableAttrbutes.values().forEach(e -> assertTrue(e.isDisplayed()));
		LOG.info("Test that noncompound and nonnillable attributes are displayed: {}",
				TESTTYPE_NONCOMPOUND_NILLABLE_ATTRIBUTES);

		assertFalse(AbstractModel.exists(super.driver, model.getModal(),
				FormsUtils.getAttributeContainerWebElementBy(model.getModal(), "xcompound", true)));
		LOG.info("Test that xcompound is not displayed");

		Map<String, WebElement> noncompoundNonnillableAttrbutes = FormsUtils.findAttributesContainerWebElement(
				model.getModal(),
				TESTTYPE_NONCOMPOUND_NONNILLABLE_ATTRIBUTES, false);
		noncompoundNonnillableAttrbutes.values().forEach(e -> assertFalse(e.isDisplayed()));
		LOG.info("Test that noncompound and nillable attributes are hidden: {}",
				TESTTYPE_NONCOMPOUND_NONNILLABLE_ATTRIBUTES);

		model.clickOnCloseButton();
	}
	

	@Test
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
		final FormsModalModel model = dataModel.clickOnEditFirstRowButton();
		dataModel = model.clickOnSaveChangesButton();
		assertTrue(dataModel.existAlertMessage("", ""));
		assertFalse(model.isModalFormOpen());
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
		FormsModalModel model = dataModel.clickOnEditFirstRowButton();

		FormsUtils.changeValueNoncompoundAttribute(model.getModal(), "xbool", "false"); // No
		FormsUtils.changeValueNoncompoundAttribute(model.getModal(), "xboolnillable", ""); // N/A
		FormsUtils.changeValueCompoundAttribute(model.getModal(), "xcompound", "xcompound_int", "30");
		FormsUtils.changeValueCompoundAttribute(model.getModal(), "xcompound", "xcompound_string", "selenium test");
		FormsUtils.changeValueNoncompoundAttribute(model.getModal(), "xcategorical_value", "ref4"); // label4
		FormsUtils.changeValueNoncompoundAttribute(model.getModal(), "xcategoricalnillable_value", ""); // N/A
		FormsUtils.changeValueAttributeCheckbox(model.getModal(), "xcategoricalmref_value", "ref1", "ref2"); // label1,
																												// label2
		FormsUtils.changeValueAttributeCheckbox(model.getModal(), "xcatmrefnillable_value", ""); // label1, lzabel2
		FormsUtils.changeValueNoncompoundAttribute(model.getModal(), "xdate", "2015-12-31");
		FormsUtils.changeValueNoncompoundAttribute(model.getModal(), "xdatenillable", "");
		FormsUtils.changeValueNoncompoundAttribute(model.getModal(), "xdatetime", "2015-12-31T23:59:59+0100");
		FormsUtils.changeValueNoncompoundAttribute(model.getModal(), "xdatetimenillable", "");
		FormsUtils.changeValueNoncompoundAttribute(model.getModal(), "xdecimal", "5.55");
		FormsUtils.changeValueNoncompoundAttribute(model.getModal(), "xdecimalnillable", "");
		FormsUtils.changeValueNoncompoundAttribute(model.getModal(), "xemail", "molgenis@gmail.com");
		FormsUtils.changeValueNoncompoundAttribute(model.getModal(), "xemailnillable", "");
		FormsUtils.changeValueNoncompoundAttribute(model.getModal(), "xenum", "enum3");
		FormsUtils.changeValueNoncompoundAttribute(model.getModal(), "xenumnillable", "");
		// model.changeValueNoncompoundAttribute("xhtml", "<h2>hello selenium testsh</h2>"); FIXME
		// model.changeValueNoncompoundAttribute("xhtmlnillable", ""); FIXME
		FormsUtils.changeValueNoncompoundAttribute(model.getModal(), "xhyperlink", "http://www.seleniumhq.org/");
		FormsUtils.changeValueNoncompoundAttribute(model.getModal(), "xhyperlinknillable", "");
		FormsUtils.changeValueNoncompoundAttribute(model.getModal(), "xint", "5");
		FormsUtils.changeValueNoncompoundAttribute(model.getModal(), "xintnillable", "");
		FormsUtils.changeValueNoncompoundAttribute(model.getModal(), "xintrange", "5");
		FormsUtils.changeValueNoncompoundAttribute(model.getModal(), "xintrangenillable", "");
		FormsUtils.changeValueNoncompoundAttribute(model.getModal(), "xlong", "5");
		FormsUtils.changeValueNoncompoundAttribute(model.getModal(), "xlongnillable", "");
		FormsUtils.changeValueNoncompoundAttribute(model.getModal(), "xlongrange", "5");
		FormsUtils.changeValueNoncompoundAttribute(model.getModal(), "xlongrangenillable", "");
		FormsUtils.changeValueAttributeSelect2(super.driver, model.getModal(), "xmref_value",
				ImmutableMap.<String, String> of("ref4", "label4", "ref5", "label5"), true, true);
		FormsUtils.changeValueAttributeSelect2(super.driver, model.getModal(), "xmrefnillable_value",
				ImmutableMap.<String, String> of("", ""), true, true);
		FormsUtils.changeValueNoncompoundAttribute(model.getModal(), "xstring", "xstring");
		FormsUtils.changeValueNoncompoundAttribute(model.getModal(), "xstringnillable", "");
		// FormsUtils.changeValueNoncompoundAttribute(model.getModal(), "xtext", "xtext"); FIXME
		// FormsUtils.changeValueNoncompoundAttribute(model.getModal(), "xtextnillable", ""); FIXME
		FormsUtils.changeValueAttributeSelect2(super.driver, model.getModal(), "xxref_value",
				ImmutableMap.<String, String> of("ref4", "label4"), false, false);
		FormsUtils.changeValueAttributeSelect2(super.driver, model.getModal(), "xxrefnillable_value",
				ImmutableMap.<String, String> of("ref4", "label4"), false, false); // Clear original values for nillable
																					// is not supported. FIXME

		assertFalse(FormsUtils.formHasErrors(super.driver, model.getModal()));

		model.clickOnSaveChangesButton();

		assertFalse(model.isModalFormOpen());
		LOG.info("Tested editing some values and pushing the save changes button");
	}

	@AfterClass
	public void afterClass() throws InterruptedException
	{
		super.token = super.restClient.login(uid, pwd).getToken();
		tryDeleteEntities("org_molgenis_test_TypeTest", "TypeTestRef", "Location", "Person");
		super.restClient.logout(token);
	}
}
