package lab.data.util;

import lab.util.DBObject;
import lab.util.annotations.Min;
import lab.util.annotations.NotEmpty;
import lab.util.annotations.NotNull;

import javax.persistence.*;

@Entity
@Table(name = "operations")
public class Operation implements DBObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private boolean completed;
    @Column
    @NotEmpty
    @NotNull
    private String username;
    @Column
    @NotNull
    @Min(0)
    private int count;

    public Operation() {
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
