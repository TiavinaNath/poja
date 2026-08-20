package api.poja.app.endpoint.rest.controller;

import api.poja.app.service.BlackAndWhiteImageService;
import java.net.URL;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
public class BlackAndWhiteImageController {

  private final BlackAndWhiteImageService blackAndWhiteImageService;

  @PostMapping(value = "/black-and-white", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public String convertToBlackAndWhite(@RequestParam("file") MultipartFile file) {
    URL presignedUrl = blackAndWhiteImageService.processAndUpload(file);
    return presignedUrl.toString();
  }
}
