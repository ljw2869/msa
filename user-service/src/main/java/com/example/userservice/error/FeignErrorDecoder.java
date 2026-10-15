package com.example.userservice.error;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class FeignErrorDecoder implements ErrorDecoder {
    Environment env;

    public FeignErrorDecoder(Environment env){
        this.env=env;
    }
    // 어떤 오류가 발생했을 때 어떻게 처리할 것인지를 구현
    @Override
    public Exception decode(String methodKey, Response response) {
        switch(response.status()){
            case 400:
                break;
            case 404:
                if(methodKey.contains("getOrders")){
                    return new ResponseStatusException(HttpStatus.valueOf(response.status()),
                            env.getProperty("order-service.exception.orders_is_empty"));
                }
                break;
            default:
                return new Exception(response.reason());
        }
        return null; //400 이 넘어오면 null을 보내 예외처리를 하지 않겠다는 의미
    }
}
