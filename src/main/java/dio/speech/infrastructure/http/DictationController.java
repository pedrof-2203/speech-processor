package dio.speech.infrastructure.http;

import dio.speech.application.usecase.dictation.DictateTransactionUseCase;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/dictate")
public class DictationController {
    private final DictateTransactionUseCase dictateTransactionUseCase;

    public DictationController(DictateTransactionUseCase dictateTransactionUseCase) {
        this.dictateTransactionUseCase = dictateTransactionUseCase;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "audio/wav")
    public ResponseEntity<Resource> transcribe(@RequestParam("file") MultipartFile file) throws IOException {

        var result = dictateTransactionUseCase.execute(file.getResource());

        if (result.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        var resource = new ByteArrayResource(result.get());

        return ResponseEntity.ok().contentType(MediaType.parseMediaType("audio/wav")).body(resource);
    }
}
