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
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
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

		model.clickOnCancelButton();
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

		model.clickOnCancelButton();
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
		assertTrue(model.isModalFormClosed());
		LOG.info("Tested save changes button");
	}

	/**
	 * Create new TypeTestRef Fill in all attributes (Use new TypeTestRef) and click 'Create'
	 */
	@Test
	public void testCreateNewTypeTestRefAndTypeTest()
	{
		DataModel dataModel = homepage.menu().selectDataExplorer().selectEntity("TypeTestRef").selectDataTab();
		FormsModalModel model = dataModel.clickOnAddRowButton();

		FormsUtils.changeValueNoncompoundAttribute(driver, model.getModalBy(), "value", "ref6");
		FormsUtils.changeValueNoncompoundAttribute(driver, model.getModalBy(), "label", "label6");

		dataModel = model.clickOnCreateButton();
		assertTrue(model.isModalFormClosed());
		LOG.info("Added a new row with values: {value:ref6, label:label6} [TypeTestRef]");

		dataModel = homepage.menu().selectDataExplorer().selectEntity("TypeTest").selectDataTab();
		model = dataModel.clickOnAddRowButton();

		// Unique
		FormsUtils.changeValueNoncompoundAttributeUnsafe(driver, model.getModalBy(), "id", "55");

		populateAllNonUniqueTestTypeAttributeValues(driver, model.getModalBy());
		
		// Unique
		FormsUtils.changeValueNoncompoundAttribute(driver, model.getModalBy(), "xstring_unique", "Unique");
		FormsUtils.changeValueNoncompoundAttribute(driver, model.getModalBy(), "xint_unique", "42");
		FormsUtils.changeValueAttributeSelect2NonMulti(driver, model.getModalBy(), "xxref_unique",
				ImmutableMap.<String, String> of("ref6", "label6"));

		// "These values are computed automatically": xcomputedint, xcomputedxref

		dataModel = model.clickOnCreateButton();

		assertTrue(model.isModalFormClosed());
		LOG.info("Added a new row with values: {...} [TypeTest]");
	}

	/**
	 * Action: Edit some values and save changes. Result: Values should be updated
	 */
	@Test
	public void testEditValuesAndSaveChanges()
	{
		DataModel dataModel = homepage.menu().selectDataExplorer().selectEntity("TypeTest").selectDataTab();
		FormsModalModel model = dataModel.clickOnEditFirstRowButton();

		populateAllNonUniqueTestTypeAttributeValues(driver, model.getModalBy());

		assertFalse(FormsUtils.formHasErrors(driver, model.getModalBy()));
		model.clickOnSaveChangesButton();
		assertTrue(model.isModalFormClosed());
		LOG.info("Tested editing some values and pushing the save changes button");
	}

	private static void populateAllNonUniqueTestTypeAttributeValues(WebDriver driver, By modalBy)
	{
		FormsUtils.changeValueNoncompoundAttributeRadio(driver, modalBy, "xbool", "true");
		FormsUtils.changeValueNoncompoundAttributeRadio(driver, modalBy, "xboolnillable", "");
		FormsUtils.changeValueCompoundAttribute(driver, modalBy, "xcompound", "xcompound_int", "55");
		FormsUtils.changeValueCompoundAttribute(driver, modalBy, "xcompound", "xcompound_string",
				"selenium test");
		FormsUtils.changeValueNoncompoundAttributeRadio(driver, modalBy, "xcategorical_value", "ref1");
		FormsUtils.changeValueNoncompoundAttributeRadio(driver, modalBy, "xcategoricalnillable_value", "");
		FormsUtils.changeValueNoncompoundAttributeCheckbox(driver, modalBy, "xcategoricalmref_value", "ref1", "ref2");
		FormsUtils.changeValueNoncompoundAttributeCheckbox(driver, modalBy, "xcatmrefnillable_value", "");
		FormsUtils.changeValueNoncompoundAttribute(driver, modalBy, "xdate", "2015-12-31");
		FormsUtils.changeValueNoncompoundAttribute(driver, modalBy, "xdatenillable", "");
		FormsUtils.changeValueNoncompoundAttribute(driver, modalBy, "xdatetime", "2015-12-31T23:59:59+0100");
		FormsUtils.changeValueNoncompoundAttribute(driver, modalBy, "xdatetimenillable", "");

		// FIXMEE: #4283 Remove value xdecimal, try to add x.xx (example: 1.00) will not be possible
		FormsUtils.changeValueNoncompoundAttribute(driver, modalBy, "xdecimal", "555");
		FormsUtils.changeValueNoncompoundAttribute(driver, modalBy, "xdecimalnillable", "");
		FormsUtils.changeValueNoncompoundAttribute(driver, modalBy, "xemail", "molgenis@gmail.com");
		FormsUtils.changeValueNoncompoundAttribute(driver, modalBy, "xemailnillable", "");
		FormsUtils.changeValueNoncompoundAttributeRadio(driver, modalBy, "xenum", "enum3");
		FormsUtils.changeValueNoncompoundAttributeRadio(driver, modalBy, "xenumnillable", "");
		FormsUtils.typeValueNoncompoundAttributeAceEditor(driver, modalBy, "xhtml",
				"<h2>hello selenium test");
		FormsUtils.typeValueNoncompoundAttributeAceEditor(driver, modalBy, "xhtmlnillable", "");
		FormsUtils.changeValueNoncompoundAttribute(driver, modalBy, "xhyperlink",
				"http://www.seleniumhq.org/");
		FormsUtils.changeValueNoncompoundAttribute(driver, modalBy, "xhyperlinknillable", "");
		FormsUtils.changeValueNoncompoundAttribute(driver, modalBy, "xint", "5");
		FormsUtils.changeValueNoncompoundAttribute(driver, modalBy, "xintnillable", "");
		FormsUtils.changeValueNoncompoundAttribute(driver, modalBy, "xintrange", "5");
		FormsUtils.changeValueNoncompoundAttribute(driver, modalBy, "xintrangenillable", "");
		FormsUtils.changeValueNoncompoundAttribute(driver, modalBy, "xlong", "5");
		FormsUtils.changeValueNoncompoundAttribute(driver, modalBy, "xlongnillable", "");
		FormsUtils.changeValueNoncompoundAttribute(driver, modalBy, "xlongrange", "5");
		FormsUtils.changeValueNoncompoundAttribute(driver, modalBy, "xlongrangenillable", "");
		FormsUtils.changeValueAttributeSelect2Multi(driver, modalBy, "xmref_value",
				ImmutableMap.<String, String> of("ref4", "label4", "ref5", "label5"), true);
		FormsUtils.changeValueAttributeSelect2Multi(driver, modalBy, "xmrefnillable_value",
				ImmutableMap.<String, String> of("", ""), true);
		FormsUtils.changeValueNoncompoundAttribute(driver, modalBy, "xstring", "xstring");
		FormsUtils.changeValueNoncompoundAttribute(driver, modalBy, "xstringnillable", "");
		FormsUtils.changeValueNoncompoundAttributeTextarea(driver, modalBy, "xtext", "xtext");
		FormsUtils.changeValueNoncompoundAttributeTextarea(driver, modalBy, "xtextnillable", "");
		FormsUtils.changeValueAttributeSelect2NonMulti(driver, modalBy, "xxref_value",
				ImmutableMap.<String, String> of("ref1", "label1"));
		FormsUtils.changeValueAttributeSelect2NonMulti(driver, modalBy, "xxrefnillable_value",
				ImmutableMap.<String, String> of("ref4", "label4"));
		FormsUtils.changeValueNoncompoundAttribute(driver, modalBy, "xstring_hidden", "hidden");
		FormsUtils.changeValueNoncompoundAttribute(driver, modalBy, "xstringnillable_hidden", "");
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
		FormsUtils.changeValueAttributeSelect2Multi(driver, model.getModalBy(), "xmref_value",
				ImmutableMap.<String, String> of("", ""), true);
		assertTrue(FormsUtils.formHasErrors(driver, model.getModalBy()));
		FormsUtils.changeValueAttributeSelect2Multi(driver, model.getModalBy(), "xmref_value",
				ImmutableMap.<String, String> of("ref1", "label1"), true);
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
			FormsUtils.changeValueAttributeSelect2NonMulti(
					driver,
					model.getModalBy(),
					"xxref_unique",
					(oXintUnique.equals("ref3") ? ImmutableMap.<String, String> of("ref4", "label4") : ImmutableMap
							.<String, String> of("ref3", "label3")));
			assertTrue(FormsUtils.formHasErrors(driver, model.getModalBy()));
			FormsUtils.changeValueAttributeSelect2NonMulti(driver, model.getModalBy(), "xxref_unique",
					ImmutableMap.<String, String> of(xXrefUnique, "label" + xXrefUnique.replace("ref", "")));
		}

		assertFalse(FormsUtils.formHasErrors(driver, model.getModalBy()));
		model.clickOnSaveChangesButton();
		assertTrue(model.isModalFormClosed());
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
		model.clickOnCancelButton();
		assertTrue(model.isModalFormClosed());
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
		assertTrue(model.isModalFormClosed());
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
