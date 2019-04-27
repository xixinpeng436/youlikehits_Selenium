import com.sun.deploy.util.SystemUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class MyWebDriver {
    public static WebDriver WebDriver;

    public static Long getTime() {
        if(time==null){
            time= System.currentTimeMillis()/1000;
        }
        return time;
    }

    public static void setTime(Long time) {
        MyWebDriver.time = time;
    }

    public static Long time;

    public static org.openqa.selenium.WebDriver getWebDriver() {
        if(WebDriver==null){
            WebDriver=new ChromeDriver();
        }
        return WebDriver;
    }

    public static void setWebDriver(org.openqa.selenium.WebDriver webDriver) {
        WebDriver = webDriver;
    }
}
