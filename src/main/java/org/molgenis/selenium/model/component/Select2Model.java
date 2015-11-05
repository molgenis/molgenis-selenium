package org.molgenis.selenium.model.component;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Model for a select2 mref selection box.
 */
public class Select2Model implements MultiSelectModel
{
	private static final Logger LOG = LoggerFactory.getLogger(Select2Model.class);
	private WebDriver driver;
	private String id;
	// Selector for the close buttons for the selected choices.
	private final By closeButtonSelector;
	// Selector for the selected choices
	private final By choiceSelector;

	/**
	 * Creates a new Select2Model
	 * 
	 * @param driver
	 *            the {@link WebDriver} to use
	 * @param selector
	 *            the id of the Select2, without the s2id_ prefix
	 */
	public Select2Model(WebDriver driver, String id)
	{
		this.driver = driver;
		this.id = id;
		closeButtonSelector = By
				.xpath("//div[@id='s2id_" + id + "']//a[contains(@class, 'select2-search-choice-close')]");
		choiceSelector = By.xpath("//div[@id='s2id_" + id + "']//li[contains(@class, 'select2-search-choice')]");
	}

	/**
	 * Retrieves the labels for the selected items.
	 * 
	 * @return List containing the selected item texts
	 */
	@Override
	public List<String> getSelectedLabels()
	{
		LOG.debug("Get selected labels in Select2 with id {}...", id);
		return driver.findElements(choiceSelector).stream().map(WebElement::getText).collect(toList());
	}

	/**
	 * Removes all selected items.
	 */
	@Override
	public void clearSelection()
	{
		LOG.info("Clear selection in Select2 with id {}...", id);
		while (!driver.findElements(closeButtonSelector).isEmpty())
		{
			driver.findElement(closeButtonSelector).click();
		}
		LOG.debug("Selection is empty.");
	}

	/**
	 * Types terms into the select box, waits for a matching result to appear and adds them to the selection.
	 * 
	 * @param term
	 *            the term to type
	 */
	@Override
	public void select(String... terms)
	{
		LOG.info("Selecting terms {} in Select2 with id {}...", Arrays.asList(terms), id);
		for (String term : terms)
		{
			LOG.debug("Click select.");
			WebElement select2Input = driver.findElement(By.id("s2id_" + id));
			select2Input.click();

			LOG.debug("Wait for text input box...");
			WebElement select2InputText = driver.findElement(
					By.xpath("//div[@id='s2id_" + id + "']//input[contains(@class, 'select2-input')][@type='text']"));

			LOG.debug("Empty text input box..");
			while (!StringUtils.isEmpty(select2InputText.getText()))
			{
				select2InputText.sendKeys(Keys.BACK_SPACE);
			}
			LOG.debug("Text input box empty. Entering term...");
			select2InputText.sendKeys(term);
			select2InputText.sendKeys(Keys.ENTER);

			LOG.debug("Waiting for selection to appear in the list of search choices...");
			driver.findElement(By.xpath("//div[@id='s2id_" + id
					+ "']//li[contains(@class, 'select2-search-choice')]/div[contains(text(), '" + term + "')]"));
			LOG.debug("Selected term '{}'.", term);
		}
		LOG.debug("Selected terms: {}. Selected labels are: {}.", terms, getSelectedLabels());
	}
}
