package org.molgenis.selenium.model.mappingservice;

import org.molgenis.selenium.model.AbstractModel;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class AlgorithmEditorModel extends AbstractModel
{

	@FindBy(css = "textarea.ace_text-input")
	WebElement algorithmTextarea;

	@FindBy(linkText = "Cancel and go back")
	WebElement cancelAndGoBackButton;

	public AlgorithmEditorModel(WebDriver driver)
	{
		super(driver);
	}

	public String getAlgorithm()
	{
		return algorithmTextarea.getText();
	}

	public MappingProjectDetailsModel cancelAndGoBack()
	{
		cancelAndGoBackButton.click();
		return PageFactory.initElements(driver, MappingProjectDetailsModel.class);
	}
}
