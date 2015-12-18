package org.molgenis.selenium.model.forms;

import java.util.concurrent.TimeUnit;

import org.molgenis.selenium.model.AbstractModel;
import org.molgenis.selenium.model.dataexplorer.data.DataModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FormsModalModel extends AbstractModel
{
	private static final Logger LOG = LoggerFactory.getLogger(FormsModalModel.class);

	// Eye button
	@FindBy(xpath = "//button[@title=\"Hide optional fields\"]")
	private WebElement eyeButton;

	@FindBy(css = "div.modal-body")
	private WebElement modal;

	// Save changes button
	private By saveChangesButtonBy = By.xpath("//button[@name=\"save-changes\"]");

	// Create button
	private By createButtonBy = By.xpath("//button[@name=\"create\"]");

	// Cancel button
	private By cancelButtonBy = By.xpath("//button[@name=\"cancel\"]");

	// Modal body
	private By modalBy = By.cssSelector("div.modal-body");

	public FormsModalModel(WebDriver driver)
	{
		super(driver);
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
		LOG.info("clicked on save changes button");
		driver.findElement(this.saveChangesButtonBy).click();
		spinner().waitTillDone(10, TimeUnit.SECONDS);
		return PageFactory.initElements(driver, DataModel.class);
	}
	
	public DataModel clickOnCreateButton()
	{
		LOG.info("clicked on create button");
		driver.findElement(this.createButtonBy).click();
		spinner().waitTillDone(10, TimeUnit.SECONDS);
		return PageFactory.initElements(driver, DataModel.class);
	}

	public DataModel clickOnCancelButton()
	{
		LOG.info("clicked on cancel button");
		driver.findElement(this.cancelButtonBy).click();
		spinner().waitTillDone(10, TimeUnit.SECONDS);
		return PageFactory.initElements(driver, DataModel.class);
	}

	/**
	 * Is this modal open?
	 * 
	 * @return boolean
	 */
	public boolean isModalFormClosed()
	{
		WebDriverWait webDriverWait = new WebDriverWait(driver, 30);
		Boolean result = webDriverWait.until(ExpectedConditions.invisibilityOfElementLocated(By
				.cssSelector(".modal-open")));
		return null != result && result.booleanValue();
	}

	/**
	 * @return the modal
	 */
	public WebElement getModal()
	{
		return modal;
	}

	/**
	 * @return the modalBy
	 */
	public By getModalBy()
	{
		return modalBy;
	}
}
