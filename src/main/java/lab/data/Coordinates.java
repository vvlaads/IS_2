package lab.data;

import lab.util.DBObject;
import lab.util.annotations.NotNull;

import javax.persistence.*;

@Entity
@Table(name = "coordinates")
public class Coordinates implements DBObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    @NotNull
    private Long x; //Поле не может быть null
    @Column
    private long y;

    public Coordinates() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Long getX() {
        return x;
    }

    public void setX(Long x) {
        this.x = x;
    }

    public long getY() {
        return y;
    }

    public void setY(long y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "{" + "x: " + x + ", y: " + y + "}";
    }
}
