package com.example.whatwillyoube.whatwillyoube_backend.util;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.IOException;

/**
 * HTTP 응답 시 'Transfer-Encoding: chunked' 대신 명시적인 'Content-Length' 헤더를 설정하기 위한 컨버터.
 * 특정 클라이언트(예: 일부 AI 서버, 레거시 시스템)가 Chunked 응답을 처리하지 못하는 호환성 문제를 해결하기 위해,
 * 직렬화된 데이터의 크기를 미리 계산하여 Content-Length 헤더에 포함
 */
public class FixedLengthJsonMessageConverter extends MappingJackson2HttpMessageConverter {

    /**
     * 객체를 JSON으로 직렬화했을 때의 실제 바이트 크기를 계산하여 반환
     *
     * @param object      직렬화할 대상 객체
     * @param contentType 미디어 타입 (application/json)
     * @return 직렬화된 JSON 데이터의 바이트 길이
     * @throws IOException 직렬화 중 오류 발생 시
     */
    @Override
    protected Long getContentLength(Object object, MediaType contentType) throws IOException {
        return calculateSize(object);
    }

    /**
     * Jackson ObjectMapper를 사용하여 객체를 JSON 문자열로 변환한 뒤,
     * 해당 문자열의 UTF-8 바이트 길이를 계산
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
