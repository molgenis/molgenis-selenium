package org.molgenis.selenium.model;

import org.molgenis.selenium.model.component.Select2Model;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a model of the MOLGENIS Data Explorer user interface
 */
public class DataExplorerModel extends MenuModel
{
	private static final Logger LOG = LoggerFactory.getLogger(DataExplorerModel.class);

	public static enum DeleteOption
	{
		DATA, DATA_AND_METADATA;
	}

	private final Select2Model entityModel;

	@FindBy(css = ".page-next")
	private WebElement next;

	@FindBy(css = ".page-prev")
	private WebElement previous;

	@FindBy(id = "entity-class-name")
	private WebElement entityClassName;

	@FindBy(id = "dropdownMenu1")
	private WebElement deleteDropdownMenu;

	@FindBy(id = "delete-data-btn")
	private WebElement deleteDataButton;

	@FindBy(id = "delete-data-metadata-btn")
	private WebElement deleteDataMetadataBtn;

	@FindBy(css = "[data-bb-handler=confirm]")
	private WebElement confirmButton;

	public DataExplorerModel(WebDriver driver)
	{
		super(driver);
		entityModel = new Select2Model(driver, "dataset-select");
	}

	public void deleteEntity(String entityFullName, DeleteOption deleteOption) throws InterruptedException
	{
		LOG.info("delete {}, mode={} ...", entityFullName, deleteOption);
		deleteDropdownMenu.click();

		switch (deleteOption)
		{
			case DATA:
				deleteDataButton.click();
				break;
			case DATA_AND_METADATA:
				deleteDataMetadataBtn.click();
				break;
			default:
				break;
		}
		confirmButton.click();
	}

	public void selectEntity(String entityLabel) throws InterruptedException
	{
		LOG.info("selectEntity", entityLabel);
		entityModel.select(entityLabel);
	}

	public String getSelectedEntityTitle() throws InterruptedException
	{
		return entityClassName.getText();
	}

	public DataExplorerModel next()
	{
		next.click();
		return this;
	}

	public DataExplorerModel previous()
	{
		previous.click();
		return this;
	}
}
