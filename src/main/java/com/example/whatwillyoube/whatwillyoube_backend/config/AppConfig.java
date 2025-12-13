package com.example.whatwillyoube.whatwillyoube_backend.config;

import com.example.whatwillyoube.whatwillyoube_backend.util.FixedLengthJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;

@Configuration
public class AppConfig {

    /**
     * Transfer-Encoding: chunked를 사용하지 않고 Content-Length 헤더를 명시하는
     * 커스텀 RestClient Bean을 생성합니다.
     * 이 Bean을 주입받아 API를 호출하면 chunked 인코딩이 비활성화됩니다.
     *
     * @param builder Spring Boot가 자동 구성한 RestClient.Builder
     * @return 커스텀 메시지 컨버터가 적용된 RestClient 인스턴스
     */
    @Bean
    public RestClient noChunkedRestClient(RestClient.Builder builder) {
        return builder
                .messageConverters(converters -> {
                    // 기본으로 등록된 Jackson 컨버터를 찾아서 제거합니다.
                    // 이렇게 해야 커스텀 컨버터만 남게 됩니다.
                    converters.removeIf(converter -> converter instanceof MappingJackson2HttpMessageConverter);

                    // 우리가 만든 Content-Length 계산 컨버터를 추가합니다.
                    converters.add(new FixedLengthJsonMessageConverter());
                })
                .build();
    }

}
