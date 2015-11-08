package org.molgenis.selenium.model.mappingservice;

import org.molgenis.selenium.model.MenuModel;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class MappingProjectDetailsModel extends MenuModel
{
	@FindBy(partialLinkText = "Back to mapping project overview")
	private WebElement backToMappingProjectOverviewButton;

	public MappingProjectDetailsModel(WebDriver driver)
	{
		super(driver);
	}

	public MappingProjectsModel backToMappingProjectsOverview()
	{
		backToMappingProjectOverviewButton.click();
		return PageFactory.initElements(driver, MappingProjectsModel.class);
	}
}
