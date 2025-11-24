package lab.beans.util;

import org.primefaces.PrimeFaces;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import java.util.Arrays;
import java.util.List;

@ManagedBean(name = "addBean")
@RequestScoped
public class AddBean {
    private String className;

    private final List<String> classNameList = Arrays.asList("Movie", "Person", "Location", "Coordinates");

    public void showDialog() {
        if (className == null) return;

        String widgetVar = null;
        switch (className) {
            case "Movie":
                widgetVar = "movieDialog";
                break;
            case "Person":
                widgetVar = "personDialog";
                break;
            case "Location":
                widgetVar = "locationDialog";
                break;
            case "Coordinates":
                widgetVar = "coordinatesDialog";
                break;
        }

        if (widgetVar != null) {
            PrimeFaces.current().executeScript("PF('" + widgetVar + "').show()");
        }
    }


    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<String> getClassNameList() {
        return classNameList;
    }
}
