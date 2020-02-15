package og.prj.adminservice.photo;



import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class PhotoService {
 
    @Autowired
    private PhotoRepository photoRepository;
 
    public String addPhoto(MultipartFile file) throws IOException {
        Photo photo = new Photo();
        photo.setImage(
          new Binary(BsonBinarySubType.BINARY, file.getBytes()));
        photo = photoRepository.save(photo);
        return photo.getId();
    }
 
    public Photo getPhoto(String id) { 
        return photoRepository.findById(id).get();
    }
}