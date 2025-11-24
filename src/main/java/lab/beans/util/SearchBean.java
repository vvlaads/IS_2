package lab.beans.util;

import lab.beans.util.RedirectBean;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import java.util.Arrays;
import java.util.List;

@ManagedBean(name = "searchBean")
@RequestScoped
public class SearchBean {
    private RedirectBean redirectBean;

    private String className;
    private final List<String> classNameList = Arrays.asList("Movie", "Person", "Location", "Coordinates");
    private int id;

    @PostConstruct
    public void init() {
        FacesContext context = FacesContext.getCurrentInstance();
        redirectBean = context.getApplication()
                .evaluateExpressionGet(context, "#{redirectBean}", RedirectBean.class);
    }

    public void findByID() {
        switch (className) {
            case "Movie":
                redirectBean.toMovie(id);
                break;
            case "Person":
                redirectBean.toPerson(id);
                break;
            case "Location":
                redirectBean.toLocation(id);
                break;
            case "Coordinates":
                redirectBean.toCoordinates(id);
                break;
            default:
                redirectBean.redirectTo("index");
                break;
        }
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<String> getClassNameList() {
        return classNameList;
    }

    private RedirectBean getRedirectBean() {
        return redirectBean;
    }
}
