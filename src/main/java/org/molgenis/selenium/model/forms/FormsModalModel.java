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
	private By modalBy = By.cssSelector("div.modal.in");

	public FormsModalModel(WebDriver driver)
	{
		super(driver);
	}

	public FormsModalModel clickEyeButton()
	{
		LOG.info("click on the modal eye button...");
		this.eyeButton.click();
		spinner().waitTillDone(AbstractModel.IMPLICIT_WAIT_SECONDS, TimeUnit.SECONDS);
		LOG.info("clicked on the modal eye button");
		return PageFactory.initElements(driver, FormsModalModel.class);
	}

	public DataModel clickOnSaveChangesButton()
	{
		LOG.info("click on save changes button...");
		if (FormsUtils.formHasErrors(driver, null))
		{
			throw new RuntimeException(
					"Form has errors: " + driver.findElement(By.cssSelector(".has-error")).getText());
		}
		saveChangesButton.click();
		waitUntilModalFormClosed();
		return PageFactory.initElements(driver, DataModel.class);
	}

	public DataModel clickOnCreateButton()
	{
		LOG.info("click on create button...");
		if (FormsUtils.formHasErrors(driver, null))
		{
			throw new RuntimeException(
					"Form has errors: " + driver.findElement(By.cssSelector(".has-error")).getText());
		}
		createButton.click();
		waitUntilModalFormClosed();
		return PageFactory.initElements(driver, DataModel.class);
	}

	public DataModel clickOnCancelButton()
	{
		LOG.info("click on cancel button...");
		cancelButton.click();
		waitUntilModalFormClosed();
		return PageFactory.initElements(driver, DataModel.class);
	}

	private void waitUntilModalFormClosed()
	{
		WebDriverWait webDriverWait = new WebDriverWait(driver, 30);
		webDriverWait.pollingEvery(100, TimeUnit.MILLISECONDS);
		webDriverWait.until((Predicate<WebDriver>)d -> AbstractModel.noElementFound(d, null, modalBy));
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

	public FormsModalModel waitForModal()
	{
		LOG.info("Wait for modal...");
		new WebDriverWait(driver, IMPLICIT_WAIT_SECONDS)
				.until(ExpectedConditions.presenceOfElementLocated(getModalBy()));
		return this;
	}
}
