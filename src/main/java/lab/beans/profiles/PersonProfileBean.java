package lab.beans.profiles;

import lab.beans.util.Updatable;
import lab.beans.util.UpdateBean;
import lab.data.Person;
import lab.database.DatabaseManager;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = "personProfileBean")
@SessionScoped
public class PersonProfileBean implements Updatable {
    @EJB
    private DatabaseManager databaseManager;
    private long lastKnownVersion = -1;
    private UpdateBean updateBean;

    private int id;
    private Person person;

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
        person = databaseManager.getPersonById(id);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        updateTable();
    }

    public Person getPerson() {
        return person;
    }
}
