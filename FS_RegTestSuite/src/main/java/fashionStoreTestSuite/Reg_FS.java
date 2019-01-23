package fashionStoreTestSuite;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.google.gson.LongSerializationPolicy;

import freemarker.template.SimpleDate;

public class Reg_FS

{
	public WebDriver dr = null;
	public ExtentReports repo;
	public ExtentTest extentTest;
	public ExtentHtmlReporter htmlRepo;

	@BeforeTest
	public void Setup() {
		htmlRepo = new ExtentHtmlReporter(new File(System.getProperty("user.dir") + "/AutomationReports.html"));
		htmlRepo.loadXMLConfig(new File(System.getProperty("user.dir")+"/extent_config.xml"));
		repo = new ExtentReports();
		repo.setSystemInfo("Cluster", "Ares:32444");
		repo.attachReporter(htmlRepo);
	}
	
	@Parameters({"browser"})
	@Test
	public void launchBrowser(String brow) throws InterruptedException {

		if (brow.equalsIgnoreCase("chrome")) {
			//System.setProperty("webdriver.chrome.driver","C:\\Users\\JOSEPHGomes\\Documents\\TechBodhi_Sel\\Driver\\chromedriver.exe");
			ChromeOptions op = new ChromeOptions();
			op.addArguments("--disable-infobars");
			op.addArguments("--start-maxmized");
			dr = new ChromeDriver(op);
		} else if (brow.equalsIgnoreCase("firefox")) {
			dr = new FirefoxDriver();
		} else if (brow.equalsIgnoreCase("ie")) {
			dr = new InternetExplorerDriver();
		}

		dr.manage().window().maximize();
		
	}



	public static String getscreenshot(WebDriver dr, String screenshotname) throws IOException {
		String dateName = new SimpleDateFormat("yyyyMMddhhss").format(new Date());
		TakesScreenshot ts = (TakesScreenshot) dr;
		File source = ts.getScreenshotAs(OutputType.FILE);
		String destination = System.getProperty("user.dir") + "/FailedTestScreenshot" + screenshotname + dateName
				+ ".png";
		File finalDestination = new File(destination);
		FileUtils.copyFile(source, finalDestination);
		return destination;
	}

	@Test (priority = 1)
	public void Launch_FashionStore() 
	{
		// dr.get("http://athena.us-east.containers.appdomain.cloud:32019");
		dr.get("http://ares.us-east.containers.appdomain.cloud:32444");
		dr.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	}

	@Test(priority = 2)
	public void Go_Shopping() {
		dr.findElement(By.xpath("//*[contains(text(), 'Shop')]")).click();
	}

	@Test(priority = 3)
	public void Add_to_Cart() {
		dr.findElement(By.xpath("//*[contains(text(), 'add to cart')]")).click();
	}

	@Test(priority = 4)
	public void Go2Cart_andCheckout() {
		dr.findElement(By.xpath("//a[@href=\"/cart\"]")).click();
		dr.findElement(By.xpath("//*[contains(text(), 'CHECKOUT')]")).click();
	}

	@Test(priority = 5)
	public void Select_Bank() {
		WebDriverWait wait = new WebDriverWait(dr, 60);
		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("//img[contains(@src,'/static/media/bank_icon.81ab3fca.jpg')]")))
				.click();
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[contains(text(),'Forgerock')]"))).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[contains(text(), 'Pay Now')]"))).click();
	}

	@Test(priority = 6)
	public void Authenticate_AT_Forgerock() {
		WebDriverWait wait = new WebDriverWait(dr, 60);
		wait.until(ExpectedConditions.elementToBeClickable(By.name("callback_0")));
		dr.findElement(By.xpath("//input[@name='callback_0']")).clear();
		dr.findElement(By.xpath("//input[@name='callback_0']")).sendKeys("joieuser");
		dr.findElement(By.xpath("//input[@name='callback_1']")).clear();
		dr.findElement(By.xpath("//input[@name='callback_1']")).sendKeys("joiepassword");
		dr.findElement(By.xpath("//*[@id='loginButton_0']")).click();
	}

	@Test(priority = 7)
	public void Select_Account_Pay() {
		dr.findElement(By.xpath("//*[@id='mat-radio-2' and @class='mat-radio-button mat-accent']")).click();
		// HardCode WebElement
		// WebElement radiobtn =
		// dr.findElement(By.xpath("//*[contains(text(),'Bills')]"));
		// radiobtn.click();
		dr.findElement(By.xpath("//*[contains(text(), 'Allow')]")).click();
	}

	@Test(priority = 8)
	public void Payment_Successful() {
		dr.findElement(By.xpath("//*[contains(text(),'Your payment has been processed')]")).click();
	}

	@Test(priority = 9)
	public void Close_Browser() {
		dr.quit();
	}

	@BeforeMethod
	public void register(Method method) {
		String testName = method.getName();
		extentTest = repo.createTest(testName);
	}

	@AfterMethod
	public void captureStatus(ITestResult result) throws IOException {
		if (result.getStatus() == ITestResult.SUCCESS) {

			extentTest.log(Status.PASS, "The Test Method Named as : " + result.getName() + "is Pass");

		} else if (result.getStatus() == ITestResult.FAILURE) {
			extentTest.log(Status.FAIL, "The Test Method Named as : " + result.getName() + "is Failed");
			extentTest.log(Status.FAIL, "Test Failuse : " + result.getThrowable());
			String screenshotpath = Reg_FS.getscreenshot(dr, result.getName());
			extentTest.addScreenCaptureFromPath(screenshotpath);

		} else if (result.getStatus() == ITestResult.SKIP) {
			extentTest.log(Status.PASS, "The Test Method Named as : " + result.getName() + "is Pass");
		}
	}

	@AfterTest
	public void cleanup() {
		repo.flush();
	}

}
