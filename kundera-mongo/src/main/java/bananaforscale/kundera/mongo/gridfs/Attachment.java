package bananaforscale.kundera.mongo.gridfs;

import java.io.Serializable;
import javax.persistence.Embeddable;

/**
 *
 * @author Paul Dunlap
 */
@Embeddable
public class Attachment implements Serializable {

    private String contentType;
    private String fileName;
    private String uploadDate; // XMLGregorianCalendar XML Format
    private String url;

    public Attachment() {
        // no args bean constructor
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "FileMetadata{" + "contentType=" + contentType + ", fileName=" + fileName + ", uploadDate=" + uploadDate + ", url=" + url + '}';
    }
}
