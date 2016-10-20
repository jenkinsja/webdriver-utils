package java.com.jenkinsja.webdriverutils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.lang.reflect.Field;
import java.lang.Class;
import java.lang.annotation.Annotation;
import java.util.List;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class PageObject<T extends PageObject<T>> {
    
    private WebDriver driver;
    private WebDriverWait wait;
    
    public PageObject(WebDriver driver){
        this.driver = driver;
        wait = new WebDriverWait(driver, 30);
    }
    
    //Location Helpers
    
    //Page Loading
    /**
     * Wait for each of the class's fields to meet the qualifications specified
     * by the annotations on that field.
     * When all these conditions are met, we consider the page to be loaded.
     */
    public T WaitUntilLoaded(){
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
    public void WaitForClickableField(Field field){
        
    }
    
    /**
     * Wait for the field with the Existence annotation to be in the DOM
     */
    public void WaitForExistenceField(Field field){
        
    }
    
    /**
     * Wait for the field with the Visible annotation to be visible on the page
     */
    public void WaitForVisibleField(Field field) {
        try{
            if (field.getGenericType() instanceof WebElement){
                WebElement element = (WebElement)(field.get(this));
                wait.until(ExpectedConditions.visibilityOf(element));
            } else if (field.getGenericType() instanceof List<?>){
                List<WebElement> elements = (List<WebElement>)(field.get(this));
                
            }
        } catch (IllegalAccessException e) {
            
        }
    }
    
    public static void WaitUntil(final WebDriver driver) {
        // Wait for the console panel to load
       (new WebDriverWait(driver, 15)).until(new ExpectedCondition<Boolean>() {
          public Boolean apply(WebDriver d) {
             List<WebElement>elements = driver.findElements(By.id(
                   ElementIds.getElementId(ElementIds.CONSOLE_INPUT)));
             return elements.size() > 0;
          }
       });
    }
    
    //Page Actions
    
}