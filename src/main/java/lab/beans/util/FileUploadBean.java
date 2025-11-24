package lab.beans.util;

import org.primefaces.model.file.UploadedFile;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = "fileUploadBean")
@RequestScoped
public class FileUploadBean {
    private UploadedFile file;

    public void upload() {
        if (file == null) {
            addError("Не выбран файл");
            return;
        }

        try {
            byte[] fileContent = file.getContent();
            String fileName = file.getFileName();

            if (fileName.endsWith(".json")) {
                System.out.println("JSON get + hello hello hello\n\n\n");
                addMessage("Готово", "Получилось");
            }
        } catch (Exception e) {
            addError("Ошибка при загрузке файла: " + e.getMessage());
        }
    }

    private void addMessage(String summary, String detail) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail));
    }

    private void addError(String detail) {
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
