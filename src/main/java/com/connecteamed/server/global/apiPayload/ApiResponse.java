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
public class ApiResponse<T> {

    @JsonProperty("status")
    private final String status;

    @JsonProperty("data")
    private T data;

    @JsonProperty("message")
    private final String message;

    @JsonProperty("code")
    private final String code;

    //성공 응답 (code는 NULL로 처리)

    //성공 응답 1 : 에러 enum에 정의 된 메세지를 그대로 사용
    public static <T> ApiResponse<T> onSuccess(BaseSuccessCode code,T data){
        return new ApiResponse<>("success",data, code.getMessage(), null);
    }

    //성공 응답 2 : custom message 사용
    public static <T> ApiResponse<T> onSuccess(BaseSuccessCode code,T data,String customMessage){
        return new ApiResponse<>("success",data,customMessage,null);
    }

    //실패 응답 1,2  (data는 NULL로 처리)

    //실패 응답 1 : data가 필요 없는 일반적인 경우 + 에러 enum에 정의 된 메세지를 그대로 사용
    public static <T> ApiResponse<T> onFailure(BaseErrorCode code){
        return new ApiResponse<>("error",null, code.getMessage(), code.getCode());
    }

    //실패 응답 2 : data가 필요 없는 일반적인 경우 + custom message 사용
    public static <T> ApiResponse<T> onSuccess(BaseSuccessCode code, String customMessage){
        return new ApiResponse<>("error",null, customMessage, code.getCode());
    }



    //실패한 응답 3 : data까지 내려주어야 할 경우
    public static <T> ApiResponse<T> onFailure(BaseErrorCode code, T data){
        return new ApiResponse<>("error",data, code.getMessage(), code.getCode());
    }

}
