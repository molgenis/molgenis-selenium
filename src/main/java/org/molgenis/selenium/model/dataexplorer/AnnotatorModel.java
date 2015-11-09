package org.molgenis.selenium.model.dataexplorer;

import java.util.concurrent.TimeUnit;

import org.molgenis.selenium.model.AbstractModel;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class AnnotatorModel extends AbstractModel
{
	@FindBy(css = "#annotator-select-container input[value=snpEff]")
	WebElement snpEffCheckbox;

	@FindBy(css = "#annotator-select-container input[value=cadd]")
	WebElement caddCheckbox;

	@FindBy(css = "#annotator-select-container input[name=createCopy]")
	WebElement copyCheckbox;

	@FindBy(id = "annotate-dataset-button")
	WebElement annotateButton;

	@FindBy(linkText = "Show result")
	WebElement showResultText;

	public AnnotatorModel(WebDriver driver)
	{
		super(driver);
	}

	public AnnotatorModel clickSnpEff()
	{
		snpEffCheckbox.click();
		return this;
	}

	public AnnotatorModel clickCADD()
	{
		caddCheckbox.click();
		return this;
	}

	public AnnotatorModel clickCopy()
	{
		copyCheckbox.click();
		return this;
	}

	public AnnotatorModel clickAnnotateButtonAndWait(int timeout, TimeUnit unit)
	{
		annotateButton.click();
		spinner().waitTillDone(timeout, unit);
		return this;
	}

	public DataExplorerModel goToResult()
	{
		showResultText.click();
		return PageFactory.initElements(driver, DataExplorerModel.class);
	}

}