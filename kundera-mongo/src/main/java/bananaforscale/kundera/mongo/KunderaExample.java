package bananaforscale.kundera.mongo;

import bananaforscale.kundera.mongo.dao.KunderaDao;
import bananaforscale.kundera.mongo.dao.SimpleKunderaDao;
import bananaforscale.kundera.mongo.entity.GenericCase;
import bananaforscale.kundera.mongo.gridfs.Attachment;
import bananaforscale.kundera.mongo.gridfs.GridFsUtility;
import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import javax.xml.datatype.DatatypeConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Paul Dunlap
 */
public class KunderaExample {

    private static Logger logger = LoggerFactory.getLogger(KunderaExample.class);

    public static void main(String[] args) {
        try {
            KunderaDao dao = new SimpleKunderaDao("mongo_pu");
            // create case
            GenericCase gc1 = new GenericCase();
            gc1.setId("CASE-0001");
            gc1.setStatus("NEW");
            gc1.setType("UNKNOWN");

            GenericCase gc2 = new GenericCase();
            gc2.setId("CASE-0002");
            gc2.setStatus("ONGOING");
            gc2.setType("UNKNOWN");

            // create and add files
            GridFsUtility gridFs = new GridFsUtility(
                    dao.getHostName(), Integer.valueOf(dao.getHostPort()),
                    dao.getDatabaseName(), dao.getRestUrl());

            URL imageUrl = new URL("http://mammoth-holiday.com/wp-content/uploads/2013/05/mammoth.jpg");
            Attachment fileMetadata = gridFs.addFile(imageUrl, "image", true);
            gc1.addAttachment(fileMetadata);

            URL kmlUrl = new URL("https://developers.google.com/kml/documentation/KML_Samples.kml");
            fileMetadata = gridFs.addFile(kmlUrl, "kml", true);
            gc2.addAttachment(fileMetadata);

            // cleanup the gridFS resource
            gridFs.shutdown();

            // save the case to mongo
            dao.saveOrUpdate(gc1);
            dao.saveOrUpdate(gc2);

//            dao.removeAll(GenericCase.class);

        } catch (UnknownHostException ex) {
            logger.error("An error occurred.", ex);
        } catch (IOException ex) {
            logger.error("An error occurred.", ex);
        } catch (DatatypeConfigurationException ex) {
            logger.error("An error occurred.", ex);
        }
    }
}
