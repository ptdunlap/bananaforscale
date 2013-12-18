package bananaforscale.kundera.mongo.gridfs;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;
import com.mongodb.gridfs.GridFSInputFile;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.commons.io.FileUtils;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Paul Dunlap
 */
public class GridFsUtility {

    private static final Logger logger = LoggerFactory.getLogger(GridFsUtility.class);
    private final MongoClient mongoClient;
    private final DB mongoDb;
    private final String restUrl;
    private final Tika tika;
    private DatatypeFactory dtf;

    public GridFsUtility(String mongoHostName, Integer mongoPort, String mongoDatabaseName, String restUrl) throws UnknownHostException, DatatypeConfigurationException {
        this.mongoClient = new MongoClient(mongoHostName, mongoPort.intValue());
        this.mongoDb = mongoClient.getDB(mongoDatabaseName);
        this.restUrl = restUrl + "/" + "gridfs" + "/" + mongoDatabaseName;
        this.tika = new Tika();
        this.dtf = DatatypeFactory.newInstance();
    }

    /**
     * Adds a remote file to the database using MongoDB's GridFS specification.
     *
     * @param file the file to store
     * @param bucket the bucket (collection) to store the file in
     * @param overwriteExisting specifies whether to overwrite an existing file
     * with the same name
     * @return The REST endpoint where the file can be accessed
     * @throws UnknownHostException
     * @throws IOException
     */
    public Attachment addFile(URL url, String bucket, boolean overwriteExisting) throws MalformedURLException, IOException {
        String[] parts = url.toString().split("/");
        String fileName = parts[parts.length - 1]; // filename will be the last part
        File file = new File(fileName);
        FileUtils.copyURLToFile(url, file);
        return addFile(file, bucket, overwriteExisting);
    }

    /**
     * Adds a local file to the database using MongoDB's GridFS specification.
     *
     * @param file the file to store
     * @param bucket the bucket (collection) to store the file in
     * @param overwriteExisting specifies whether to overwrite an existing file
     * with the same name
     * @return The REST endpoint where the file can be accessed
     * @throws UnknownHostException
     * @throws IOException
     */
    public Attachment addFile(File file, String bucket, boolean overwriteExisting) throws UnknownHostException, IOException {
        GridFS gfsBucket = new GridFS(mongoDb, bucket);
        GridFSDBFile dbFile = gfsBucket.findOne(file.getName());
        String gfsFilePath = restUrl + "/" + bucket + "?filename=" + file.getName();
        String contentType = tika.detect(file);

        // if the file doesnt already exist in the database create it.
        if (dbFile == null) {
            GridFSInputFile gfsFile = gfsBucket.createFile(file);
            gfsFile.setFilename(file.getName());
            gfsFile.setContentType(contentType);
            gfsFile.save();
            return getFileMetaData(gfsFilePath, gfsFile);
        }

        // if the file already exists and the user doesnt want to overwrite then
        // skip uploading of file.
        if (!overwriteExisting) {
            logger.info("File already exists in the database: " + gfsFilePath);
            return getFileMetaData(gfsFilePath, dbFile);
        }

        // The file already exists and the user wants to overwrite
        gfsBucket.remove(file.getName());
        GridFSInputFile gfsFile = gfsBucket.createFile(file);
        gfsFile.setFilename(file.getName());
        gfsFile.setContentType(contentType);
        gfsFile.save();
        return getFileMetaData(gfsFilePath, gfsFile);
    }

    /**
     * Removes a file from the database using MongoDB's GridFS specification.
     *
     * @param fileName
     * @param bucket
     */
    public void removeFile(String fileName, String bucket) {
        GridFS gfsBucket = new GridFS(mongoDb, bucket);
        gfsBucket.remove(fileName);
    }

    /**
     * Shutdowns the connection to MongoDB for this instance.
     */
    public void shutdown() {
        mongoClient.close();
    }

    /**
     * Packages the file metadata into a JSON friendly object.
     *
     * @param url
     * @param file
     * @return
     */
    private Attachment getFileMetaData(String url, GridFSFile file) {
        Attachment meta = new Attachment();
        meta.setContentType(file.getContentType());
        meta.setFileName(file.getFilename());
        meta.setUrl(url);

        // set upload date with XML Gregorian XML format
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(file.getUploadDate());
        XMLGregorianCalendar uploadDate = dtf.newXMLGregorianCalendar(gc);
        meta.setUploadDate(uploadDate.toXMLFormat());
        return meta;
    }
}
