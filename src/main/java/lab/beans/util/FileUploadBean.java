package lab.beans.util;

import lab.database.DatabaseManager;
import org.primefaces.model.file.UploadedFile;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = "fileUploadBean")
@RequestScoped
public class FileUploadBean {
    @EJB
    private DatabaseManager databaseManager;

    private UploadedFile file;

    public void upload() {
        try {
            if (file == null) {
                showError("Не выбран файл");
                return;
            }

            byte[] fileContent = file.getContent();
            String fileName = file.getFileName();

            if (fileName == null || !fileName.toLowerCase().endsWith(".json")) {
                showError("Неверный тип файла");
                return;
            }

            boolean result = databaseManager.importObjects(fileContent);
            if (result) {
                showMessage("Успешно выполнено", "Все объекты были добавлены");
            } else {
                showError("Ошибка при добавлении объектов");
            }
        } catch (Exception e) {
            showError("Внутренняя ошибка: " + e.getMessage());
        }
    }

    private void showMessage(String summary, String detail) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail));
    }

    private void showError(String detail) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка", detail));
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }
}
