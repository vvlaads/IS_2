package lab.util;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lab.data.Coordinates;
import lab.data.Location;
import lab.data.Movie;
import lab.data.Person;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Movie.class, name = "movie"),
        @JsonSubTypes.Type(value = Person.class, name = "person"),
        @JsonSubTypes.Type(value = Location.class, name = "location"),
        @JsonSubTypes.Type(value = Coordinates.class, name = "coordinates")
})
public interface DBObject {
    int getId();
}
