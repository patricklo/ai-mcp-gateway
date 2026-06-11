package cn.bugstack.ai.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 分页返回对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponsePage<T> implements Serializable {

    private String code;
    private String info;
    private T data;
    
    /**
     * 总记录数
     */
    private Long total;

}