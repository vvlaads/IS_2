package lab.beans.dialogs;

import lab.beans.util.UpdateBean;
import lab.data.Location;
import lab.database.DatabaseManager;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = "locationDialogBean")
@ViewScoped
public class LocationDialogBean {
    @EJB
    private DatabaseManager databaseManager;
    private UpdateBean updateBean;
    private Location location = new Location();

    private boolean editing = false;

    @PostConstruct
    public void init() {
        FacesContext context = FacesContext.getCurrentInstance();
        updateBean = context.getApplication()
                .evaluateExpressionGet(context, "#{updateBean}", UpdateBean.class);
    }

    public void openAddDialog() {
        location = new Location();
        editing = false;
    }

    public void openEditDialog(int id) {
        location = databaseManager.getLocationById(id);
        editing = true;
    }

    public void addLocation() {
        databaseManager.addObject(location);
        updateBean.increaseVersion();
    }

    public void updateLocation() {
        databaseManager.updateObject(location);
        updateBean.increaseVersion();
    }


    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean isEditing() {
        return editing;
    }

    public void setEditing(boolean editing) {
        this.editing = editing;
    }
}
