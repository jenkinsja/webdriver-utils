package java.com.jenkinsja.webdriverutils;

import org.openqa.selenium.WebDriver;
import java.lang.reflect.Field;

public class PageObject<T extends PageObject<T>> {
    
    public PageObject(){
        
    }
    
    //Location Helpers
    
    //Page Loading
    public T WaitUntilLoaded(){
        
        return (T)this;
    }
    
    //Page Actions
    
}