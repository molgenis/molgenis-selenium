package org.molgenis.selenium.model.forms;

import static org.openqa.selenium.support.ui.ExpectedConditions.textToBePresentInElementValue;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.molgenis.selenium.model.AbstractModel;
import org.molgenis.selenium.model.component.Select2Model;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;

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
		changeValueAttributeTextNumberEmailUrl(driver, attributeContainer, simpleName, value);
		assertEquals(getValueNoncompoundAttribute(driver, context, simpleName), value);
	}

	public static void changeValueNoncompoundAttributeUnsafe(WebDriver driver, By context, String simpleName,
			String value)
	{
		WebElement attributeContainer = findAttributeContainerWebElement(driver, context, simpleName, false);
		changeValueAttributeTextNumberEmailUrl(driver, attributeContainer, simpleName, value);
	}

	public static void changeValueNoncompoundAttributeRadio(WebDriver driver, By context, String simpleName,
			String value)
	{
		WebElement attributeContainer = findAttributeContainerWebElement(driver, context, simpleName, false);
		attributeContainer
				.findElement(By.xpath("//input[@name='" + simpleName + "'][@type='radio'][@value='" + value + "']"))
				.click();
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
		new WebDriverWait(driver, 1).until((Predicate<WebDriver>)d-> textarea.getAttribute("value").isEmpty());
		textarea.sendKeys(value);
	}

	public static void testErrorMessageInvalidValueNoncompoundAttribute(WebDriver driver, By context, String simpleName,
			String value)
	{
		String originalValue = FormsUtils.getValueNoncompoundAttribute(driver, context, simpleName);
		WebElement attributeContainer = findAttributeContainerWebElement(driver, context, simpleName, false);
		changeValueAttributeTextNumberEmailUrl(driver, attributeContainer, simpleName, value);
		FormsUtils.waitForErrors(driver, context);
		FormsUtils.getValueNoncompoundAttribute(driver, context, simpleName);
		changeValueAttributeTextNumberEmailUrl(driver, attributeContainer, simpleName, originalValue);
	}

	public static void testOnblurAutoConvertValueNoncompoundAttribute(WebDriver driver, By context, String simpleName,
			String value, String expected)
	{
		WebElement attributeContainer = findAttributeContainerWebElement(driver, context, simpleName, false);
		changeValueAttributeTextNumberEmailUrl(driver, attributeContainer, simpleName, value);
		attributeContainer.click(); // Onblur
		assertFalse(FormsUtils.formHasErrors(driver, context));
		new WebDriverWait(driver, AbstractModel.IMPLICIT_WAIT_SECONDS)
			.until((Predicate<WebDriver>) d -> expected.equals(getValueNoncompoundAttribute(driver, context, simpleName)));
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
			String simpleNamePartOf, String value)
	{
		WebElement attributeContainer = findAttributeContainerWebElement(driver, context, simpleName, true);
		changeValueAttributeTextNumberEmailUrl(driver, attributeContainer, simpleNamePartOf, value);
	}

	public static void changeValueAttributeTextNumberEmailUrl(WebDriver driver, WebElement attributeContainer,
			String simpleName, String value)
	{
		LOG.info("Change value of {} to {}...", simpleName, value);
		WebElement inputElement = attributeContainer.findElement(By.xpath("//input[@name='" + simpleName + "']"));
		for (int i = 0; i < 10; i++)
		{
			inputElement.clear();
			inputElement.sendKeys(value);
			try
			{
				WebDriverWait wdw = new WebDriverWait(driver, 1);
				wdw.until(textToBePresentInElementValue(inputElement, value));
				return;
			}
			catch (Exception ex)
			{
				LOG.warn("Attempt {} to change value failed, value is {}", i, inputElement.getAttribute("value"));
			}
		}
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
		LOG.info("Change value of checkboxes {} to {}...", simpleName, values);
		WebElement container = findAttributeContainerWebElement(driver, context, simpleName, false);
		By checkedBoxesSelector = By.cssSelector("input[name='" + simpleName + "']:checked");
		if(!AbstractModel.noElementFound(driver, context, checkedBoxesSelector)){
			LOG.info("Deselect selected boxes...");
			container.findElements(checkedBoxesSelector).stream()
					.forEachOrdered(e -> e.click());
		}
		LOG.info("Select boxes to select...");
		Arrays.asList(values).stream().filter(e -> !"".equals(e)).forEach(e -> container
				.findElement(By.xpath("//input[@name='" + simpleName + "'][@value='" + e + "']")).click());
	}

	public static void clickDeselectAll(WebDriver driver, By context, String simpleName)
	{
		LOG.info("Click [Deselect all]...");
		WebElement container = findAttributeContainerWebElement(driver, context, simpleName, false);
		WebElement link = container.findElement(By.xpath("//span[contains(text(), 'Deselect all')]/.."));
		link.click();
	}

	public static void clickSelectAll(WebDriver driver, By context, String simpleName)
	{
		LOG.info("Click [Select all]...");
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

		Select2Model s2model = new Select2Model(driver,
				container.findElement(By.cssSelector(".select2-container")).getAttribute("id"), true);

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
		Select2Model s2model = new Select2Model(driver,
				container.findElement(By.cssSelector(".select2-container")).getAttribute("id"), false);
		s2model.select(idAndLabel);
		container.click();
	}

	public static Map<String, WebElement> findAttributesContainerWebElement(WebDriver driver, By context,
			List<String> simpleNames, boolean isCompoundAttribute)
	{
		Map<String, WebElement> result = new HashMap<String, WebElement>();
		simpleNames.stream().forEachOrdered(simpleName -> result.put(simpleName,
				FormsUtils.findAttributeContainerWebElement(driver, context, simpleName, isCompoundAttribute)));
		return result;
	}

	public static WebElement findAttributeContainerWebElement(WebDriver driver, By context, String simpleName,
			boolean isCompoundAttribute)
	{
		return driver.findElement(context)
				.findElement(By.xpath("//" + (isCompoundAttribute ? COMPOUND_CONTAINER : NONCOMPOUND_CONTAINER)
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
		return !AbstractModel.noElementFound(webDriver, context, By.cssSelector(".has-error"));
	}

	public static void waitForErrors(WebDriver driver, By context)
	{
		new WebDriverWait(driver, AbstractModel.IMPLICIT_WAIT_SECONDS)
				.until(visibilityOfElementLocated(By.cssSelector(".has-error")));
	}
}
