package org.example;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

public class first_test {

    private WebDriver driver;
    private WebDriverWait wait;

    @Before
    public void before() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        System.setProperty("webdriver.chrome.driver", "src/test/resources/chromedriver.exe");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

        wait = new WebDriverWait(driver, 10, 1000);

        String baseUrl = "https://www.rgs.ru/";
        driver.get(baseUrl);
    }

    @Test
    public void test() {

        closeRibbon();
        String closeCookies = "//*[@class='btn btn-default text-uppercase']";
        WebElement closeCookiesBtn = driver.findElement(By.xpath(closeCookies));
        closeCookiesBtn.click();

        //Открыть меню
        String menuBtnXPath = "//*[@class='dropdown adv-analytics-navigation-line1-link current']/a";
        WebElement menuBtn = driver.findElement(By.xpath(menuBtnXPath));
        menuBtn.click();

        //Перейти на вкладку "Компаниям"
        String companiesBtnXPath = "//a[contains(text(),'Компаниям')]";
        WebElement companiesBtn = driver.findElement(By.xpath(companiesBtnXPath));
        companiesBtn.click();

        closeRibbon();
        //Перейти на "Страхование здоровья"
        String wellnessInsuranceXPath = "//a[contains(text(),'Страхование здоровья')]";
        WebElement wellnessInsuranceBtn = driver.findElement(By.xpath(wellnessInsuranceXPath));
        scrollToElement(wellnessInsuranceBtn);
        wellnessInsuranceBtn.click();

        ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
        String newWindow = tabs.get(1);
        driver = driver.switchTo().window(newWindow);



        //Перейти на страницу расчета стоимости ДМС
        String healthInsuranceXPath = "//a[contains(text(),'полиса ДМС')]";
        WebElement healthInsuranceBtn = driver.findElement(By.xpath(healthInsuranceXPath));
        scrollToElement(healthInsuranceBtn);
        healthInsuranceBtn.click();

        closeRibbon();
        //Проверить соответствие заголовка
        String titleXPath = "//h1";
        WebElement titleBtn = driver.findElement(By.xpath(titleXPath));
        Assert.assertEquals("Заголовок не соответствует",
                "ДМС — добровольное медицинское страхование", titleBtn.getText());

        //Открыть окно заполнения заявки
        String sendApplicationXPath = "//a[contains(text(), 'Отправить заявку')]";
        WebElement sendApplicationBtn = driver.findElement(By.xpath(sendApplicationXPath));
        sendApplicationBtn.click();

        closeRibbon();
        String applicationFieldsInputXPath = "//label[ contains(text(), '%s')]/../input";
        fillInputField(driver.findElement(By.xpath
                (String.format(applicationFieldsInputXPath, "Фамилия"))), "Иванов");
        fillInputField(driver.findElement(By.xpath
                (String.format(applicationFieldsInputXPath, "Имя"))), "Иван");
        fillInputField(driver.findElement(By.xpath
                (String.format(applicationFieldsInputXPath, "Отчество"))), "Иванович");
        fillInputField(driver.findElement(By.xpath
                (String.format(applicationFieldsInputXPath, "Эл. почта"))), "qwertyqwerty");


        WebElement phone = driver.findElement(By.xpath(String.format(applicationFieldsInputXPath, "Телефон")));
        phone.click();
        //phone.clear();
        phone.sendKeys("0123456789");
        boolean checkFlag = wait.until(ExpectedConditions.attributeContains(phone, "value", "+7 (012) 345-67-89"));
        Assert.assertTrue("Поле заполнено некорректно", checkFlag);





        String regionXPath = "//select[ @name = 'Region']";
        Select regionField = new Select(driver.findElement(By.xpath(regionXPath)));
        regionField.selectByVisibleText("Москва");

        //CheckBox согласен с обработкой персонвльных данных
        String checkBoxXPath = "//input[@class='checkbox']";
        WebElement checkBox = driver.findElement(By.xpath(checkBoxXPath));
        checkBox.click();

        //Отправить заявку
        String btnSendXpath = "//button[@id= 'button-m']";
        WebElement sendBth = driver.findElement(By.xpath(btnSendXpath));
        sendBth.click();

        String checkLabelXPath = "//span[contains(text(), 'Введите адрес электронной почты')]/..";
        WebElement checkLabel = null;
        try {
            checkLabel = driver.findElement(By.xpath(checkLabelXPath));
        }catch (Exception e){}
        Assert.assertNotNull("Сообщение об ошибке отсутствует", checkLabel);

    }

    @After
    public void after() {
        driver.quit();
    }


    private void scrollToElement(WebElement element) {
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
        javascriptExecutor.executeScript("arguments[0].scrollIntoView(true);", element);
    }

    private void fillInputField(WebElement element, String value) {
        scrollToElement(element);
        element.click();
        element.clear();
        element.sendKeys(value);
        //Проверка что поля заполнены введенными значениями
        boolean checkFlag = wait.until(ExpectedConditions.attributeContains(element, "value", value));
        Assert.assertTrue("Поле заполнено некорректно", checkFlag);
    }

    private void closeRibbon(){
        driver.manage().timeouts().implicitlyWait(500, TimeUnit.MILLISECONDS);


        try{
            String frameRibbon = "//*[@class='flocktory-widget']";
            WebElement frame = driver.findElement(By.xpath(frameRibbon));
            driver.switchTo().frame(frame);
            String strRibbon = "//*[@class='Ribbon-close']";
            WebElement closeRibbonBtn = driver.findElement(By.xpath(strRibbon));
            closeRibbonBtn.click();
        }
        catch (Exception e){}
        finally {
            driver.switchTo().defaultContent();
            driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        }
    }

}