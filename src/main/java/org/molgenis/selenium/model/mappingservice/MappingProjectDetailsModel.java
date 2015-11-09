package org.molgenis.selenium.model.mappingservice;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.molgenis.selenium.model.AbstractModel;
import org.molgenis.selenium.model.component.Select2Model;
import org.molgenis.selenium.model.dataexplorer.DataExplorerModel;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MappingProjectDetailsModel extends AbstractModel
{
	private static final Logger LOG = LoggerFactory.getLogger(MappingProjectDetailsModel.class);

	@FindBy(partialLinkText = "Back to mapping project overview")
	private WebElement backToMappingProjectOverviewButton;

	@FindBy(id = "add-new-attr-mapping-btn")
	private WebElement addSourceButton;

	@FindBy(id = "submit-new-source-column-btn")
	private WebElement submitNewSourceColumnButton;

	@FindBy(xpath = "//div[@id='create-new-source-column-modal']//button[contains(text(), 'Cancel')]")
	private WebElement cancelNewSourceButton;

	@FindBy(css = "#attribute-mapping-table tbody tr")
	private List<WebElement> mappingProjectTableRows;

	@FindBy(id = "//a[@data-target='#create-integrated-entity-modal' and contains(@Class, btn)]")
	private WebElement createIntegratedEntityOpenModalButton;

	@FindBy(xpath = "//a[@data-target='#create-integrated-entity-modal' and contains(@Class, btn)]")
	private WebElement createIntegratedDatasetModalButton;

	private Select2Model sourceEntitySelect;

	public MappingProjectDetailsModel(WebDriver driver)
	{
		super(driver);
		sourceEntitySelect = new Select2Model(driver, "source-entity-select", false);
	}

	public MappingProjectsModel backToMappingProjectsOverview()
	{
		backToMappingProjectOverviewButton.click();
		return PageFactory.initElements(driver, MappingProjectsModel.class);
	}

	public MappingProjectDetailsModel addSource(String sourceEntityName)
	{
		addSourceButton.click();
		sourceEntitySelect.select(sourceEntityName);
		submitNewSourceColumnButton.click();
		spinner().waitTillDone(30, TimeUnit.SECONDS);
		return this;
	}

	public DataExplorerModel createIntegratedDataset(String entityName)
	{
		LOG.info("Create integrated dataset. Entity name = {}", entityName);
		createIntegratedDatasetModalButton.click();
		return PageFactory.initElements(driver, CreateIntegratedDatasetModalModel.class).setEntityName(entityName)
				.createIntegratedDataset();
	}

	public List<List<String>> getMappingProjectTableData()
	{
		return getTableData(mappingProjectTableRows);
	}
}
