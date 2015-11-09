package org.molgenis.selenium.model.mappingservice;

import org.apache.commons.lang3.StringUtils;
import org.molgenis.selenium.model.AbstractModel;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlgorithmEditorModel extends AbstractModel
{
	private static final Logger LOG = LoggerFactory.getLogger(AlgorithmEditorModel.class);

	@FindBy(css = "div.ace-editor-container")
	WebElement aceEditorTextArea;

	@FindBy(linkText = "Cancel and go back")
	WebElement cancelAndGoBackButton;

	public AlgorithmEditorModel(WebDriver driver)
	{
		super(driver);
	}

	public AlgorithmEditorModel assertAlgorithmValueEquals(String string)
	{
		LOG.info("assertAlgorithmValueEquals {}...", string);
		Wait<WebDriver> tenSecondWait = new WebDriverWait(driver, 10);
		tenSecondWait.until(ExpectedConditions.textToBePresentInElement(aceEditorTextArea, string));
		LOG.info("OK");
		return this;
	}

	public MappingProjectDetailsModel cancelAndGoBack()
	{
		cancelAndGoBackButton.click();
		return PageFactory.initElements(driver, MappingProjectDetailsModel.class);
	}
}
