package java.com.jenkinsja.webdriverutils;

import java.lang.annotation.Annotation;
import java.lang.Class;
import java.util.List;
import java.util.logging.Logger;
import java.lang.reflect.Field;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Generic class that represents a page object.
 * This class facilitates the finding of elements on the page, or underneath
 * an element on the page.
 * It also takes care of synchronization with the WaitUntilLoaded method, which will
 * reflexively inspect the fields of class, and wait until each of those fields meets
 * the annotated states of those fields.
 * Finally, this class facilitates common page actions, taking care of synchronization,
 * logging, and the action itself.
 */
public class PageObject<T extends PageObject<T>> {
    
    private final static Logger LOGGER = Logger.getLogger(PageObject.class.getName());
    private WebDriver driver;
    private WebDriverWait wait;
    
    public PageObject(WebDriver driver){
        this.driver = driver;
        wait = new WebDriverWait(driver, 30);
    }
    
    //Location Helpers
    /**
     * Pass-through to driver
     */
    public WebElement FindElement(By by){
        return driver.findElement(by);
    }
    
    /**
     * Pass-through to driver
     */
    public List<WebElement> FindElements(By by){
        return driver.findElements(by);
    }
    
    /**
     * Find element under root
     */
    public WebElement FindElement(By by, WebElement root){
        return root.findElement(by);
    }
    
    /**
     * Find elements under root
     */
    public List<WebElement> FindElements(By by, WebElement root){
        return root.findElements(by);
    }
    
    //Page Loading
    /**
     * Wait for each of the class's fields to meet the qualifications specified
     * by the annotations on that field.
     * When all these conditions are met, we consider the page to be loaded.
     */
    protected T WaitUntilLoaded(){
        Field[] fields = this.getClass().getFields();
        for (Field field : fields){
            Annotation[] annotations = field.getDeclaredAnnotations();
            for (Annotation annotation : annotations){
                String annotationName = annotation.toString();
                if (annotationName.equals("Clickable")){
                    WaitForClickableField(field);
                } else if (annotationName.equals("Existence")){
                    WaitForExistenceField(field);
                } else if (annotationName.equals("Visible")){
                    WaitForVisibleField(field);
                }
            }
        }
        return (T)this;
    }
    
    /**
     * Wait for the field with the Clickable annotation to be visible and enabled
     */
    private void WaitForClickableField(Field field){
        try{
            if (field.getGenericType() instanceof WebElement){
                WebElement element = (WebElement)(field.get(this));
                wait.until(ExpectedConditions.elementToBeClickable(element));
            } else if (field.getGenericType() instanceof List<?>){
                List<WebElement> elements = (List<WebElement>)(field.get(this));
                wait.until(ElementsClickable(elements));
            }
        } catch (IllegalAccessException e) {
            LOGGER.info("Could not determine field type");
        }
    }
    
    /**
     * Wait for the field with the Existence annotation to be in the DOM
     */
    private void WaitForExistenceField(Field field){
        try{
            if (field.getGenericType() instanceof WebElement){
                WebElement element = (WebElement)(field.get(this));
                //If we have the element, then it exists
            } else if (field.getGenericType() instanceof List<?>){
                List<WebElement> elements = (List<WebElement>)(field.get(this));
                //If we have a list of elements, then they exists
            }
        } catch (IllegalAccessException e) {
            LOGGER.info("Could not determine field type");
        }
    }
    
    /**
     * Wait for the field with the Visible annotation to be visible on the page
     */
    private void WaitForVisibleField(Field field) {
        try{
            if (field.getGenericType() instanceof WebElement){
                WebElement element = (WebElement)(field.get(this));
                wait.until(ExpectedConditions.visibilityOf(element));
            } else if (field.getGenericType() instanceof List<?>){
                List<WebElement> elements = (List<WebElement>)(field.get(this));
                wait.until(ElementsVisible(elements));
            }
        } catch (IllegalAccessException e) {
            LOGGER.info("Could not determine field type");
        }
    }
    
    /**
     * Checks if one of the elements in the list is visible
     */
    private static ExpectedCondition<Boolean> ElementsVisible(final List<WebElement> elements) {
    	return new ExpectedCondition<Boolean>() {
    		@Override
    		public Boolean apply(WebDriver webDriver) {
                for (WebElement element : elements){
                    if (element.isDisplayed()){
                        return true;
                    }
                }
                return false;
            }
        };
    }
    
    /**
     * Checks if one of the elements in the list is clickable
     */
    private static ExpectedCondition<Boolean> ElementsClickable(final List<WebElement> elements) {
    	return new ExpectedCondition<Boolean>() {
    		@Override
    		public Boolean apply(WebDriver webDriver) {
                for (WebElement element : elements){
                    if (element.isDisplayed() && element.isEnabled()){
                        return true;
                    }
                }
                return false;
            }
        };
    }
    
    //Page Actions
    public T ClickButton(WebElement element, String name){
        LOGGER.info("Clicking element " + name);
        wait.until(ExpectedConditions.elementToBeClickable(element));
        element.click();
        LOGGER.info("Done clicking element " + name);
        return (T)this;
    }
    
    public T SendKeys(WebElement element, String keys, String name){
        LOGGER.info("Sending test \"" + keys + "\" to " + name);
        element.clear();
        element.sendKeys(keys);
        LOGGER.info("Done sending test \"" + keys + "\" to " + name);
        return (T)this;
    }
}