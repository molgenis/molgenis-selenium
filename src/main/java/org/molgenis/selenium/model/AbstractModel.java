package org.molgenis.selenium.model;

import static com.google.common.collect.Lists.transform;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.molgenis.selenium.model.component.SpinnerModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

public abstract class AbstractModel
{
	protected final WebDriver driver;
	protected final MenuModel menuModel;
	protected final SpinnerModel spinnerModel;

	public AbstractModel(WebDriver driver)
	{
		this.driver = requireNonNull(driver);
		this.menuModel = PageFactory.initElements(driver, MenuModel.class);
		this.spinnerModel = PageFactory.initElements(driver, SpinnerModel.class);
	}

	public SpinnerModel spinner()
	{
		return spinnerModel;
	}

	public MenuModel menu()
	{
		return menuModel;
	}

	protected List<List<String>> getTableData(List<WebElement> tableRows)
	{
		return tableRows.stream().map(elt -> elt.findElements(By.cssSelector("td")))
				.map(tds -> transform(tds, WebElement::getText)).collect(toList());
	}

	protected synchronized boolean elementsExists(WebElement we, By by)
	{
		this.driver.manage().timeouts().implicitlyWait(0, TimeUnit.MILLISECONDS);
		boolean exists = we.findElements(by).size() != 0;
		this.driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS); // Restore default value
		return exists;
	}
}
