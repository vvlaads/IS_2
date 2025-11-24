package lab.data;

import lab.util.DBObject;
import lab.util.annotations.NotNull;

import javax.persistence.*;

@Entity
@Table(name = "locations")
public class Location implements DBObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private float x;
    @Column
    private double y;
    @Column(nullable = false)
    @NotNull
    private Integer z; //Поле не может быть null

    public Location() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public Integer getZ() {
        return z;
    }

    public void setZ(Integer z) {
        this.z = z;
    }

    @Override
    public String toString() {
        return "{" + "x: " + x + ", y: " + y + ", z: " + z + "}";
    }
}
