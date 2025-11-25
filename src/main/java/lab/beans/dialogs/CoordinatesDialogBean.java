package lab.beans.dialogs;

import lab.beans.util.UpdateBean;
import lab.data.Coordinates;
import lab.database.DatabaseManager;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = "coordinatesDialogBean")
@ViewScoped
public class CoordinatesDialogBean {
    @EJB
    private DatabaseManager databaseManager;
    private UpdateBean updateBean;
    private Coordinates coordinates = new Coordinates();

    private boolean editing = false;

    @PostConstruct
    public void init() {
        FacesContext context = FacesContext.getCurrentInstance();
        updateBean = context.getApplication()
                .evaluateExpressionGet(context, "#{updateBean}", UpdateBean.class);
    }

    public void openAddDialog() {
        coordinates = new Coordinates();
        editing = false;
    }

    public void openEditDialog(int id) {
        coordinates = databaseManager.getObjectById(Coordinates.class, id);
        editing = true;
    }

    public void addCoordinates() {
        databaseManager.addObject(coordinates);
        updateBean.increaseVersion();
    }

    public void updateCoordinates() {
        databaseManager.updateObject(coordinates);
        updateBean.increaseVersion();
    }


    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public boolean isEditing() {
        return editing;
    }

    public void setEditing(boolean editing) {
        this.editing = editing;
    }
}
