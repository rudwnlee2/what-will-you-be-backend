package com.example.whatwillyoube.whatwillyoube_backend;

import com.example.whatwillyoube.whatwillyoube_backend.dto.PythonApiRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JacksonTest {

    static class TestDto {
        private int id;
        private String name;

        public TestDto() {} // 기본 생성자 (역직렬화용)
        public TestDto(int id, String name) { // 테스트 생성자
            this.id = id;
            this.name = name;
        }

        public int getId() { return id; }
        public String getName() { return name; }
    }

    @Test
    void jacksonTest() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        String json = mapper.writeValueAsString(new TestDto(1, "테스트"));
        System.out.println(json); // {"id":1,"name":"테스트"}

        TestDto dto = mapper.readValue(json, TestDto.class);
        assertEquals(1, dto.getId());
        assertEquals("테스트", dto.getName());
    }

    @Test
    void testJsonNaming() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        PythonApiRequestDto dto = new PythonApiRequestDto(
                1L, "개발자", "코딩", "안정성", "INTJ", "독서", "수학", "RIASEC"
        );

        String json = mapper.writeValueAsString(dto);
        System.out.println(json);

        PythonApiRequestDto result = mapper.readValue(json, PythonApiRequestDto.class);
        assertEquals("개발자", result.getDream());
    }
}


