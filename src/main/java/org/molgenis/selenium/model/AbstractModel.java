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
	public static final int IMPLICIT_WAIT_SECONDS = 30;

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

	/**
	 * Tests the absence of an element right now, without waiting if it perhaps will appear within the implicit timeout.
	 * Resets the web driver's timeout to {@link #IMPLICIT_WAIT_SECONDS} when done.
	 * 
	 * @param webDriver
	 *            WebDriver
	 * @param context
	 *            WebElement: can be null than the webDriver is used to search.
	 * @param by
	 *            By: by is used to fined the WebElement and define if exist
	 * @return
	 */
	public static boolean noElementFound(WebDriver webDriver, By context, By by)
	{
		try
		{
			webDriver.manage().timeouts().implicitlyWait(1, TimeUnit.MILLISECONDS);
			return (null == context ? webDriver : webDriver.findElement(context)).findElements(by).isEmpty();
		}
		finally
		{
			webDriver.manage().timeouts().implicitlyWait(IMPLICIT_WAIT_SECONDS, TimeUnit.SECONDS); // Restore default value
		}
	}
}
