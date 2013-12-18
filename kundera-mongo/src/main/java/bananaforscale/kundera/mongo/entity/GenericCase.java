package bananaforscale.kundera.mongo.entity;

import bananaforscale.kundera.mongo.gridfs.Attachment;
import com.impetus.kundera.index.Index;
import com.impetus.kundera.index.IndexCollection;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author Paul Dunlap
 */
@Entity
// The table "name" is equivalent to the collection you will be storing to. 
// The table "schema" is equivalent to the database.
// In this example we are using collection "users" under database "kunderaexamples"
@Table(name = "case", schema = "sava@mongo_pu")
@IndexCollection(columns = {
    @Index(name = "id"),
    @Index(name = "type"),
    @Index(name = "status")})
public class GenericCase implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    private String type;
    private String status;
    @ElementCollection
    private List<Attachment> attachments;

    public GenericCase() {
        // no args bean constructor
        attachments = new ArrayList<Attachment>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void addAttachment(Attachment attachment) {
        attachments.add(attachment);
    }

    public List<Attachment> getAttachments() {
        return Collections.unmodifiableList(attachments);
    }
}
