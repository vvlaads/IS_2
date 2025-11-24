package lab.beans.util;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

@ManagedBean(name = "updateBean")
@ApplicationScoped
public class UpdateBean {
    private long version = 0;

    public void increaseVersion() {
        this.version += 1;
    }

    public long getVersion() {
        return version;
    }
}
