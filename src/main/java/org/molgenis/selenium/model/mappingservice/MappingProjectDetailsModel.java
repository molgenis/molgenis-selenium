package org.molgenis.selenium.model.mappingservice;

import org.molgenis.selenium.model.MenuModel;
import org.molgenis.selenium.model.component.Select2Model;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class MappingProjectDetailsModel extends MenuModel
{
	@FindBy(partialLinkText = "Back to mapping project overview")
	private WebElement backToMappingProjectOverviewButton;

	@FindBy(id = "add-new-attr-mapping-btn")
	private WebElement addSourceButton;

	@FindBy(id = "submit-new-source-column-btn")
	private WebElement submitNewSourceColumnButton;

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
}
