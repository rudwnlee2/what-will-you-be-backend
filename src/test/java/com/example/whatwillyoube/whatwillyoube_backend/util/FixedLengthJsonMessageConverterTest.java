package com.example.whatwillyoube.whatwillyoube_backend.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FixedLengthJsonMessageConverter 통합 테스트")
class FixedLengthJsonMessageConverterTest {

    private FixedLengthJsonMessageConverter converter;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        converter = new FixedLengthJsonMessageConverter();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("간단한 문자열의 실제 JSON 크기와 계산된 Content-Length 일치 검증")
    void getContentLength_SimpleString_MatchesActualJsonSize() throws IOException {
        // given
        String testString = "Hello World";
        
        // when
        Long contentLength = converter.getContentLength(testString, MediaType.APPLICATION_JSON);
        
        // then
        String actualJson = objectMapper.writeValueAsString(testString);
        byte[] actualBytes = actualJson.getBytes();
        
        assertEquals(actualBytes.length, contentLength.intValue());
        assertEquals(13, contentLength);
    }

    @Test
    @DisplayName("Map 객체의 실제 JSON 크기와 계산된 Content-Length 일치 검증")
    void getContentLength_MapObject_MatchesActualJsonSize() throws IOException {
        // given
        Map<String, Object> testMap = new HashMap<>();
        testMap.put("name", "테스트");
        testMap.put("age", 25);
        testMap.put("active", true);
        
        // when
        Long contentLength = converter.getContentLength(testMap, MediaType.APPLICATION_JSON);
        
        // then
        String actualJson = objectMapper.writeValueAsString(testMap);
        byte[] actualBytes = actualJson.getBytes("UTF-8");
        
        assertEquals(actualBytes.length, contentLength.intValue());
    }

    @Test
    @DisplayName("null 객체 처리")
    void getContentLength_NullObject_MatchesActualJsonSize() throws IOException {
        // given
        Object nullObject = null;
        
        // when
        Long contentLength = converter.getContentLength(nullObject, MediaType.APPLICATION_JSON);
        
        // then
        String actualJson = objectMapper.writeValueAsString(nullObject);
        byte[] actualBytes = actualJson.getBytes();
        
        assertEquals(actualBytes.length, contentLength.intValue());
        assertEquals(4, contentLength);
    }

    @Test
    @DisplayName("빈 객체 처리")
    void getContentLength_EmptyObject_MatchesActualJsonSize() throws IOException {
        // given
        Map<String, Object> emptyMap = new HashMap<>();
        
        // when
        Long contentLength = converter.getContentLength(emptyMap, MediaType.APPLICATION_JSON);
        
        // then
        String actualJson = objectMapper.writeValueAsString(emptyMap);
        byte[] actualBytes = actualJson.getBytes();
        
        assertEquals(actualBytes.length, contentLength.intValue());
        assertEquals(2, contentLength);
    }

    @Test
    @DisplayName("특수문자 포함 객체 - UTF-8 인코딩 정확성 검증 (디버깅)")
    void getContentLength_SpecialCharacters_MatchesActualJsonSize() throws IOException {
        // given
        Map<String, String> specialMap = new HashMap<>();
        specialMap.put("korean", "안녕하세요");
        specialMap.put("emoji", "😀🎉");
        
        // when
        Long contentLength = converter.getContentLength(specialMap, MediaType.APPLICATION_JSON);
        
        // then - 디버깅 정보 출력
        String actualJson = objectMapper.writeValueAsString(specialMap);
        byte[] actualBytes = actualJson.getBytes("UTF-8");
        
        System.out.println("=== 디버깅 정보 ===");
        System.out.println("실제 JSON: " + actualJson);
        System.out.println("실제 JSON 바이트 크기: " + actualBytes.length);
        System.out.println("계산된 Content-Length: " + contentLength);
        System.out.println("차이: " + (actualBytes.length - contentLength));
        
        // 각 문자별 바이트 크기 확인
        System.out.println("한글 '안녕하세요' 바이트: " + "안녕하세요".getBytes("UTF-8").length);
        System.out.println("이모지 '😀🎉' 바이트: " + "😀🎉".getBytes("UTF-8").length);
        
        assertEquals(actualBytes.length, contentLength.intValue());
    }

    @Test
    @DisplayName("간단한 한글 테스트")
    void getContentLength_SimpleKorean_MatchesActualJsonSize() throws IOException {
        // given
        String korean = "한글";
        
        // when
        Long contentLength = converter.getContentLength(korean, MediaType.APPLICATION_JSON);
        
        // then
        String actualJson = objectMapper.writeValueAsString(korean);
        byte[] actualBytes = actualJson.getBytes("UTF-8");
        
        System.out.println("=== 간단한 한글 테스트 ===");
        System.out.println("입력: " + korean);
        System.out.println("JSON: " + actualJson);
        System.out.println("실제 바이트: " + actualBytes.length);
        System.out.println("계산된 크기: " + contentLength);
        
        assertEquals(actualBytes.length, contentLength.intValue());
    }

    @Test
    @DisplayName("복잡한 객체 처리")
    void getContentLength_ComplexObject_MatchesActualJsonSize() throws IOException {
        // given
        TestObject testObject = new TestObject();
        
        // when
        Long contentLength = converter.getContentLength(testObject, MediaType.APPLICATION_JSON);
        
        // then
        String actualJson = objectMapper.writeValueAsString(testObject);
        byte[] actualBytes = actualJson.getBytes("UTF-8");
        
        assertEquals(actualBytes.length, contentLength.intValue());
    }

    private static class TestObject {
        private String name = "테스트";
        private int value = 100;
        private List<String> items = Arrays.asList("item1", "item2");
        
        public String getName() { return name; }
        public int getValue() { return value; }
        public List<String> getItems() { return items; }
    }
}