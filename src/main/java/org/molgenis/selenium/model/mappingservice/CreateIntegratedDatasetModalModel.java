package org.molgenis.selenium.model.mappingservice;

import org.molgenis.selenium.model.AbstractModel;
import org.molgenis.selenium.model.dataexplorer.DataExplorerModel;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 * Model for the "Create integrated dataset" popup in the MappingProjectDetail view.
 */
public class CreateIntegratedDatasetModalModel extends AbstractModel
{
	@FindBy(name = "newEntityName")
	private WebElement newEntityName;

	@FindBy(id = "create-integrated-entity-btn")
	private WebElement createIntegratedEntityButton;

	public CreateIntegratedDatasetModalModel(WebDriver driver)
	{
		super(driver);
	}

	public CreateIntegratedDatasetModalModel setEntityName(String name)
	{
		newEntityName.clear();
		newEntityName.sendKeys(name);
		return this;
	}

	public DataExplorerModel createIntegratedDataset()
	{
		createIntegratedEntityButton.click();
		return PageFactory.initElements(driver, DataExplorerModel.class);
	}

}
