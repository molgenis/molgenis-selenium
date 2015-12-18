package org.molgenis.selenium.forms;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.molgenis.selenium.model.AbstractModel;
import org.molgenis.selenium.model.component.Select2Model;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FormsUtils
{
	private static final Logger LOG = LoggerFactory.getLogger(FormsUtils.class);
	private static final String NONCOMPOUND_CONTAINER = "div";
	private static final String COMPOUND_CONTAINER = "fieldset";

	public FormsUtils(WebDriver driver)
	{
	}

	public static void changeValueNoncompoundAttribute(WebDriver driver, By context, String simpleName, String value)
	{
		WebElement attributeContainer = findAttributeContainerWebElement(driver, context, simpleName, false);
		changeValueAttributeTextNumberEmailUrl(attributeContainer, simpleName, value);
		assertEquals(getValueNoncompoundAttribute(driver, context, simpleName), value);
	}

	public static void changeValueNoncompoundAttributeUnsafe(WebDriver driver, By context, String simpleName,
			String value)
	{
		WebElement attributeContainer = findAttributeContainerWebElement(driver, context, simpleName, false);
		changeValueAttributeTextNumberEmailUrl(attributeContainer, simpleName, value);
	}

	public static void changeValueNoncompoundAttributeRadio(WebDriver driver, By context, String simpleName,
			String value)
	{
		WebElement attributeContainer = findAttributeContainerWebElement(driver, context, simpleName, false);
		attributeContainer.findElement(
				By.xpath("//input[@name='" + simpleName + "'][@type='radio'][@value='" + value + "']")).click();
		assertEquals(value, getValueNoncompoundAttributeRadio(driver, context, simpleName));
		attributeContainer.click();
	}

	public static void typeValueNoncompoundAttributeAceEditor(WebDriver driver, By context, String simpleName,
			String value)
	{
		By textareaBy = By.xpath("//textarea[@class='ace_text-input']");
		WebElement attributeContainer = findAttributeContainerWebElement(driver, context, simpleName, false);
		WebElement textarea = attributeContainer.findElement(textareaBy);
		textarea.sendKeys(value);
	}

	public static void changeValueNoncompoundAttributeTextarea(WebDriver driver, By context, String simpleName,
			String value)
	{
		By textareaBy = By.cssSelector("textarea");
		WebElement attributeContainer = findAttributeContainerWebElement(driver, context, simpleName, false);
		WebElement textarea = attributeContainer.findElement(textareaBy);
		textarea.clear();
		textarea.sendKeys(value);
	}

	public static void testErrorMessageInvalidValueNoncompoundAttribute(WebDriver driver, By context,
			String simpleName, String value)
	{
		String originalValue = FormsUtils.getValueNoncompoundAttribute(driver, context, simpleName);
		WebElement attributeContainer = findAttributeContainerWebElement(driver, context, simpleName, false);
		changeValueAttributeTextNumberEmailUrl(attributeContainer, simpleName, value);
		assertTrue(FormsUtils.formHasErrors(driver, context));
		FormsUtils.getValueNoncompoundAttribute(driver, context, simpleName);
		changeValueAttributeTextNumberEmailUrl(attributeContainer, simpleName, originalValue);
	}

	public static void testOnblurAutoConvertValueNoncompoundAttribute(WebDriver driver, By context, String simpleName,
			String value, String expected)
	{
		WebElement attributeContainer = findAttributeContainerWebElement(driver, context, simpleName, false);
		changeValueAttributeTextNumberEmailUrl(attributeContainer, simpleName, value);
		attributeContainer.click(); // Onblur
		assertFalse(FormsUtils.formHasErrors(driver, context));
		String actual = FormsUtils.getValueNoncompoundAttribute(driver, context, simpleName);
		assertEquals(actual, expected);
	}

	public static String getValueNoncompoundAttribute(WebDriver driver, By context, String simpleName)
	{
		WebElement attributeContainer = findAttributeContainerWebElement(driver, context, simpleName, false);
		return attributeContainer.findElement(By.cssSelector("\\input[name=" + simpleName + "]")).getAttribute("value");
	}

	public static String getValueNoncompoundAttributeRadio(WebDriver driver, By context, String simpleName)
	{
		WebElement attributeContainer = findAttributeContainerWebElement(driver, context, simpleName, false);
		return attributeContainer.findElement(By.cssSelector("input[name='" + simpleName + "'][type='radio']:checked"))
				.getAttribute("value");
	}

	public static void changeValueCompoundAttribute(WebDriver driver, By context, String simpleName,
			String simpleNamePartOf,
			String value)
	{
		WebElement attributeContainer = findAttributeContainerWebElement(driver, context, simpleName, true);
		changeValueAttributeTextNumberEmailUrl(attributeContainer, simpleNamePartOf, value);
	}

	public static void changeValueAttributeTextNumberEmailUrl(WebElement attributeContainer, String simpleName, String value)
	{
		WebElement inputElement = attributeContainer.findElement(By.xpath("//input[@name='" + simpleName + "']"));
		inputElement.clear();
		inputElement.sendKeys(value);
	}

	/**
	 * Change the value of a checkbox attribute
	 * 
	 * @param driver
	 * @param context
	 * @param simpleName
	 * @param values
	 */
	public static void changeValueNoncompoundAttributeCheckbox(WebDriver driver, By context, String simpleName,
			String... values)
	{
		WebElement container = findAttributeContainerWebElement(driver, context, simpleName, false);
		container.findElements(By.cssSelector("input[name='" + simpleName + "']:checked")).stream()
				.forEachOrdered(e -> e.click());
		Arrays.asList(values)
				.stream()
				.filter(e -> !"".equals(e))
				.forEach(
						e -> container.findElement(
								By.xpath("//input[@name='" + simpleName + "'][@value='" + e + "']")).click());
	}

	public static void clickDeselectAll(WebDriver driver, By context, String simpleName)
	{
		WebElement container = findAttributeContainerWebElement(driver, context, simpleName, false);
		WebElement link = container.findElement(By.xpath("//span[contains(text(), 'Deselect all')]/.."));
		link.click();
	}

	public static void clickSelectAll(WebDriver driver, By context, String simpleName)
	{
		WebElement container = findAttributeContainerWebElement(driver, context, simpleName, false);
		WebElement link = container.findElement(By.xpath("//span[contains(text(), 'Select all')]/.."));
		link.click();
	}

	/**
	 * Use this method to empty and add new values to the select2 attribute
	 * 
	 * @param simpleName
	 * @param idAndLabel
	 */
	public static void changeValueAttributeSelect2Multi(WebDriver driver, By context, String simpleName,
			Map<String, String> idAndLabel, boolean clearOriginalValues)
	{
		WebElement container = findAttributeContainerWebElement(driver, context, simpleName, false);

		Select2Model s2model = new Select2Model(driver, container.findElement(By.cssSelector(".select2-container"))
				.getAttribute("id"), true);

		if (clearOriginalValues)
		{
			s2model.clearSelection();
		}
		s2model.select(idAndLabel);
		container.click();
	}

	/**
	 * Use this method to change selection of non multi select2 attribute
	 * 
	 * @param simpleName
	 * @param idAndLabel
	 */
	public static void changeValueAttributeSelect2NonMulti(WebDriver driver, By context, String simpleName,
			Map<String, String> idAndLabel)
	{
		WebElement container = findAttributeContainerWebElement(driver, context, simpleName, false);
		Select2Model s2model = new Select2Model(driver, container.findElement(By.cssSelector(".select2-container"))
				.getAttribute("id"), false);
		s2model.select(idAndLabel);
		container.click();
	}

	public static Map<String, WebElement> findAttributesContainerWebElement(WebDriver driver, By context,
			List<String> simpleNames,
			boolean isCompoundAttribute)
	{
		Map<String, WebElement> result = new HashMap<String, WebElement>();
		simpleNames.stream().forEachOrdered(
				simpleName -> result.put(simpleName,
						FormsUtils.findAttributeContainerWebElement(driver, context, simpleName, isCompoundAttribute)
						));
		return result;
	}

	public static WebElement findAttributeContainerWebElement(WebDriver driver, By context, String simpleName,
			boolean isCompoundAttribute)
	{
		return driver.findElement(context).findElement(
				By.xpath("//" + (isCompoundAttribute ? COMPOUND_CONTAINER : NONCOMPOUND_CONTAINER)
				+ "[substring(@data-reactid, string-length(@data-reactid) - " + simpleName.length() + ") = '$"
				+ simpleName + "']"));
	}

	public static By getAttributeContainerWebElementBy(WebElement context, String simpleName,
			boolean isCompoundAttribute)
	{
		By by = By.xpath("//" + (isCompoundAttribute ? COMPOUND_CONTAINER : NONCOMPOUND_CONTAINER)
				+ "[substring(@data-reactid, string-length(@data-reactid) - " + simpleName.length() + ") = '$"
				+ simpleName + "']");
		return by;
	}

	/**
	 * an answer for the question: This form contains errors?
	 * 
	 * @param webDriver
	 *            WebDriver
	 * @param context
	 *            WebElement the context in which an element with class "has-error" can be found.
	 * @return an answer for the question: This form contains errors?
	 */
	public static boolean formHasErrors(WebDriver webDriver, By context)
	{
		return !AbstractModel.noElementFound(webDriver, null, By.cssSelector(".has-error"));
	}
}
