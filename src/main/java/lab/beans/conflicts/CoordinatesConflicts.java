package lab.beans.conflicts;

import lab.beans.util.Updatable;
import lab.beans.util.UpdateBean;
import lab.data.Coordinates;
import lab.data.Movie;
import lab.database.DatabaseManager;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.util.List;

@ManagedBean(name = "coordinatesConflicts")
@SessionScoped
public class CoordinatesConflicts implements Updatable {
    @EJB
    private DatabaseManager databaseManager;
    private int coordinatesId;
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

    public void findMoviesWithConflicts(int coordinatesId) {
        this.coordinatesId = coordinatesId;
        updateTable();
    }

    public void deleteCoordinates() {
        if (allowed) {
            databaseManager.deleteObject(Coordinates.class, coordinatesId);
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
        conflictMovieList = databaseManager.findMoviesByCoordinatesId(coordinatesId);
        allowed = conflictMovieList.isEmpty();
    }

    public void setCoordinatesId(int coordinatesId) {
        this.coordinatesId = coordinatesId;
    }

    public int getCoordinatesId() {
        return coordinatesId;
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
