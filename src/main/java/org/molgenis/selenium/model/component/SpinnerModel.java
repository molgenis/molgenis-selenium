package org.molgenis.selenium.model.component;

import static org.molgenis.selenium.test.AbstractSeleniumTest.IMPLICIT_WAIT_SECONDS;
import static org.openqa.selenium.support.ui.ExpectedConditions.not;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;

public class SpinnerModel
{
	private static final Logger LOG = LoggerFactory.getLogger(SpinnerModel.class);

	private final WebDriver driver;

	@FindBy(id = "spinner")
	private WebElement spinner;

	public SpinnerModel(WebDriver driver)
	{
		this.driver = driver;
	}

	/**
	 * Waits for a certain amount of seconds for a period of half a second without spinner.
	 */
	public SpinnerModel waitTillDone(int timeout)
	{
		try
		{
			LOG.info("Wait for spinner...");
			Stopwatch sw = Stopwatch.createStarted();
			Wait<WebDriver> halfSecondWait = new WebDriverWait(driver, 0, 500).ignoring(NotFoundException.class,
					ElementNotVisibleException.class);
			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			while (true)
			{
				LOG.debug("Wait half a second for the spinner to appear...");
				try
				{
					// wait for the spinner to appear, this may take a while
					halfSecondWait.until(visibilityOf(spinner));
				}
				catch (TimeoutException expected)
				{
					// half a second without spinner! we're done!
					return this;
				}
				long timeOutInSeconds = sw.elapsed(TimeUnit.SECONDS) - timeout;
				LOG.debug("Spinner showing. Wait {} more seconds for spinner to hide...", timeOutInSeconds);
				Wait<WebDriver> spinnerWait = new WebDriverWait(driver, timeOutInSeconds)
						.ignoring(NotFoundException.class, ElementNotVisibleException.class);
				spinnerWait.until(not(visibilityOf(spinner)));
				LOG.debug("Spinner hidden.");
			}
		}
		finally
		{
			driver.manage().timeouts().implicitlyWait(IMPLICIT_WAIT_SECONDS, TimeUnit.SECONDS);
		}
	}

}
