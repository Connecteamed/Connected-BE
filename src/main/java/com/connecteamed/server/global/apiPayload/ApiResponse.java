package com.connecteamed.server.global.apiPayload;


import com.connecteamed.server.global.apiPayload.code.BaseErrorCode;
import com.connecteamed.server.global.apiPayload.code.BaseSuccessCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"status","data","message","code"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    @JsonProperty("status")
    private final String status;

    @JsonProperty("data")
    private T data;

    @JsonProperty("message")
    private final String message;

    @JsonProperty("code")
    private final String code;

    //성공한 경우({status, data} 구조)
    public static <T> ApiResponse<T> onSuccess(BaseSuccessCode code,T data){
        return new ApiResponse<>("success",data, null, null);
    }

    //실패한 경우 onFailure1 - 데이터가 필요 없는 일반적인 경우({status,message,code} 구조)
    public static <T> ApiResponse<T> onFailure(BaseErrorCode code){
        return new ApiResponse<>("error",null, code.getMessage(), code.getCode());
    }


    //실패한 경우 onFailure2 - 만약 데이터까지 내려주어야 할 경우
    public static <T> ApiResponse<T> onFailure(BaseErrorCode code, T data){
        return new ApiResponse<>("error",data, code.getMessage(), code.getCode());
    }

}
