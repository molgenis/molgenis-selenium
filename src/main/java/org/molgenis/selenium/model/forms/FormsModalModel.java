package org.molgenis.selenium.model.forms;

import java.util.concurrent.TimeUnit;

import org.molgenis.selenium.model.AbstractModel;
import org.molgenis.selenium.model.dataexplorer.data.DataModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;

public class FormsModalModel extends AbstractModel
{
	private static final Logger LOG = LoggerFactory.getLogger(FormsModalModel.class);

	@FindBy(xpath = "//button[@title=\"Hide optional fields\"]")
	private WebElement eyeButton;

	@FindBy(css = "div.modal-body")
	private WebElement modal;

	@FindBy(xpath = "//button[@name=\"save-changes\"]")
	private WebElement saveChangesButton;

	@FindBy(xpath = "//button[@name=\"create\"]")
	private WebElement createButton;

	@FindBy(xpath = "//button[@name=\"cancel\"]")
	private WebElement cancelButton;

	// Modal body
	private By modalBy = By.cssSelector("div.modal-body");

	public FormsModalModel(WebDriver driver)
	{
		super(driver);
	}

	public FormsModalModel clickEyeButton()
	{
		LOG.info("click on the modal eye button...");
		this.eyeButton.click();
		spinner().waitTillDone(2, TimeUnit.SECONDS);
		return PageFactory.initElements(driver, FormsModalModel.class);
	}

	public DataModel clickOnSaveChangesButton()
	{
		LOG.info("click on save changes button...");
		if(FormsUtils.formHasErrors(driver, null)){
			LOG.warn("Form has errors: {}"+ driver.findElement(By.cssSelector(".has-error")).getText());
		}
		saveChangesButton.click();
		waitForModalFormToClose();
		return PageFactory.initElements(driver, DataModel.class);
	}

	public DataModel clickOnCreateButton()
	{
		LOG.info("click on create button...");
		createButton.click();
		waitForModalFormToClose();
		return PageFactory.initElements(driver, DataModel.class);
	}

	public DataModel clickOnCancelButton()
	{
		LOG.info("click on cancel button...");
		cancelButton.click();
		waitForModalFormToClose();
		return PageFactory.initElements(driver, DataModel.class);
	}

	public void waitForModalFormToClose()
	{
		new WebDriverWait(driver, IMPLICIT_WAIT_SECONDS)
				.until((Predicate<WebDriver>) d -> noElementFound(d, null, By.cssSelector(".modal-open")));
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
