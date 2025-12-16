package com.example.whatwillyoube.whatwillyoube_backend.config;

import com.example.whatwillyoube.whatwillyoube_backend.util.FixedLengthJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;

@Configuration
public class AppConfig {

    /**
     * Python AI 서버와의 호환성을 위해 커스텀 RestClient를 빈으로 등록합니다.
     * <p>
     * 기본 Jackson 컨버터 대신 {@link FixedLengthJsonMessageConverter}를 사용하여,
     * 'Transfer-Encoding: chunked'를 비활성화하고 'Content-Length' 헤더를 명시적으로 전송합니다.
     * </p>
     *
     * @param builder Spring Boot가 자동 구성한 RestClient.Builder
     * @return 커스텀 메시지 컨버터가 적용된 RestClient 인스턴스
     */
    @Bean
    public RestClient noChunkedRestClient(RestClient.Builder builder) {
        return builder
                .messageConverters(converters -> {
                    // 기본으로 등록된 Jackson 컨버터를 찾아서 제거
                    converters.removeIf(converter -> converter instanceof MappingJackson2HttpMessageConverter);

                    //  Content-Length 계산 컨버터를 추가
                    converters.add(new FixedLengthJsonMessageConverter());
                })
                .build();
    }

}
