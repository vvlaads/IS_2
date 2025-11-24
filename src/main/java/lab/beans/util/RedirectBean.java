package lab.beans.util;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import java.io.IOException;

@ManagedBean(name = "redirectBean")
@ApplicationScoped
public class RedirectBean {

    public void redirectTo(String page) {
        redirectTo(page, null);
    }

    public void redirectTo(String page, String query) {
        try {
            if (!page.endsWith(".xhtml")) {
                page += ".xhtml";
            }

            // Параметры
            if (query != null && !query.isEmpty()) {
                page += "?" + query;
            }

            FacesContext.getCurrentInstance().getExternalContext().redirect(page);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void toMovie(int id) {
        redirectTo("profiles/movie", "id=" + id);
    }

    public void toPerson(int id) {
        redirectTo("profiles/person", "id=" + id);
    }

    public void toLocation(int id) {
        redirectTo("profiles/location", "id=" + id);
    }

    public void toCoordinates(int id) {
        redirectTo("profiles/coordinates", "id=" + id);
    }
}

