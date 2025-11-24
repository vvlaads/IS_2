package lab.beans.dialogs;

import lab.beans.util.UpdateBean;
import lab.data.Person;
import lab.data.enums.Color;
import lab.database.DatabaseManager;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.util.Arrays;
import java.util.List;

@ManagedBean(name = "personDialogBean")
@ViewScoped
public class PersonDialogBean {
    @EJB
    private DatabaseManager databaseManager;
    private UpdateBean updateBean;
    private Person person = new Person();
    private Integer selectedLocationId;

    private boolean editing = false;

    @PostConstruct
    public void init() {
        FacesContext context = FacesContext.getCurrentInstance();
        updateBean = context.getApplication()
                .evaluateExpressionGet(context, "#{updateBean}", UpdateBean.class);
    }

    public void openAddDialog() {
        person = new Person();
        editing = false;
    }

    public void openEditDialog(int id) {
        person = databaseManager.getPersonById(id);
        selectedLocationId = person.getLocation() != null ? person.getLocation().getId() : null;
        editing = true;
    }

    public void addPerson() {
        person.setLocation(databaseManager.getLocationById(selectedLocationId));
        databaseManager.addObject(person);
        updateBean.increaseVersion();
    }

    public void updatePerson() {
        person.setLocation(databaseManager.getLocationById(selectedLocationId));
        databaseManager.updateObject(person);
        updateBean.increaseVersion();
    }


    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public boolean isEditing() {
        return editing;
    }

    public void setEditing(boolean editing) {
        this.editing = editing;
    }

    public Integer getSelectedLocationId() {
        return selectedLocationId;
    }

    public void setSelectedLocationId(Integer selectedLocationId) {
        this.selectedLocationId = selectedLocationId;
    }

    public List<Color> getColorList() {
        return Arrays.asList(Color.values());
    }
}
