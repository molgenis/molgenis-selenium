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

	// Save changes button
	@FindBy(xpath = "//button[@name=\"save-changes\"]")
	private WebElement saveChangesButton;

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

	public DataModel clickOnSaveChangesButton()
	{
		LOG.info("clicked on edit first row button for entity TypeTest");
		this.saveChangesButton.click();
		spinner().waitTillDone(10, TimeUnit.SECONDS);
		return PageFactory.initElements(driver, DataModel.class);
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

	enum HTMLInputType
	{
		radio, text, number
	}

	public void changeValueNoncompoundAttribute(String simpleName, String value)
	{
		WebElement attribuetContainer = findAttributesContainerWebElement(simpleName, false);
		changeValueAttribute(attribuetContainer, simpleName, value);
	}

	public void changeValueCompoundAttribute(String simpleName, String simpleNamePartOf, String value)
	{
		WebElement attribuetContainer = findAttributesContainerWebElement(simpleName, true);
		changeValueAttribute(attribuetContainer, simpleNamePartOf, value);
	}

	public void changeValueAttribute(WebElement attribuetContainer, String simpleName, String value)
	{
		List<WebElement> inputList = attribuetContainer
				.findElements(By.cssSelector("\\input[name=" + simpleName + "]"));

		switch (HTMLInputType.valueOf(inputList.get(0).getAttribute("type")))
		{
			case radio:
				attribuetContainer.findElement(By.xpath("//input[@name='" + simpleName + "'][@value='" + value + "']"))
						.click();
				break;
			case text:
			case number:
				final WebElement element = attribuetContainer.findElement(By.xpath("//input[@name='" + simpleName
						+ "']"));
				element.clear();
				element.sendKeys(value);
				break;
			default:
				break;
		}
	}

	public boolean exists(String simpleName, boolean isCompoundAttribute)
	{
		try
		{
			return null != findAttributesContainerWebElement(simpleName, isCompoundAttribute);
		}
		catch (Exception e)
		{
			return false;
		}
	}

	public Map<String, WebElement> findAttributesContainerWebElement(List<String> simpleNames,
			boolean isCompoundAttribute)
	{
		Map<String, WebElement> result = new HashMap<String, WebElement>();
		simpleNames.stream().forEachOrdered(
				simpleName -> result.put(simpleName,
						this.findAttributesContainerWebElement(simpleName, isCompoundAttribute)
						));
		return result;
	}
	
	private WebElement findAttributesContainerWebElement(String simpleName,
			boolean isCompoundAttribute){
		return modalBody.findElement(By.xpath("//" + (isCompoundAttribute ? COMPOUND_CONTAINER : NONCOMPOUND_CONTAINER)
				+ "[substring(@data-reactid, string-length(@data-reactid) - " + simpleName.length()
				+ ") = '$" + simpleName + "']"));
	}
}
