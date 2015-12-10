package org.molgenis.selenium.forms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

public class FormsModel extends AbstractModel
{
	private static final Logger LOG = LoggerFactory.getLogger(FormsModel.class);
	private static final String NONCOMPOUND_CONTAINER = "div";
	private static final String COMPOUND_CONTAINER = "fieldset";
	
	// Cancel button
	@FindBy(css = "button[name=\"cancel\"]")
	private WebElement cancelButton;

	// Eye button
	@FindBy(xpath = "//button[@title=\"Hide optional fields\"]")
	private WebElement eyeButton;

	// id label
	@FindBy(css = "label[for=\"id\"]")
	private WebElement idLabel;
	
	// id input
	@FindBy(css = "input#id.form-control")
	private WebElement idInput;

	// // xbool label
	// @FindBy(css = "label[for=\"xbool\"]")
	// private WebElement xboolLabel;
	//
	// // xbool input
	// @FindBy(css = "input#xbool.form-control")
	// private WebElement xboolInput;

	@FindBy(css = "div.modal-body")
	private WebElement modalBody;

	public FormsModel(WebDriver driver)
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
	
	public FormsModel clickEyeButton()
	{
		this.eyeButton.click();
		spinner().waitTillDone(2, TimeUnit.SECONDS);
		LOG.info("clicked on the modal eye button");
		return PageFactory.initElements(driver, FormsModel.class);
	}

	/**
	 * @return the idLabel
	 */
	public WebElement getIdLabel()
	{
		return idLabel;
	}

	/**
	 * @return the idInput
	 */
	public WebElement getIdInput()
	{
		return idInput;
	}

	public Map<String, WebElement> findAttributes(List<String> simpleNames, boolean isCompoundAttribute)
	{
		return findAttributes(modalBody, simpleNames, isCompoundAttribute);
	}

	public boolean exists(String simpleName, boolean isCompoundAttribute)
	{
		String container = getAttributeContainer(isCompoundAttribute);
		try
		{
			modalBody
					.findElement(By.xpath("//" + container
					+ "[substring(@data-reactid, string-length(@data-reactid) - " + simpleName.length() + ") = '$"
					+ simpleName + "']"));
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}

	private Map<String, WebElement> findAttributes(WebElement webElement, List<String> simpleNames,
			boolean isCompoundAttribute)
	{
		String container = getAttributeContainer(isCompoundAttribute);
		Map<String, WebElement> result = new HashMap<String, WebElement>();
		simpleNames.stream().forEachOrdered(
				simpleName -> result.put(
						simpleName,
						webElement.findElement(By.xpath("//" + container
								+ "[substring(@data-reactid, string-length(@data-reactid) - " + simpleName.length()
								+ ") = '$" + simpleName + "']"))));
		return result;
	}

	private String getAttributeContainer(boolean isCompoundAttribute)
	{
		return isCompoundAttribute ? COMPOUND_CONTAINER : NONCOMPOUND_CONTAINER;
	}
}
