package hillel.qaauto;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;
import java.util.List;

public class StackDemoBase {
    WebDriver driver;
    WebDriverWait wait;
    Actions actions;
    String messageInEmptyBag = "Add some products in the bag\n" + ":)";
    String messageInBuyButtonIfBagEmpty = "CONTINUE SHOPPING";
    String messageInBuyButtonIfBagNotEmpty = "CHECKOUT";
    String itemBoxByIdXpath = "//div[@id='%d']";
    String itemIdAddToBagXpath = "//div[@id='%d']//div[text()='Add to cart']";
    String allItemsInBagNamesXpath = "//p[@class='title']";
    String allItemsInBagPricesXpath = "//div[@class='shelf-item__price']//p";
    String qtyItemsInBagXpath = "//span[@class='bag bag--float-cart-closed']//span";
    String closeBagButtonXpath = "//div[@class='float-cart__close-btn']";
    String openBagButtonXpath = "//span[@class='bag bag--float-cart-closed']";
    String bagOpenFlagXpath = "//div[@class='float-cart float-cart--open']";
    String totalPriceInBagXpath = "//p[@class='sub-price__val']";
    String checkOutButtonXpath = "//div[@class='buy-btn']";
    String deleteItemFromBagButtonXpath = "//div[@class='shelf-item__del']";
    String messageInEmptyBagXpath = "//p[@class='shelf-empty']";

    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://www.bstackdemo.com/");
    }

    @AfterMethod
    public void tearDown() {
        driver.quit();
    }

    public String checkItemNameByIdOnMainPage(int id) {
        String allTextFromItemBlock = driver.findElement(By.xpath(String.format(itemBoxByIdXpath, id))).getText();
        String[] allTextFromBlockList = allTextFromItemBlock.split("\n");
        return allTextFromBlockList[0];
    }

    public Double checkItemPriceByIdOnMainPage(int id) {
        String allTextFromItemBlock = driver.findElement(By.xpath(String.format(itemBoxByIdXpath, id))).getText();
        String[] allTextFromBlockList = allTextFromItemBlock.split("\n");
        return Double.valueOf(allTextFromBlockList[1].split("\\$")[1]);
    }

    public String checkItemNameByOrderInBag(int positionInBag) {
        openBag();
        List<WebElement> itemsNamesInBag = driver.findElements(By.xpath(allItemsInBagNamesXpath));
        String itemName = itemsNamesInBag.get(positionInBag - 1).getText();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(closeBagButtonXpath))).click();

        return itemName;
    }

    public double checkItemPriceByOrderInBag(int positionInBag) {
        openBag();
        List<WebElement> itemsPricesInBag = driver.findElements(By.xpath(allItemsInBagPricesXpath));
        return Double.parseDouble(itemsPricesInBag.get(positionInBag - 1).getText().split("\\$")[1]);
    }

    public void addItemsInBagInOrderOnMainPage(int qtyItems) {
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        if (qtyItems > 25 || qtyItems < 0) {
            System.out.println("Wrong argument. Possible from 1 to 25.");
        } else {
            for (int i = 1; i <= qtyItems; i++) {
                driver.findElement(By.xpath(String.format(itemIdAddToBagXpath, i))).click();
                wait.until(ExpectedConditions.elementToBeClickable(By.xpath(closeBagButtonXpath))).click();
            }
        }
    }

    public int checkQtyItemsInBag() {
        return Integer.parseInt(driver.findElement(By.xpath(qtyItemsInBagXpath)).getText());
    }

    public double checkTotalPriceInBag() {
        openBag();
        return Double.parseDouble(driver.findElement(By.xpath(totalPriceInBagXpath)).getText().split(" ")[1]);
    }

    public boolean availableCheckoutButton() {
        openBag();
        return driver.findElement(By.xpath(checkOutButtonXpath)).isDisplayed();
    }

    public void openBag() {
        try {
            wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            actions = new Actions(driver);
            WebElement openBagButton = driver.findElement(By.xpath(openBagButtonXpath));
            actions.moveToElement(openBagButton).click(openBagButton).perform();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(bagOpenFlagXpath)));
        } catch (NoSuchElementException ignored) {
        }
    }

    public void deleteItemFromBagByPosition(int position) {
        openBag();
        List<WebElement> itemsForDeletingFromBag = driver.findElements(By.xpath(deleteItemFromBagButtonXpath));
        itemsForDeletingFromBag.get(position - 1).click();
    }

    public boolean checkCorrectMessageInEmptyBag() {
        openBag();
        return driver.findElement(By.xpath(messageInEmptyBagXpath)).getText().equals(messageInEmptyBag);
    }

    public boolean checkCorrectMessageInBuyButton(boolean bagEmpty) {
        openBag();
        if (bagEmpty) {
            return driver.findElement(By.xpath(checkOutButtonXpath)).getText().equals(messageInBuyButtonIfBagEmpty);
        } else {
            return driver.findElement(By.xpath(checkOutButtonXpath)).getText().equals(messageInBuyButtonIfBagNotEmpty);
        }
    }
}
