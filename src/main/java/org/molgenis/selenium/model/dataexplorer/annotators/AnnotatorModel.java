package org.molgenis.selenium.model.dataexplorer.annotators;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.molgenis.selenium.model.AbstractModel;
import org.molgenis.selenium.model.dataexplorer.DataExplorerModel;
import org.molgenis.selenium.model.dataexplorer.data.DataModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnnotatorModel extends AbstractModel
{
	private static final Logger LOG = LoggerFactory.getLogger(AnnotatorModel.class);

	@FindBy(css = "#enabled-annotator-selection-container input")
	private List<WebElement> availableCheckboxes;

	@FindBy(css = "#enabled-annotator-selection-container input:checked")
	private List<WebElement> selectedCheckboxes;

	@FindBy(id = "enabled-annotator-selection-container")
	private WebElement enabledAnnotatorSelectionContainer;

	@FindBy(id = "annotate-dataset-button")
	private WebElement annotateButton;

	@FindBy(css = "a.select-all-btn")
	private WebElement selectAll;

	@FindBy(css = "a.deselect-all-btn")
	private WebElement deselectAll;

	@FindBy(linkText = "Data")
	private WebElement dataTab;

	public AnnotatorModel(WebDriver driver)
	{
		super(driver);
	}

	public DataExplorerModel selectDataTab()
	{
		LOG.info("Select Data tab...");
		dataTab.click();
		spinner().waitTillDone(10, TimeUnit.SECONDS);
		return PageFactory.initElements(driver, DataExplorerModel.class);
	}

	private void waitForAnnotators()
	{
		new WebDriverWait(driver, 10).until(webDriver -> ExpectedConditions.visibilityOf(enabledAnnotatorSelectionContainer));
	}

	public AnnotatorModel select(String annotator)
	{
		LOG.info("Select {}...", annotator);
		WebElement checkbox = findAnnotatorCheckbox(annotator);

		if (!checkbox.isSelected())
		{
			checkbox.click();
		}

		return this;
	}

	public AnnotatorModel selectAll()
	{
		LOG.info("Select All...");
		waitForAnnotators();
		selectAll.click();
		return this;
	}

	public AnnotatorModel deselectAll()
	{
		LOG.info("Deselect All...");
		waitForAnnotators();
		deselectAll.click();
		return this;
	}

	public AnnotatorModel deselect(String annotator)
	{
		LOG.info("deselect {}...", annotator);
		WebElement checkbox = findAnnotatorCheckbox(annotator);
		if (checkbox.isSelected())
		{
			checkbox.click();
		}
		return this;
	}

	private WebElement findAnnotatorCheckbox(String annotator)
	{
		return driver.findElement(By.cssSelector("#annotator-select-container input[value=" + annotator + "]"));
	}

	public List<String> getSelectedAnnotators()
	{
		LOG.info("getSelectedAnnotators()...");
		List<String> result = selectedCheckboxes.stream().map(e -> e.getAttribute("value"))
				.collect(Collectors.toList());
		LOG.debug("result = {}", result);
		return result;
	}

	public List<String> getAvailableAnnotators()
	{
		return availableCheckboxes.stream().map(e -> e.getAttribute("name")).collect(Collectors.toList());
	}

	public DataExplorerModel clickAnnotateButtonAndWait(int timeout)
	{
		annotateButton.click();

		DataModel dataModel = PageFactory.initElements(driver, DataModel.class);
		dataModel.waitUntilReady(timeout);

		return PageFactory.initElements(driver, DataExplorerModel.class);
	}

	public AnnotatorModel clickCopy(String entityName, String newEntityName)
	{
		LOG.info("Copy [" + entityName + "] and create [" + newEntityName + "]");
		selectDataTab().selectEntity(entityName);
		DataExplorerModel dataExplorerModel = PageFactory.initElements(driver, DataExplorerModel.class);
		dataExplorerModel.copyEntity(newEntityName);
		return this;
	}
}
