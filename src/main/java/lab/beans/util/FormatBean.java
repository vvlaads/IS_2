package lab.beans.util;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

@ManagedBean(name = "formatBean")
@RequestScoped
public class FormatBean {
    public String formatMoney(long amount) {
        if (amount >= 1_000_000) {
            return String.format("%.1f млн", amount / 1_000_000.0);
        } else if (amount >= 1_000) {
            return String.format("%.0f тыс", amount / 1_000.0);
        } else {
            return String.valueOf(amount);
        }
    }

    public String formatDate(String date) {
        return date;
    }
}
