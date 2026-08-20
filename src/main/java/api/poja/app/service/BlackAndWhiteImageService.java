package api.poja.app.service;

import static java.io.File.createTempFile;
import static java.util.UUID.randomUUID;

import api.poja.app.file.bucket.BucketComponent;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.time.Duration;
import javax.imageio.ImageIO;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
public class BlackAndWhiteImageService {

  private final BucketComponent bucketComponent;

  private static final String BUCKET_DIRECTORY = "black-and-white/";
  private static final Duration PRESIGNED_URL_EXPIRATION = Duration.ofHours(1);

  @SneakyThrows
  public URL processAndUpload(MultipartFile multipartFile) {
    BufferedImage originalImage = ImageIO.read(multipartFile.getInputStream());
    if (originalImage == null) {
      throw new IllegalArgumentException("Invalid or unsupported image file");
    }

    BufferedImage bwImage =
        new BufferedImage(
            originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
    Graphics2D graphics = bwImage.createGraphics();
    graphics.drawImage(originalImage, 0, 0, null);
    graphics.dispose();

    File tempFile = createTempFile("bw-" + randomUUID(), ".png");
    try {
      ImageIO.write(bwImage, "png", tempFile);
      String bucketKey = BUCKET_DIRECTORY + tempFile.getName();
      bucketComponent.upload(tempFile, bucketKey);
      return bucketComponent.presign(bucketKey, PRESIGNED_URL_EXPIRATION);
    } finally {
      tempFile.delete();
    }
  }
}
