package org.molgenis.selenium.util;

import java.util.List;
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
		// Step one: open selector
		logger.info("Open select2 component. Container id: " + select2containerId);
		WebElement select2choice = webDriver.findElement(By.cssSelector("#" + select2containerId + " .select2-choice"));
		select2choice.click();

		// Step two: fill in the perfect search parameter
		String subContainerSelector = "#select2-drop:not([style*='display:none'])";
		SeleniumUtils.waitForElement(By.cssSelector(subContainerSelector), webDriver);
		
		WebElement select2searchInput = webDriver.findElement(By.cssSelector(subContainerSelector + " .select2-input"));
		select2searchInput.sendKeys(label);

		// Step three: select the first perfect match
		String subContainerResultsSelector = subContainerSelector + " .select2-results li.select2-result-selectable";
		SeleniumUtils.waitForElement(By.cssSelector(subContainerResultsSelector), webDriver);
		List<WebElement> select2elements = webDriver.findElements(By.cssSelector(subContainerResultsSelector));

		List<WebElement> select2elementsFilterd = select2elements.stream().filter(e -> e.getText().equals(label))
				.collect(Collectors.toList());

		select2elementsFilterd.get(0).click();
	}
}

