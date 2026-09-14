package com.sprint.mission.discodeit.common.exception;

import com.sprint.mission.discodeit.common.exception.UploadedFileReadException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(OutputCaptureExtension.class)
class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ThrowingController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void uploadedFileReadFailureReturnsInternalServerError(CapturedOutput output) throws Exception {
        mockMvc.perform(get("/test/uploaded-file-read"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("서버에서 요청을 처리하지 못했습니다."));

        assertThat(output).contains("업로드된 파일을 읽지 못했습니다.");
    }

    @Test
    void invalidMultipartRequestReturnsBadRequest(CapturedOutput output) throws Exception {
        mockMvc.perform(get("/test/multipart"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("파일 업로드 요청 형식이 올바르지 않습니다."));

        assertThat(output).contains("multipart 요청을 처리하지 못했습니다.");
    }

    @Test
    void oversizedUploadReturnsPayloadTooLarge(CapturedOutput output) throws Exception {
        mockMvc.perform(get("/test/max-upload-size"))
                .andExpect(status().isPayloadTooLarge())
                .andExpect(jsonPath("$.status").value(413))
                .andExpect(jsonPath("$.message").value("업로드 가능한 파일 크기를 초과했습니다."));

        assertThat(output).contains("업로드 허용 용량을 초과했습니다.");
    }

    @RestController
    private static class ThrowingController {

        @GetMapping("/test/uploaded-file-read")
        void throwUploadedFileReadException() {
            throw new UploadedFileReadException(
                    "테스트 파일을 읽지 못했습니다.",
                    new IOException("테스트 I/O 예외")
            );
        }

        @GetMapping("/test/multipart")
        void throwMultipartException() {
            throw new MultipartException("잘못된 multipart 요청");
        }

        @GetMapping("/test/max-upload-size")
        void throwMaxUploadSizeExceededException() {
            throw new MaxUploadSizeExceededException(1024L);
        }
    }
}
