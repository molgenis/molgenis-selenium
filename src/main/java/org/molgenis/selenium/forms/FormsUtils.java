package org.molgenis.selenium.forms;

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

	enum HTMLInputType
	{
		radio, text, number, email, url, hidden, checkbox
	}

	public static void changeValueNoncompoundAttribute(WebElement context, String simpleName, String value)
	{
		WebElement attribuetContainer = findAttributeContainerWebElement(context, simpleName, false);
		changeValueAttribute(attribuetContainer, simpleName, value);
	}

	public static void changeValueCompoundAttribute(WebElement context, String simpleName, String simpleNamePartOf,
			String value)
	{
		WebElement attribuetContainer = findAttributeContainerWebElement(context, simpleName, true);
		changeValueAttribute(attribuetContainer, simpleNamePartOf, value);
	}


	public static void changeValueAttribute(WebElement attributeContainer, String simpleName, String value)
	{
		List<WebElement> inputList = attributeContainer
				.findElements(By.cssSelector("\\input[name=" + simpleName + "]"));

		switch (HTMLInputType.valueOf(inputList.get(0).getAttribute("type")))
		{
			case radio:
				attributeContainer.findElement(By.xpath("//input[@name='" + simpleName + "'][@value='" + value + "']"))
						.click();
				break;
			case text:
			case number:
			case email:
			case url:
				final WebElement urlInput = attributeContainer.findElement(By.xpath("//input[@name='" + simpleName
						+ "']"));
				urlInput.clear();
				urlInput.sendKeys((String) value);
				break;
			case hidden:
			case checkbox:
				LOG.warn("The hidden and checkbox HTML input type are not supported");
				break;
			default:
				break;
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
	public static void changeValueAttributeCheckbox(WebElement context, String simpleName,
			String... values)
	{
		WebElement container = findAttributeContainerWebElement(context, simpleName, false);
		container.findElements(By.cssSelector("input[name='" + simpleName + "']:checked")).stream()
				.forEachOrdered(e -> e.click());
		Arrays.asList(values)
				.stream()
				.filter(e -> !"".equals(e))
				.forEach(
						e -> container.findElement(
								By.xpath("//input[@name='" + simpleName + "'][@value='" + e + "']")).click());
	}

	/**
	 * Use this method to empty and add new values to a select2 attribute
	 * 
	 * @param simpleName
	 * @param idAndLabel
	 * @param multi
	 */
	public static void changeValueAttributeSelect2(WebDriver driver, WebElement context, String simpleName,
			Map<String, String> idAndLabel, boolean multi, boolean clearOriginalValues)
	{
		WebElement container = findAttributeContainerWebElement(context, simpleName, false);

		Select2Model s2model = new Select2Model(driver, container.findElement(
				By.cssSelector(".select2-container")).getAttribute("id"), multi);

		if (clearOriginalValues)
		{
			s2model.clearSelection();
		}
		s2model.select(idAndLabel);
	}

	public static Map<String, WebElement> findAttributesContainerWebElement(WebElement context,
			List<String> simpleNames,
			boolean isCompoundAttribute)
	{
		Map<String, WebElement> result = new HashMap<String, WebElement>();
		simpleNames.stream().forEachOrdered(
				simpleName -> result.put(simpleName,
						FormsUtils.findAttributeContainerWebElement(context, simpleName, isCompoundAttribute)
						));
		return result;
	}

	public static WebElement findAttributeContainerWebElement(WebElement context, String simpleName,
			boolean isCompoundAttribute)
	{
		return context.findElement(By.xpath("//" + (isCompoundAttribute ? COMPOUND_CONTAINER : NONCOMPOUND_CONTAINER)
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
	public static boolean formHasErrors(WebDriver webDriver, WebElement context)
	{
		return AbstractModel.exists(webDriver, context, By.cssSelector(".has-error"));
	}
}
