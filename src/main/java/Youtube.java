import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.json.XML;
import org.openqa.selenium.*;
import java.io.File;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Set;
import javax.imageio.ImageIO;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class Youtube {
    private static String YoutubeAccount="";//youlikehits账号
    private static String YoutubePassword="";//youlikehits密码
    private static String RuokuaiAccount="";//若快账号
    private static String RuokuaiPassword="";//若快密码
    private static String imgpath="C:/test.jpg";//验证码默认位置
    static{
        System.getProperties().setProperty("webdriver.chrome.driver","H:\\chromedriver_win32\\chromedriver.exe");//chromedriver.exe位置
    }

    public static  void main(String [] arg){
        WebDriver driver = MyWebDriver.getWebDriver();
        driver.manage().window().maximize();
        driver.get("https://www.youlikehits.com/login.php");
        WebElement name= driver.findElement(By.id("username"));
        name.sendKeys(YoutubeAccount);
        WebElement pwd= driver.findElement(By.id("password"));
        pwd.sendKeys(YoutubePassword);
        List<WebElement> subs=driver.findElements(By.xpath("//input"));
        subs.get(2).click();
        new Thread(new Runnable() {
            public void run() {
                while(true){
                    try {
                        getanswer(MyWebDriver.getWebDriver());
                        Thread.sleep(2000);
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }){}.start();
        if(driver.getCurrentUrl().indexOf("stats.php")!=-1){
            driver.get("https://www.youlikehits.com/youtubenew2.php");
        }
    }
    public static void getanswer(WebDriver driver) throws Exception {
        WebElement answer=null;
        try {
             answer= driver.findElement(By.name("answer"));
        }catch (Exception e){
            answer=null;
        }
        if(answer!=null){//当出现答题页面时
            WebElement ele = driver.findElement(By.xpath("//*[@id=\"captcha\"]/table[1]/tbody/tr/td/img"));
//            Utils.waitABit(2000);
            File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
            BufferedImage fullImg = ImageIO.read(screenshot);  // 读取截图
            // 得到页面元素
            org.openqa.selenium.Point point= ele.getLocation();
            // 得到长、宽
            int eleWidth= ele.getSize().getWidth();
            int eleHeight= ele.getSize().getHeight();
            BufferedImage eleScreenshot= fullImg.getSubimage(point.getX(), point.getY(), eleWidth, eleHeight);
            ImageIO.write(eleScreenshot, "png", screenshot);
            // copy 把图片放对应的生成目录下
            File screenshotLocation = new File(imgpath);
            FileUtils.copyFile(screenshot, screenshotLocation);
            String num=gettxapi(imgpath);
            answer.sendKeys(num);
            WebElement submit= driver.findElement(By.name("submit"));
            submit.click();
        }else{
            //视频播放页
            if(driver.getCurrentUrl().indexOf("youtubenew2")!=-1){
                Long date = System.currentTimeMillis() / 1000;
                if(driver.getWindowHandles().size()<2) {
                    Thread.sleep(2000);
                    driver.findElement(By.className("followbutton")).click();
                    if ((date - MyWebDriver.getTime() < 10 && date - MyWebDriver.getTime() > 3) || date - MyWebDriver.getTime() > 150) {
                        driver.get("https://www.youlikehits.com/youtubenew2.php");
                    }
                    MyWebDriver.setTime(System.currentTimeMillis() / 1000);
                }else{
                    if ( date - MyWebDriver.getTime() > 150) {
                        Set<String>sts=driver.getWindowHandles();
                        String s=driver.getWindowHandle();
                        for(String sss:sts){
                            if(!sss.equals(s)){
                                driver.switchTo().window(sss);
                                driver.close();
                                driver.switchTo().window(s);
                            }
                        }
                        driver.get("https://www.youlikehits.com/youtubenew2.php");
                        MyWebDriver.setTime(System.currentTimeMillis() / 1000);
                    }
                }
            }
        }
    }
    private static String  addjs(){
        String ss="";
        ss+="var head= document.getElementsByTagName('head')[0];";
        ss+="var script= document.createElement('script');";
        ss+="script.type= 'text/javascript';";
        ss+=" script.onload = script.onreadystatechange = function() {";
        ss+="    if (!this.readyState || this.readyState === \"loaded\" || this.readyState === \"complete\" ) {";
        ss+="     script.onload = script.onreadystatechange = null;";
        ss+="  } };";
        ss+=" script.src= 'https://cdn.jsdelivr.net/gh/naptha/tesseract.js/dist/tesseract.min.js';";
        ss+=" head.appendChild(script);";
        return ss;
    }
    private static void  loadjs(String ss){
        JavascriptExecutor javascriptExecutor= (JavascriptExecutor) MyWebDriver.getWebDriver();
        javascriptExecutor.executeScript(ss);
    }
    private static String getyzm(){
        String ss="";
        ss+="Tesseract.recognize($(\"img[src*='captchayt']\").attr(\"src\")).then(equation => {\n";
        ss+="var formula = equation.text;\n";
        ss+="if (formula.length = 3) {\n";
        ss+="   if (formula.substr(1, 1) == 7) {\n";
        ss+="        formula = formula.substr(0, 1) + \"-\" + formula.substr(2);\n";
        ss+="   }\n";
        ss+="   formula = formula.replace(\"x\", \"*\") \n";
        ss+="   $(\"input[name='answer']\").val(eval(formula));\n";
        ss+="   $(\"input[value='Submit']\").first().click();\n";
        ss+="  }\n";
        ss+=" });\n";
        System.out.println(ss);
        return ss;
    }

    /**
     * 调用若快打码
     * @param imgpath
     * @return
     * @throws Exception
     */
    private static String gettxapi(String imgpath) throws Exception{
        String result = "";
        result = RuoKuai.createByPost(RuokuaiAccount, RuokuaiPassword, "5000", "90", "1", "b40ffbee5c1cf4e38028c197eb2fc751", imgpath);
        JSONObject obj=XML.toJSONObject(result);
        System.out.println(result);
        return ""+obj.getJSONObject("Root").get("Result");
    }
}
