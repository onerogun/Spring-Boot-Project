package og.prj.adminservice.filestore;

import og.prj.adminservice.bucket.BucketName;
import og.prj.adminservice.product.Product;
import og.prj.adminservice.product.ProductRepository;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class FileStorageService {

    @Autowired
    private ProductRepository productRepository;

    private Store fileStore;

    @Autowired
    public FileStorageService(Store fileStore) {
        this.fileStore = fileStore;
    }

    public void saveToBucket(Long id, MultipartFile image) {

        productRepository.findById(id)
                .orElseThrow( () -> new IllegalStateException(String.format("Product with id: %s doesn't exist!", id)));


        if (image.isEmpty()) {
            throw new IllegalStateException("EMPTY IMAGE!");
        }

        isFileImage(image);

        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", image.getContentType());
        metadata.put("Content-Length", String.valueOf(image.getSize()));

        String path = getPath(id);

        try {
            fileStore.save(path, image.getOriginalFilename(), Optional.of(metadata), image.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Product product = productRepository.findById(id).get();
        product.setProductImageName(image.getOriginalFilename());
        productRepository.save(product);

    }

    public byte[] downloadFromBucket(Long productId) {

        productRepository.findById(productId).orElseThrow( () -> new IllegalStateException(String.format("Product with id: %s doesn't exist!", productId)));

        String productImageName = productRepository.findById(productId).get().getProductImageName();

        String path = getPath(productId);

        return fileStore.download(path, productImageName);
    }

    private String getPath(Long productId) {
        return String.format("%s/%s", BucketName.IMAGE.getBucketName(), String.valueOf(productId));
    }

    private void isFileImage(MultipartFile image) {
        if (!Arrays.asList(ContentType.IMAGE_JPEG.getMimeType(),
                ContentType.IMAGE_PNG.getMimeType()).contains(image.getContentType())
        ) {
            throw new IllegalStateException("File is not an Image! : " + image.getContentType());
        }
    }
}
