package org.molgenis.selenium.model.forms;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.molgenis.selenium.model.AbstractModel;
import org.molgenis.selenium.model.component.Select2Model;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
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

	public static void changeValueNoncompoundAttributeUnsafe(WebDriver driver, By context, String simpleName,
			String value)
	{
		WebElement input = driver.findElement(context).findElement(findAttributeInputBy(simpleName, false));

		input.clear();
		input.sendKeys(value);
	}

	public static void changeValueNoncompoundAttribute(WebDriver driver, By context, String simpleName, String value)
	{
		int count = 0;
		while (count < 3)
		{
			try
			{
				WebDriverWait wait = new WebDriverWait(driver, 5);
				changeValueNoncompoundAttributeUnsafe(driver, context, simpleName, value);
				wait.until(ExpectedConditions.textToBePresentInElementValue(findAttributeInputBy(simpleName, false),
						value));
				break;
			}
			catch (TimeoutException te)
			{
				count++;
			}
		}
	}

	public static void sendKeysNoncompoundAttributeUnsafe(WebDriver driver, By context, String simpleName, String value)
	{
		driver.findElement(context).findElement(findAttributeInputBy(simpleName, false)).sendKeys(value);
	}

	public static void changeValueNoncompoundAttributeRadio(WebDriver driver, By context, String simpleName,
			String value)
	{
		String xpathContext = createXPathAttributeContainerWebElement(simpleName, false);
		LOG.info("Click on a radio element of attribute {} with value: '{}'...", simpleName, value);
		driver.findElement(
				By.xpath(xpathContext + "//input[@name='" + simpleName + "'][@type='radio'][@value='" + value + "']"))
				.click();
		new WebDriverWait(driver, AbstractModel.IMPLICIT_WAIT_SECONDS).until((Predicate<WebDriver>) d -> value
				.equals(getValueNoncompoundAttributeRadio(driver, context, simpleName)));
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

	public static String getValueNoncompoundAttribute(WebDriver driver, By context, String simpleName)
	{
		return driver.findElement(findAttributeInputBy(simpleName, false)).getAttribute("value");
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
		WebElement inputElement = attributeContainer.findElement(By.xpath("//input[@name='" + simpleNamePartOf + "']"));
		inputElement.clear();
		inputElement.sendKeys(value);
	}

	/**
	 * Focus on element
	 */
	public static void focusOnElement(WebDriver driver, By context, String simpleName)
	{
		WebElement attributeContainer = findAttributeContainerWebElement(driver, context, simpleName, false);
		WebElement inputElement = attributeContainer.findElement(By.xpath("//input[@name='" + simpleName + "']"));
		new Actions(driver).moveToElement(inputElement).perform();
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
		Arrays.asList(values).stream().filter(e -> !"".equals(e)).forEach(e -> container
				.findElement(By.xpath("//input[@name='" + simpleName + "'][@value='" + e + "']")).click());
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
		Select2Model s2model = new Select2Model(driver,
				container.findElement(By.cssSelector(".select2-container")).getAttribute("id"), true);

		if (clearOriginalValues)
		{
			s2model.clearSelection();
		}
		s2model.selectReactForms(idAndLabel);
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
		s2model.selectReactForms(idAndLabel);
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
				.findElement(By.xpath(createXPathAttributeContainerWebElement(simpleName, isCompoundAttribute)));
	}

	public static String createXPathAttributeContainerWebElement(String simpleName, boolean isCompoundAttribute)
	{
		return "//" + (isCompoundAttribute ? COMPOUND_CONTAINER : NONCOMPOUND_CONTAINER)
				+ "[substring(@data-reactid, string-length(@data-reactid) - " + simpleName.length() + ") = '$"
				+ simpleName + "']";
	}

	public static By findAttributeInputBy(String simpleName, boolean isCompoundAttribute)
	{
		return By.xpath("//" + (isCompoundAttribute ? COMPOUND_CONTAINER : NONCOMPOUND_CONTAINER)
				+ "[substring(@data-reactid, string-length(@data-reactid) - " + simpleName.length() + ") = '$"
				+ simpleName + "']//input[@name='" + simpleName + "']");
	}

	public static void waitForErrorMessage(WebDriver driver, String simpleName, String simpleNamePartOf,
			String errorMessage)
	{
		waitForErrorMessageInternal(driver, errorMessage,
				By.xpath(FormsUtils.createXPathAttributeContainerWebElement(simpleName, true)));
	}

	public static void waitForErrorMessage(WebDriver driver, String simpleName, String errorMessage)
	{
		waitForErrorMessageInternal(driver, errorMessage,
				By.xpath(FormsUtils.createXPathAttributeContainerWebElement(simpleName, false)));
	}

	private static void waitForErrorMessageInternal(WebDriver driver, String errorMessage, By container)
	{
		try
		{
			new WebDriverWait(driver, AbstractModel.IMPLICIT_WAIT_SECONDS)
					.until((Predicate<WebDriver>)d -> d.findElement(container).getAttribute("textContent").contains(errorMessage));
		}
		catch (TimeoutException ex)
		{
			LOG.error("Expected error message {} did not appear in element {}. Element text is:{}", errorMessage,
					container, driver.findElement(container).getText());
			throw ex;
		}
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
