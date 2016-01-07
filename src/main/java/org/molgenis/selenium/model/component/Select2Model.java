package org.molgenis.selenium.model.component;

import static java.util.stream.Collectors.toList;
import static org.openqa.selenium.support.ui.ExpectedConditions.textToBePresentInElementLocated;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Model for a select2 selection box. This class works for both multi selects and single selects.
 */
public class Select2Model
{
	private static final Logger LOG = LoggerFactory.getLogger(Select2Model.class);
	private WebDriver driver;
	private String id;
	// Selector for the close buttons for the selected choices.
	private final By closeButtonSelector;
	// Selector for the selected choices
	private final By choiceSelector;
	private final By selectedOptionSelector;
	private final By selectSelector;
	private boolean multi;
	private final WebDriverWait tenSecondWait;

	/**
	 * Creates a new Select2Model
	 * 
	 * @param driver
	 *            the {@link WebDriver} to use
	 * @param selector
	 *            the id of the Select2, without the s2id_ prefix
	 */
	public Select2Model(WebDriver driver, String id, boolean multi)
	{
		this.driver = driver;
		this.id = id.replaceFirst("s2id_", "");
		this.multi = multi;
		closeButtonSelector = By.xpath("//div[@id='s2id_" + this.id
				+ "']//a[contains(@class, 'select2-search-choice-close')]");
		choiceSelector = By.xpath("//div[@id='s2id_" + this.id + "']//.[contains(@class, 'select2-choice')]");
		selectedOptionSelector = By
				.cssSelector("#s2id_" + this.id + (multi ? " .select2-choices" : " .select2-choice"));
		selectSelector = By.id("s2id_" + this.id);
		tenSecondWait = new WebDriverWait(driver, 10);
	}

	/**
	 * Retrieves the labels for the selected items.
	 * 
	 * @return List containing the selected item texts
	 */
	public List<String> getSelectedLabels()
	{
		LOG.debug("Get selected labels in Select2 with id {}...", id);
		return driver.findElements(choiceSelector).stream().map(WebElement::getText).map(String::trim)
				.collect(toList());
	}

	/**
	 * Removes all selected items.
	 */
	public void clearSelection()
	{
		LOG.info("Clear selection in Select2 with id {}...", id);
		while (!StringUtils.isEmpty(driver.findElement(selectSelector).getText().trim()))
		{
			// FIXME It is not working for non multi select2.
			driver.findElement(closeButtonSelector).click();
		}
		LOG.debug("Selection is empty.");
	}

	/**
	 * Types terms into the select box, waits for a matching result to appear and adds them to the selection.
	 * 
	 * @param term
	 *            the term to type
	 * @throws InterruptedException
	 */
	public void select(String... terms)
	{
		select(Arrays.stream(terms).collect(Collectors.<String, String, String> toMap(term -> term, term -> term)));
	}

	/**
	 * Types terms into the select box, waits for a matching result to appear and adds them to the selection.
	 * 
	 * @param idsAndLabels
	 *            Map mapping the ID of the term to the label of the term
	 * @throws InterruptedException
	 */
	public void select(Map<String, String> idsAndLabels)
	{
		LOG.info("Selecting terms with ids and labels {} in Select2 with id {}...", idsAndLabels, id);
		for (Map.Entry<String, String> entry : idsAndLabels.entrySet())
		{
			if (entry.getKey().isEmpty() || entry.getValue().isEmpty()) continue;
			
			LOG.debug("Click select.");
			WebElement select2Option = driver.findElement(selectedOptionSelector);
			select2Option.click();

			LOG.debug("Wait for text input box...");
			WebElement select2InputText = driver.findElement(By
					.cssSelector(multi ? "#s2id_" + id + " input" : "#select2-drop input"));

			LOG.debug("Empty text input box..");
			select2InputText.clear();

			LOG.debug("Text input box empty. Entering term...");
			select2InputText.sendKeys(entry.getKey());

			LOG.debug("Waiting for match..");
			By matchSelector = By
					.xpath("//div[contains(@class,'select2-result-label')]//b[normalize-space(.)='"
							+ entry.getKey()
							+ "']|//div[contains(@class,'select2-result-label')][normalize-space(.)='"
							+ entry.getKey() + "']");
			selectMatch(matchSelector);
			
			LOG.debug("Waiting for selection to appear in the list of search choices...");
			tenSecondWait.until(textToBePresentInElementLocated(
					multi ? By.xpath("//div[@id='s2id_" + id + "']") : selectedOptionSelector, entry.getValue()));

			LOG.debug("Selected '{}'.", entry);
		}
		LOG.debug("Selected terms with ids and labels {}. Selected labels are: {}.", idsAndLabels, getSelectedLabels());
	}

	private void selectMatch(By matchSelector)
	{
		try
		{
			Thread.sleep(100);
		}
		catch (InterruptedException e)
		{
			
		}
		for(int i = 0; i<10; i++){
			LOG.info("Wait for result...");
			WebElement match = driver.findElement(matchSelector);
			LOG.info("Found result, try to click it...");
			try {
				match.click();
				return;
			} catch (StaleElementReferenceException|ElementNotVisibleException ex){
				LOG.warn("Match went stale, attempt {} failed.", i);
			}
		}
		throw new StaleElementReferenceException("Match not found after 10 tries");
	}
}
