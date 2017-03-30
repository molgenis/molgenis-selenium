package org.molgenis.selenium.model.component;

import static java.util.Arrays.asList;
import static org.openqa.selenium.support.ui.ExpectedConditions.not;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.NoSuchElementException;
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
	public static final int IMPLICIT_WAIT_SECONDS = 30;
	private static final int SPINNER_APPEAR_TIMEOUT = 1;

	private static final Logger LOG = LoggerFactory.getLogger(SpinnerModel.class);

	private final WebDriver driver;

	@FindBy(id = "spinner")
	private WebElement spinner;

	public SpinnerModel(WebDriver driver)
	{
		this.driver = driver;
	}

	/**
	 * Waits for a certain amount of seconds for a period of one full second without spinner.
	 */
	public SpinnerModel waitTillDone(long timeout, TimeUnit unit)
	{
		try
		{
			LOG.info("Wait for spinner...");
			Stopwatch sw = Stopwatch.createStarted();
			noExplicitWait();
			internalWaitTillDone(unit.toSeconds(timeout), sw);
			return this;
		}
		finally
		{
			restoreImplicitWait();
		}
	}

	private void restoreImplicitWait()
	{
		driver.manage().timeouts().implicitlyWait(SpinnerModel.IMPLICIT_WAIT_SECONDS, TimeUnit.SECONDS);
	}

	private void noExplicitWait()
	{
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
	}

	/**
	 * Waits for the spinner to have disappeared for at least one second within a certain timespan
	 * 
	 * @param timeout
	 *            the timespan to wait for
	 * @param sw
	 *            {@link Stopwatch} that keeps track of time since we started waiting
	 */
	private void internalWaitTillDone(long timeout, Stopwatch sw)
	{
		while (waitForSpinnerToAppear(SPINNER_APPEAR_TIMEOUT))
		{
			waitForSpinnerToHide(checkTimeLeft(timeout, sw));
		}
	}

	/**
	 * Checks now much time is left until timeout.
	 * 
	 * @param timeout
	 *            timeout in seconds
	 * @param sw
	 *            {@link Stopwatch} that keeps track of time since we started waiting
	 * @return seconds left to wait
	 */
	private long checkTimeLeft(long timeout, Stopwatch sw)
	{
		long timeOutInSeconds = timeout - sw.elapsed(TimeUnit.SECONDS);
		if (timeOutInSeconds <= 0)
		{
			throw new TimeoutException("Spinner did not stop showing for " + timeout + " seconds.");
		}
		return timeOutInSeconds;
	}

	/**
	 * Waits for the spinner to hide. Throws exception if timeout is exceeded.
	 * 
	 * @param timeOutInSeconds
	 *            timeout to wait for
	 */
	private void waitForSpinnerToHide(long timeOutInSeconds)
	{
		LOG.debug("Spinner showing. Wait {} more seconds for spinner to hide...", timeOutInSeconds);
		Wait<WebDriver> spinnerWait = new WebDriverWait(driver, timeOutInSeconds).ignoreAll(
				asList(NotFoundException.class, ElementNotVisibleException.class, NoSuchElementException.class));
		spinnerWait.until(webDriver -> not(visibilityOf(spinner)));
		LOG.debug("Spinner hidden.");
	}

	/**
	 * Waits for the spinner to appear.
	 * 
	 * @param seconds
	 * @return
	 */
	private boolean waitForSpinnerToAppear(int seconds)
	{
		Wait<WebDriver> secondWait = new WebDriverWait(driver, seconds).ignoring(NotFoundException.class,
				ElementNotVisibleException.class);
		LOG.debug("Wait one second for the spinner to appear...");
		try
		{
			// wait for the spinner to appear, this may take a while
			secondWait.until(webDriver -> visibilityOf(spinner));
			return true;
		}
		catch (TimeoutException expected)
		{
			LOG.info("Done.");
			// one second without spinner! we're done!
			return false;
		}
	}

}
