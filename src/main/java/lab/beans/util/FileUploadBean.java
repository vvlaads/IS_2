package lab.beans.util;

import org.primefaces.model.file.UploadedFile;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

@ManagedBean(name = "fileUploadBean")
@RequestScoped
public class FileUploadBean {
    private UploadedFile file;

    public void upload() {
        if (file != null) {
            try {
                // Получаем файл в память
                byte[] fileContent = file.getContent();
                String fileName = file.getFileName();

                // НЕМЕДЛЕННАЯ обработка
                if (fileName.endsWith(".json")) {
                    System.out.println("JSON get + hello hello hello\n\n\n");
                }
            } catch (Exception e) {
                // Обработка ошибки
            }
        }
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }
}
