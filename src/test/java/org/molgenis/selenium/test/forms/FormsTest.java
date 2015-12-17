package org.molgenis.selenium.test.forms;

import static org.testng.Assert.assertFalse;
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

	/**
	 * Action: Click on 'edit' icon of first row.
	 * Result: Form should be visible.
	 */
	@Test
	public void testVisibilityFirstRow()
	{
		final DataModel dataModel = homepage.menu().selectDataExplorer().selectEntity("TypeTest").selectDataTab();
		final FormsModalModel model = dataModel.clickOnEditFirstRowButton();
		
		Map<String, WebElement> noncompoundAttrbutes = FormsUtils.findAttributesContainerWebElement(driver,
				model.getModalBy(),
				TESTTYPE_NONCOMPOUND_ATTRIBUTES, false);
		noncompoundAttrbutes.values().forEach(e -> assertTrue(e.isDisplayed()));
		LOG.info("Test that noncompound attributes are found: {}", TESTTYPE_NONCOMPOUND_ATTRIBUTES);

		Map<String, WebElement> compoundAttrbutes = FormsUtils.findAttributesContainerWebElement(driver,
				model.getModalBy(),
				Arrays.asList("xcompound"),
				true);
		compoundAttrbutes.values().forEach(e -> assertTrue(e.isDisplayed()));
		LOG.info("Test that compound attributes are found: {}", "xcompound");

		model.clickOnCloseButton();
	}

	/**
	 * Action: Click on 'eye' icon.
	 * Result: Nillable fields should be hidden.
	 */
	@Test
	public void testNillableFieldsShouldBeHidden()
	{
		final DataModel dataModel = homepage.menu().selectDataExplorer().selectEntity("TypeTest").selectDataTab();
		FormsModalModel model = dataModel.clickOnEditFirstRowButton();
		model = model.clickEyeButton();

		Map<String, WebElement> noncompoundNillableAttrbutes = FormsUtils.findAttributesContainerWebElement(driver,
				model.getModalBy(),
				TESTTYPE_NONCOMPOUND_NILLABLE_ATTRIBUTES, false);
		noncompoundNillableAttrbutes.values().forEach(e -> assertTrue(e.isDisplayed()));
		LOG.info("Test that noncompound and nonnillable attributes are displayed: {}",
				TESTTYPE_NONCOMPOUND_NILLABLE_ATTRIBUTES);

		assertFalse(AbstractModel.exists(super.driver, model.getModalBy(),
				FormsUtils.getAttributeContainerWebElementBy(model.getModal(), "xcompound", true)));
		LOG.info("Test that xcompound is not displayed");

		Map<String, WebElement> noncompoundNonnillableAttrbutes = FormsUtils.findAttributesContainerWebElement(driver,
				model.getModalBy(),
				TESTTYPE_NONCOMPOUND_NONNILLABLE_ATTRIBUTES, false);
		noncompoundNonnillableAttrbutes.values().forEach(e -> assertFalse(e.isDisplayed()));
		LOG.info("Test that noncompound and nillable attributes are hidden: {}",
				TESTTYPE_NONCOMPOUND_NONNILLABLE_ATTRIBUTES);

		model.clickOnCloseButton();
	}
	
	/**
	 * Action: Click 'Save changes'.
	 * Result: Form should be saved without errors and give a 'saved' message.
	 * 
	 * This test will fail because of this issue:
	 * "Save changes message in forms is not shown #4273"
	 */
	@Test
	public void testSaveChanges()
	{
		DataModel dataModel = homepage.menu().selectDataExplorer().selectEntity("TypeTest").selectDataTab();
		final FormsModalModel model = dataModel.clickOnEditFirstRowButton();
		dataModel = model.clickOnSaveChangesButton();
		assertTrue(dataModel.existAlertMessage("", ""));
		assertFalse(model.isModalFormOpen());
		LOG.info("Tested save changes button");
	}

	/**
	 * Action: Edit some values and save changes.
	 * Result: Values should be updated
	 */
	@Test
	public void testEditValuesAndSaveChanges()
	{
		DataModel dataModel = homepage.menu().selectDataExplorer().selectEntity("TypeTest").selectDataTab();
		FormsModalModel model = dataModel.clickOnEditFirstRowButton();

		FormsUtils.changeValueNoncompoundAttribute(driver, model.getModalBy(), "xbool", "false");
		FormsUtils.changeValueNoncompoundAttribute(driver, model.getModalBy(), "xboolnillable", "");
		FormsUtils.changeValueCompoundAttribute(driver, model.getModalBy(), "xcompound", "xcompound_int", "30");
		FormsUtils.changeValueCompoundAttribute(driver, model.getModalBy(), "xcompound", "xcompound_string",
				"selenium test");
		FormsUtils.changeValueNoncompoundAttribute(driver, model.getModalBy(), "xcategorical_value", "ref4");
		FormsUtils.changeValueNoncompoundAttribute(driver, model.getModalBy(), "xcategoricalnillable_value", "");
		FormsUtils.changeValueAttributeCheckbox(driver, model.getModalBy(), "xcategoricalmref_value", "ref1", "ref2");
		FormsUtils.changeValueAttributeCheckbox(driver, model.getModalBy(), "xcatmrefnillable_value", "");
		FormsUtils.changeValueNoncompoundAttribute(driver, model.getModalBy(), "xdate", "2015-12-31");
		FormsUtils.changeValueNoncompoundAttribute(driver, model.getModalBy(), "xdatenillable", "");
		FormsUtils.changeValueNoncompoundAttribute(driver, model.getModalBy(), "xdatetime", "2015-12-31T23:59:59+0100");
		FormsUtils.changeValueNoncompoundAttribute(driver, model.getModalBy(), "xdatetimenillable", "");
		FormsUtils.changeValueNoncompoundAttribute(driver, model.getModalBy(), "xdecimal", "5.55");
		FormsUtils.changeValueNoncompoundAttribute(driver, model.getModalBy(), "xdecimalnillable", "");
		FormsUtils.changeValueNoncompoundAttribute(driver, model.getModalBy(), "xemail", "molgenis@gmail.com");
		FormsUtils.changeValueNoncompoundAttribute(driver, model.getModalBy(), "xemailnillable", "");
		FormsUtils.changeValueNoncompoundAttribute(driver, model.getModalBy(), "xenum", "enum3");
		FormsUtils.changeValueNoncompoundAttribute(driver, model.getModalBy(), "xenumnillable", "");
		// model.changeValueNoncompoundAttribute("xhtml", "<h2>hello selenium testsh</h2>"); FIXME
		// model.changeValueNoncompoundAttribute("xhtmlnillable", ""); FIXME
		
		// xhyperlink
		FormsUtils.testErrorMessageInvalidValueNoncompoundAttribute(driver, model.getModalBy(), "xhyperlink",
				"www.molgenis.org");
		FormsUtils.changeValueNoncompoundAttribute(driver, model.getModalBy(), "xhyperlink",
				"http://www.seleniumhq.org/");

		FormsUtils.changeValueNoncompoundAttribute(driver, model.getModalBy(), "xhyperlinknillable", "");
		FormsUtils.changeValueNoncompoundAttribute(driver, model.getModalBy(), "xint", "5");
		FormsUtils.changeValueNoncompoundAttribute(driver, model.getModalBy(), "xintnillable", "");
		FormsUtils.changeValueNoncompoundAttribute(driver, model.getModalBy(), "xintrange", "5");
		FormsUtils.changeValueNoncompoundAttribute(driver, model.getModalBy(), "xintrangenillable", "");
		FormsUtils.changeValueNoncompoundAttribute(driver, model.getModalBy(), "xlong", "5");
		FormsUtils.changeValueNoncompoundAttribute(driver, model.getModalBy(), "xlongnillable", "");
		FormsUtils.changeValueNoncompoundAttribute(driver, model.getModalBy(), "xlongrange", "5");
		FormsUtils.changeValueNoncompoundAttribute(driver, model.getModalBy(), "xlongrangenillable", "");
		FormsUtils.changeValueAttributeSelect2(driver, model.getModalBy(), "xmref_value",
				ImmutableMap.<String, String> of("ref4", "label4", "ref5", "label5"), true, true);
		FormsUtils.changeValueAttributeSelect2(driver, model.getModalBy(), "xmrefnillable_value",
				ImmutableMap.<String, String> of("", ""), true, true);
		FormsUtils.changeValueNoncompoundAttribute(driver, model.getModalBy(), "xstring", "xstring");
		FormsUtils.changeValueNoncompoundAttribute(driver, model.getModalBy(), "xstringnillable", "");
		// FormsUtils.changeValueNoncompoundAttribute(model.getModal(), "xtext", "xtext"); // FIXME
		// FormsUtils.changeValueNoncompoundAttribute(model.getModal(), "xtextnillable", ""); // FIXME
		FormsUtils.changeValueAttributeSelect2(driver, model.getModalBy(), "xxref_value",
				ImmutableMap.<String, String> of("ref4", "label4"), false, false);

		// FIXME Clear original values for
		// nillable non multi is not supported by select2model.
		// Delete button is not visible
		FormsUtils.changeValueAttributeSelect2(driver, model.getModalBy(), "xxrefnillable_value",
				ImmutableMap.<String, String> of("ref4", "label4"), false, false);

		FormsUtils.changeValueNoncompoundAttribute(driver, model.getModalBy(), "xstring_hidden", "hidden");
		FormsUtils.changeValueNoncompoundAttribute(driver, model.getModalBy(), "xstringnillable_hidden", "");

		// FormsUtils.changeValueNoncompoundAttribute(driver, model.getModalBy(), "xcomputedxref",
		// "This value is computed automatically"); // FIXME Is not editable
		// FormsUtils.changeValueNoncompoundAttribute(driver, model.getModalBy(), "xcomputedint",
		// "This value is computed automatically"); // FIXME Is not editable

		assertFalse(FormsUtils.formHasErrors(driver, model.getModalBy()));
		model.clickOnSaveChangesButton();
		assertFalse(model.isModalFormOpen());
		LOG.info("Tested editing some values and pushing the save changes button");
	}

	/**
	 * Action: Test error messages for invalid values
	 */
	@Test
	public void testErrorMessagesInvalidValues()
	{
		DataModel dataModel = homepage.menu().selectDataExplorer().selectEntity("TypeTest").selectDataTab();
		FormsModalModel model = dataModel.clickOnEditFirstRowButton();

		// xcompound_int
		// Test change value and invalid value message
		FormsUtils.changeValueCompoundAttribute(driver, model.getModalBy(), "xcompound", "xcompound_int",
				"9999999999999");
		assertTrue(FormsUtils.formHasErrors(driver, model.getModalBy()));
		FormsUtils.changeValueCompoundAttribute(driver, model.getModalBy(), "xcompound", "xcompound_int", "30");

		// xdate
		String oXdate = FormsUtils.getValueNoncompoundAttribute(driver, model.getModalBy(), "xdate");
		FormsUtils.testOnblurAutoConvertValueNoncompoundAttribute(driver, model.getModalBy(), "xdate", oXdate + "TEST",
				oXdate);

		// xdecimal
		FormsUtils.testOnblurAutoConvertValueNoncompoundAttribute(driver, model.getModalBy(), "xdecimal", "1-1-1-1-1",
				"11111");

		// xemail
		FormsUtils.testErrorMessageInvalidValueNoncompoundAttribute(driver, model.getModalBy(), "xemail",
				"molgenisgmail.com");

		// xhyperlink
		FormsUtils.testErrorMessageInvalidValueNoncompoundAttribute(driver, model.getModalBy(), "xhyperlink",
				"www.molgenis.org");

		// xmref_value
		FormsUtils.changeValueAttributeSelect2(driver, model.getModalBy(), "xmref_value",
				ImmutableMap.<String, String> of("", ""), true, true);
		assertTrue(FormsUtils.formHasErrors(driver, model.getModalBy()));
		FormsUtils.changeValueAttributeSelect2(driver, model.getModalBy(), "xmref_value",
				ImmutableMap.<String, String> of("ref1", "label1"), true, true);
		assertFalse(FormsUtils.formHasErrors(driver, model.getModalBy()));

		// xstring_unique
		String oXstringUnique = FormsUtils.getValueNoncompoundAttribute(driver, model.getModalBy(), "xstring_unique");
		FormsUtils.testErrorMessageInvalidValueNoncompoundAttribute(driver, model.getModalBy(), "xstring_unique",
				(oXstringUnique.equals("str4") ? "str3" : "str4"));

		// xint_unique
		String oXintUnique = FormsUtils.getValueNoncompoundAttribute(driver, model.getModalBy(), "xint_unique");
		FormsUtils.testErrorMessageInvalidValueNoncompoundAttribute(driver, model.getModalBy(), "xint_unique",
				(oXintUnique.equals("2") ? "1" : "2"));

		// xxref_unique
		// Test change value and invalid value message
		String xXrefUnique = FormsUtils.getValueNoncompoundAttribute(driver, model.getModalBy(), "xxref_unique");
		if (!xXrefUnique.isEmpty())
		{
			FormsUtils.changeValueAttributeSelect2(
					driver,
					model.getModalBy(),
					"xxref_unique",
					(oXintUnique.equals("ref3") ? ImmutableMap.<String, String> of("ref4", "label4") : ImmutableMap
							.<String, String> of("ref3", "label3")), false, false);
			assertTrue(FormsUtils.formHasErrors(driver, model.getModalBy()));
			FormsUtils.changeValueAttributeSelect2(driver, model.getModalBy(), "xxref_unique",
					ImmutableMap.<String, String> of(xXrefUnique, "label" + xXrefUnique.replace("ref", "")), false,
					false);
		}

		assertFalse(FormsUtils.formHasErrors(driver, model.getModalBy()));
		model.clickOnSaveChangesButton();
		assertFalse(model.isModalFormOpen());
		LOG.info("Tested editing some values and pushing the save changes button");
	}

	/**
	 * Action: Click link 'Deselect all' link of xcategoricalmref_value. Result: All checkboxes should be unchecked.
	 */
	@Test
	public void testDeselectAll()
	{
		DataModel dataModel = homepage.menu().selectDataExplorer().selectEntity("TypeTest").selectDataTab();
		FormsModalModel model = dataModel.clickOnEditFirstRowButton();
		FormsUtils.clickDeselectAll(driver, model.getModalBy(), "xcategoricalmref_value");
		assertTrue(FormsUtils.formHasErrors(driver, model.getModalBy()));
		model.clickOnSaveChangesButton();
		assertTrue(model.isModalFormOpen());
		LOG.info("Test deselect all checkboxes xcategoricalmref_value");
	}

	/**
	 * Action: Click 'select all' link of xcategoricalmref_value. Result: All checkboxes should be checked.
	 */
	@Test
	public void testSelectAll()
	{
		DataModel dataModel = homepage.menu().selectDataExplorer().selectEntity("TypeTest").selectDataTab();
		FormsModalModel model = dataModel.clickOnEditFirstRowButton();
		FormsUtils.clickSelectAll(driver, model.getModalBy(), "xcategoricalmref_value");
		assertFalse(FormsUtils.formHasErrors(driver, model.getModalBy()));
		model.clickOnSaveChangesButton();
		assertFalse(model.isModalFormOpen());
		LOG.info("Test select all checkboxes xcategoricalmref_value");
	}

	@AfterClass
	public void afterClass() throws InterruptedException
	{
		super.token = super.restClient.login(uid, pwd).getToken();
		tryDeleteEntities("org_molgenis_test_TypeTest", "TypeTestRef", "Location", "Person");
		super.restClient.logout(token);
	}
}
