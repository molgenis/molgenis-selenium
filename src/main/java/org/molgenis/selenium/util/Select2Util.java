package org.molgenis.selenium.util;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;

/**
 * This is a util for the use of the Molgenis Menu
 */
public class Select2Util
{
	public static void select(String select2containerId, String label, WebDriver webDriver, Logger logger)
			throws InterruptedException
	{
		webDriver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
		webDriver.manage().timeouts().setScriptTimeout(10, TimeUnit.SECONDS);

		// Step one: open selector
		logger.info("Open select2 component. Container id: " + select2containerId);
		By select2choiceSelector = By.cssSelector("#" + select2containerId + " .select2-choice");
		SeleniumUtils.waitForElement(select2choiceSelector, webDriver);
		WebElement select2choice = webDriver.findElement(select2choiceSelector);
		select2choice.click();

		// Step two: fill in the perfect search parameter
		By subContainerSelector = By.cssSelector("#select2-drop:not([style*='display:none'])");
		SeleniumUtils.waitForElement(subContainerSelector, webDriver);

		By inputSelector = By.cssSelector("#select2-drop:not([style*='display:none']) .select2-input");
		SeleniumUtils.waitForElement(inputSelector, webDriver);
		WebElement select2searchInput = webDriver.findElement(inputSelector);
		select2searchInput.sendKeys(label);

		// Step three: select the first perfect match
		By subContainerResultsSelector = By
				.cssSelector("#select2-drop:not([style*='display:none']) .select2-results li.select2-result-selectable");
		SeleniumUtils.waitForElement(subContainerResultsSelector, webDriver);
		List<WebElement> select2elements = webDriver.findElements(subContainerResultsSelector);

		List<WebElement> select2elementsFilterd = select2elements.stream().filter(e -> e.getText().equals(label))
				.collect(Collectors.toList());

		select2elementsFilterd.get(0).click();
	}
}