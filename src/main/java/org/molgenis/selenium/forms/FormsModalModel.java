package org.molgenis.selenium.forms;

import java.util.concurrent.TimeUnit;

import org.molgenis.selenium.model.AbstractModel;
import org.molgenis.selenium.model.dataexplorer.data.DataModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FormsModalModel extends AbstractModel
{
	private static final Logger LOG = LoggerFactory.getLogger(FormsModalModel.class);
	
	// Cancel button
	@FindBy(css = "button[name=\"cancel\"]")
	private WebElement cancelButton;

	// Eye button
	@FindBy(xpath = "//button[@title=\"Hide optional fields\"]")
	private WebElement eyeButton;

	// Save changes button
	@FindBy(xpath = "//button[@name=\"save-changes\"]")
	private WebElement saveChangesButton;

	@FindBy(css = "div.modal-body")
	private WebElement modal;

	public FormsModalModel(WebDriver driver)
	{
		super(driver);
	}

	public DataModel clickOnCloseButton()
	{
		this.cancelButton.click();
		spinner().waitTillDone(2, TimeUnit.SECONDS);
		LOG.info("clicked on the modal cancel button");
		return PageFactory.initElements(driver, DataModel.class);
	}
	
	public FormsModalModel clickEyeButton()
	{
		this.eyeButton.click();
		spinner().waitTillDone(2, TimeUnit.SECONDS);
		LOG.info("clicked on the modal eye button");
		return PageFactory.initElements(driver, FormsModalModel.class);
	}

	public DataModel clickOnSaveChangesButton()
	{
		LOG.info("clicked on edit first row button for entity TypeTest");
		this.saveChangesButton.click();
		spinner().waitTillDone(10, TimeUnit.SECONDS);
		return PageFactory.initElements(driver, DataModel.class);
	}
	
	/**
	 * Is this modal open?
	 * 
	 * @return boolean
	 */
	public boolean isModalFormOpen()
	{
		return AbstractModel.exists(super.driver, null, By.cssSelector(".modal-open"));
	}

	/**
	 * @return the modal
	 */
	public WebElement getModal()
	{
		return modal;
	}
}
