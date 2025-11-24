package lab.beans.conflicts;

import lab.beans.util.Updatable;
import lab.beans.util.UpdateBean;
import lab.data.Movie;
import lab.data.Person;
import lab.database.DatabaseManager;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.util.List;

@ManagedBean(name = "personConflicts")
@SessionScoped
public class PersonConflicts implements Updatable {
    @EJB
    private DatabaseManager databaseManager;
    private int personId;
    private List<Movie> conflictMovieList;
    private UpdateBean updateBean;
    private long lastKnownVersion;
    private boolean allowed = false;

    @PostConstruct
    public void init() {
        FacesContext context = FacesContext.getCurrentInstance();
        updateBean = context.getApplication()
                .evaluateExpressionGet(context, "#{updateBean}", UpdateBean.class);
        lastKnownVersion = updateBean.getVersion();
        updateTable();
    }

    public void findMoviesWithConflicts(int personId) {
        this.personId = personId;
        updateTable();
    }

    public void deletePerson() {
        if (allowed) {
            databaseManager.deleteObject(Person.class, personId);
            updateBean.increaseVersion();
        }
    }

    @Override
    public void checkForUpdates() {
        if (lastKnownVersion != updateBean.getVersion()) {
            lastKnownVersion = updateBean.getVersion();
            updateTable();
        }
    }

    @Override
    public void updateTable() {
        conflictMovieList = databaseManager.findMoviesByPersonId(personId);
        allowed = conflictMovieList.isEmpty();
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public List<Movie> getConflictMovieList() {
        return conflictMovieList;
    }

    public long getLastKnownVersion() {
        return lastKnownVersion;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }
}
