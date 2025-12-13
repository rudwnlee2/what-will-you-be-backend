package com.example.whatwillyoube.whatwillyoube_backend.util;

import com.fasterxml.jackson.core.JsonGenerator;
import org.apache.commons.io.output.CountingOutputStream;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.IOException;
import java.io.OutputStream;

/**
 * JSON 객체를 직렬화하여 실제 Content-Length를 계산하는 MessageConverter.
 * 이 컨버터를 사용하면 Transfer-Encoding: chunked 대신 Content-Length 헤더가 설정됩니다.
 */
public class FixedLengthJsonMessageConverter extends MappingJackson2HttpMessageConverter {

    /**
     * 객체를 직렬화하여 실제 바이트 크기를 계산하고, 그 값을 Content-Length로 반환합니다.
     */
    @Override
    protected Long getContentLength(Object object, MediaType contentType) throws IOException {
        // 실제 데이터 전송 없이 크기만 계산하기 위해 "dry run"을 수행합니다.
        return calculateSize(object);
    }

    /**
     * Jackson ObjectMapper를 사용하여 객체를 JSON으로 변환할 때의 크기를 계산합니다.
     * CountingOutputStream을 사용하여 실제 출력 없이 바이트 수만 셉니다.
     *
     * @param value 크기를 계산할 Java 객체
     * @return 직렬화된 JSON의 바이트 크기
     * @throws IOException 직렬화 중 오류 발생 시
     */
    private long calculateSize(Object value) throws IOException {
        // 직접 JSON 문자열로 변환 후 UTF-8 바이트 크기 계산
        String jsonString = getObjectMapper().writeValueAsString(value);
        return jsonString.getBytes("UTF-8").length;
    }
}
