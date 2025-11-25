package lab.beans.profiles;

import lab.beans.util.Updatable;
import lab.beans.util.UpdateBean;
import lab.data.Coordinates;
import lab.database.DatabaseManager;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = "coordinatesProfileBean")
@SessionScoped
public class CoordinatesProfileBean implements Updatable {
    @EJB
    private DatabaseManager databaseManager;
    private UpdateBean updateBean;
    private long lastKnownVersion = -1;

    private int id;
    private Coordinates coordinates;

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
        coordinates = databaseManager.getObjectById(Coordinates.class, id);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        updateTable();
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }
}
