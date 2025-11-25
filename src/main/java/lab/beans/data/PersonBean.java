package lab.beans.data;

import lab.beans.util.Updatable;
import lab.beans.util.UpdateBean;
import lab.data.Person;
import lab.database.DatabaseManager;
import lab.data.enums.Color;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean(name = "personBean")
@SessionScoped
public class PersonBean implements Updatable {
    @EJB
    private DatabaseManager databaseManager;
    private List<Person> filteredPersonList;

    private UpdateBean updateBean;
    private long lastKnownVersion = -1;

    private String nameFilter;
    private String eyeColorFilter;
    private String hairColorFilter;
    private String passportIDFilter;

    private String sortByColumn;
    private final List<String> sortColumns = Arrays.asList("Нет", "По имени", "По цвету глаз", "По цвету волос");


    @PostConstruct
    public void init() {
        FacesContext context = FacesContext.getCurrentInstance();
        updateBean = context.getApplication()
                .evaluateExpressionGet(context, "#{updateBean}", UpdateBean.class);
        lastKnownVersion = updateBean.getVersion();
        updateTable();
    }

    public void checkForUpdates() {
        long currentVersion = updateBean.getVersion();
        if (currentVersion != lastKnownVersion) {
            lastKnownVersion = currentVersion;
            updateTable();
        }
    }

    public void updateTable() {
        applyFiltersAndSort();
    }

    public void applyFiltersAndSort() {
        applyFilters();
        applySort();
    }

    public void applyFilters() {
        List<Person> allPersons = databaseManager.getObjectList(Person.class);

        filteredPersonList = allPersons.stream()
                .filter(p -> nameFilter == null || nameFilter.isEmpty() || p.getName().equals(nameFilter))
                .filter(p -> eyeColorFilter == null || eyeColorFilter.isEmpty() || p.getEyeColor().name().equalsIgnoreCase(eyeColorFilter))
                .filter(p -> hairColorFilter == null || hairColorFilter.isEmpty() || p.getHairColor().name().equalsIgnoreCase(hairColorFilter))
                .filter(p -> passportIDFilter == null || passportIDFilter.isEmpty() || p.getPassportID().equals(passportIDFilter))
                .collect(Collectors.toList());
    }

    public void removeFilters() {
        nameFilter = null;
        eyeColorFilter = null;
        hairColorFilter = null;
        passportIDFilter = null;
        applyFiltersAndSort();
    }

    public void applySort() {
        if (sortByColumn == null || sortByColumn.equals("Нет")) {
            filteredPersonList = filteredPersonList.stream()
                    .sorted(Comparator.comparing(Person::getId))
                    .collect(Collectors.toList());
            return;
        }

        switch (sortByColumn) {
            case "По имени":
                filteredPersonList = filteredPersonList.stream()
                        .sorted(Comparator.comparing(Person::getName))
                        .collect(Collectors.toList());
                break;
            case "По цвету глаз":
                filteredPersonList = filteredPersonList.stream()
                        .sorted(Comparator.comparing(Person::getEyeColor))
                        .collect(Collectors.toList());
                break;
            case "По цвету волос":
                filteredPersonList = filteredPersonList.stream()
                        .sorted(Comparator.comparing(Person::getHairColor))
                        .collect(Collectors.toList());
                break;
            default:
                filteredPersonList = filteredPersonList.stream()
                        .sorted(Comparator.comparing(Person::getId))
                        .collect(Collectors.toList());
                break;
        }
    }

    public List<Color> getColorList() {
        return Arrays.asList(Color.values());
    }

    public List<Person> getFilteredPersonList() {
        return filteredPersonList;
    }

    public void setFilteredPersonList(List<Person> filteredPersonList) {
        this.filteredPersonList = filteredPersonList;
    }

    public String getNameFilter() {
        return nameFilter;
    }

    public void setNameFilter(String nameFilter) {
        this.nameFilter = nameFilter;
    }

    public String getEyeColorFilter() {
        return eyeColorFilter;
    }

    public void setEyeColorFilter(String eyeColorFilter) {
        this.eyeColorFilter = eyeColorFilter;
    }

    public String getHairColorFilter() {
        return hairColorFilter;
    }

    public void setHairColorFilter(String hairColorFilter) {
        this.hairColorFilter = hairColorFilter;
    }

    public String getPassportIDFilter() {
        return passportIDFilter;
    }

    public void setPassportIDFilter(String passportIDFilter) {
        this.passportIDFilter = passportIDFilter;
    }

    public String getSortByColumn() {
        return sortByColumn;
    }

    public void setSortByColumn(String sortByColumn) {
        this.sortByColumn = sortByColumn;
    }

    public List<String> getSortColumns() {
        return sortColumns;
    }

    public List<Person> getPersonList() {
        return databaseManager.getObjectList(Person.class);
    }
}
