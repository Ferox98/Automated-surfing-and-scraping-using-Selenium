import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/* Author: Kaleab B. Belay
   Description: This program logs into the university's website portal and exports grade information in .txt format
 */
public class GradeFetcher {
    public static String init_url = "https://portal.aait.edu.et";
    public static String home_url = "https://portal.aait.edu.et/Home";
    public static String grade_url = "https://portal.aait.edu.et/Grade/GradeReport";

    public static String username = "your_university_id";
    public static String password = "your 4-digit password";

    public static String[] courseInformation = {
            "Course Number",
            "Course Title",
            "Course Code",
            "Course Credit Hour",
            "Course ECTS",
            "Course Grade"
    };

    public static WebDriver init() {
        System.setProperty("webdriver.gecko.driver", "C:\\Gecko\\geckodriver.exe");
        // Create an instance of the Webdriver
        return new FirefoxDriver();
    }
    public static void login(WebDriver driver) {
        // enter email
        driver.findElement(By.id("UserName")).click();
        driver.findElement(By.id("UserName")).sendKeys(username);

        // enter password
        driver.findElement(By.id("Password")).click();
        driver.findElement(By.id("Passowrd")).sendKeys(password);

        driver.findElement(By.className("btn-success")).click();
    }

    public static void main(String[] args) {
        // open browser window
        WebDriver driver = init();
        // goto url
        driver.get(init_url);
        // attempt login
        login(driver);
        // check if login was successful
        if(driver.getCurrentUrl().equals(home_url)) {
            // go to grade page
            driver.navigate().to(grade_url);
            // Write grades to file
            try {
                FileWriter fw = new FileWriter("grades.txt");
                // fetch table from site
                WebElement table = driver.findElement(By.xpath("//*[@class='table table-bordered table-striped table-hover']"));
                // fetch each row from the table
                List<WebElement> rows = table.findElements(By.xpath("..//tbody/tr"));

                StringBuilder builder;
                for(WebElement e : rows) {
                    builder = new StringBuilder();
                    if(e.getAttribute("class").equals("yrsm")) {
                        String semester_info = e.getText();
                        if(semester_info.contains("Academic year")) {
                            builder.append("Semester information: \t").append(e.getText()).append("\n");
                            builder.append("-----------------------------------------------------------");
                        }
                        else {
                            builder.append("\tOverall semester information: \t");
                            builder.append(e.getText());
                            builder.append("\n");
                            builder.append("-----------------------------------------------------------");
                            builder.append("\n");
                        }
                    }
                    else {
                        builder.append("\tCourse Information: \n");
                        List<WebElement> gradeInfos = e.findElements(By.xpath(".//td"));
                        for(int i = 0; i < gradeInfos.size(); i++) {
                            WebElement gradeInfo = gradeInfos.get(i);
                            if(gradeInfos.indexOf(gradeInfo) < courseInformation.length-1) {
                                builder.append("\t\t").append(courseInformation[i]).append(":\t").append(gradeInfo.getText()).append("\n");
                                i++;
                            }
                            else
                                break;
                        }
                        builder.append("------------------------------------------------");
                    }
                    fw.write(builder.toString());
                }
                driver.quit();
            } catch(IOException e) {

            }
        }
    }
}
