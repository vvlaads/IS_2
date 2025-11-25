package lab.beans.data.util;

import lab.beans.util.Updatable;
import lab.beans.util.UpdateBean;
import lab.data.util.Operation;
import lab.database.DatabaseManager;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.util.List;

@ManagedBean(name = "operationBean")
@SessionScoped
public class OperationBean implements Updatable {
    @EJB
    private DatabaseManager databaseManager;
    private List<Operation> operationList;

    private UpdateBean updateBean;
    private long lastKnownVersion = -1;


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
        operationList = databaseManager.getObjectList(Operation.class);
        System.out.println("New Operation list: " + operationList.size() + "\n\n\n");
    }

    public List<Operation> getOperationList() {
        if (operationList == null) {
            operationList = databaseManager.getObjectList(Operation.class);
        }
        return operationList;
    }
}
