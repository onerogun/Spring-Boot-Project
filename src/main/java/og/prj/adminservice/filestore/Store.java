package og.prj.adminservice.filestore;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

@Service
public class Store {

    private final AmazonS3 amazonS3;

    @Autowired
    public Store(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    public void save(String path, String fileName, Optional<Map<String,String>> optionalMetaData,
                     InputStream inputStream) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        optionalMetaData.ifPresent(map ->
                map.forEach((key, value) ->
                        objectMetadata.addUserMetadata(key,value)
                )
        );

        try {
            amazonS3.putObject(path,fileName,inputStream,objectMetadata);
        } catch (AmazonServiceException ex) {
            throw new IllegalStateException("Storage attempt failed ", ex);
        }
    }

    public byte[] download(String path, String key) {
        try {
            S3Object s3Object = amazonS3.getObject(path, key);
            return IOUtils.toByteArray(s3Object.getObjectContent());
        } catch (AmazonServiceException | IOException ex) {
            throw new IllegalStateException("Download attempt failed ", ex);
        }
    }
}
