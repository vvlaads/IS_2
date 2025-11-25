package lab.beans.profiles;

import lab.beans.util.Updatable;
import lab.beans.util.UpdateBean;
import lab.data.Movie;
import lab.database.DatabaseManager;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = "movieProfileBean")
@SessionScoped
public class MovieProfileBean implements Updatable {
    @EJB
    private DatabaseManager databaseManager;
    private long lastKnownVersion = -1;
    private UpdateBean updateBean;

    private Movie movie;
    private int id;

    @PostConstruct
    public void init() {
        FacesContext context = FacesContext.getCurrentInstance();
        updateBean = context.getApplication()
                .evaluateExpressionGet(context, "#{updateBean}", UpdateBean.class);
        lastKnownVersion = updateBean.getVersion();
        updateTable();
    }

    @Override
    public void checkForUpdates() {
        long currentVersion = updateBean.getVersion();
        if (currentVersion != lastKnownVersion) {
            lastKnownVersion = currentVersion;
            updateTable();
        }
    }

    @Override
    public void updateTable() {
        movie = databaseManager.getObjectById(Movie.class, id);
    }

    public Movie getMovie() {
        return movie;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        updateTable();
    }
}
