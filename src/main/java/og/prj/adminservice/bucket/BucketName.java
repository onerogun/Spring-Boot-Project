package og.prj.adminservice.bucket;

public enum BucketName {

    IMAGE("product-images-storage1313");

    private final String bucketName;

    BucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getBucketName() {
        return bucketName;
    }
}
