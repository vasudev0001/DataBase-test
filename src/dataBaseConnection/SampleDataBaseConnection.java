package dataBaseConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.util.HashMap;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/*
 * data base jar: mysql-connector-java-8.0.28
 */
public class SampleDataBaseConnection {
	WebDriver driver = null;

	@Test(dataProvider = "test")
	public void main(HashMap<String, String> data) throws SQLException {
		String url = data.get("url");
		String un = data.get("userName");
		String pw = data.get("password");
		loginValidation(url, un, pw);
	}

	public static Object[] getDataBase(String dataBaseName, String query, String tableName) throws SQLException {
		System.out.println("entered into getDataBase method.");
		// initialize HashMap with null. HashMap to store testData.
		HashMap<String, String> data = null;
		String port = "33061";

		// connect to the dataBase with valid crediantials.
		// url="jdbc:mysql://localhost:portNumber/Name of the dataBase";
		Connection c = DriverManager.getConnection("jdbc:mysql://localhost:" + port + "/" + dataBaseName, "root",
				"vasudevK@09");
		// Creates a Statement object for sendingSQL statements to the database.
		Statement s = c.createStatement();
		// Executes the given SQL statement, which returns a single ResultSet object.
		ResultSet rowCount = s.executeQuery(" SELECT COUNT(*) FROM " + tableName);
		rowCount.next();
		int count = rowCount.getInt(1);
		rowCount.close();
		System.out.println("Number of logininfo table rows: " + count);

		Object[] rowData = new Object[count];
		ResultSet rs = s.executeQuery(query);
		ResultSetMetaData md = rs.getMetaData();

		System.out.println("Table column size: " + md.getColumnCount());

		while (rs.next()) {
			data = new HashMap<>();
			for (int j = 1; j <= md.getColumnCount(); j++) {

				data.put(md.getColumnName(j), rs.getString(j));
				System.out.print(md.getColumnName(j) + ": " + rs.getString(j) + ",  ");
			}
			System.out.println();
			rowData[rs.getRow() - 1] = data;
		}
		rs.close();
		return rowData;
	}

	public void loginValidation(String url, String un, String pw) {
		By heading = By.xpath("//div[@class='mainHeading']/h2");
		By loginLabel = By.xpath("//input[@name='txtUserName']/../preceding-sibling::td");
		System.setProperty("webdriver.gecko.driver", "C:\\selenium\\geckodriver-v0.32.0-win64\\geckodriver.exe");
		driver = new FirefoxDriver();
		driver.get(url);
		System.out.println("entered url:" + url);
		driver.findElement(By.name("txtUserName")).sendKeys(un);
		System.out.println("entered user name: " + un);
		driver.findElement(By.name("txtPassword")).sendKeys(pw);
		System.out.println("enterd password: " + pw);
		driver.findElement(By.name("Submit")).click();
		System.out.println("click on submit button");
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));

		try {
			wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("rightMenu"));
			String heading1 = wait.until(d -> d.findElement(heading)).getText();
			System.out.println("Heading after login: " + heading1);
			System.out.println("Login successfully completed.");
		} catch (Exception e) {
			String label = driver.findElement(loginLabel).getText();
			System.out.println("Home page label: " + label);
			System.out.println("Login failed.");
		}
	}

	@AfterMethod
	public void quitBrowser() {
		driver.quit();
	}

	@DataProvider(name = "test")
	public Object[] getData() throws SQLException {
		String dataBaseName = "qadb";
		String query = " select * from loginHrm";
		String tableName = "loginHrm";
		Object[] d = getDataBase(dataBaseName, query, tableName);
		return d;
	}
}
