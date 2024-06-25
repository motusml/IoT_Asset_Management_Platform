package DataManager.model.graphDB;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node
@Getter
@Setter
public class Device {

    @Id @GeneratedValue
    private String id;

    private String name;

    private Boolean isRegistered;


}
