package org.molgenis.selenium.model.mappingservice;

import org.molgenis.selenium.model.MenuModel;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Model for the "Create integrated dataset" popup in the MappingProjectDetail view.
 */
public class CreateIntegratedDatasetModalModel extends MenuModel
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

}
