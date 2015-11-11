package org.molgenis.selenium.model.mappingservice;

import org.molgenis.selenium.model.AbstractModel;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlgorithmEditorModel extends AbstractModel
{
	private static final Logger LOG = LoggerFactory.getLogger(AlgorithmEditorModel.class);

	@FindBy(linkText = "Cancel and go back")
	WebElement cancelAndGoBackButton;

	@FindBy(id = "ace-editor-text-area")
	WebElement aceEditorTextArea;

	public AlgorithmEditorModel(WebDriver driver)
	{
		super(driver);
	}

	public String getAlgorithmValue()
	{
		LOG.info("getAlgorithmValue()...");
		String result = aceEditorTextArea.getAttribute("textContent");
		LOG.debug("algorithm={}", result);
		return result;
	}

	public MappingProjectDetailsModel cancelAndGoBack()
	{
		cancelAndGoBackButton.click();
		return PageFactory.initElements(driver, MappingProjectDetailsModel.class);
	}
}
