package org.molgenis.selenium.model.mappingservice;

import java.util.List;

import org.molgenis.selenium.model.AbstractModel;
import org.molgenis.selenium.model.component.Select2Model;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class MappingProjectDetailsModel extends AbstractModel
{
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
		return this;
	}

	public void createIntegratedDataset()
	{

	}

	public List<List<String>> getMappingProjectTableData()
	{
		return getTableData(mappingProjectTableRows);
	}
}
