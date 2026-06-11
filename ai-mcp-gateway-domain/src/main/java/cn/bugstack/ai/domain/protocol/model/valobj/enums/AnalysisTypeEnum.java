package cn.bugstack.ai.domain.protocol.model.valobj.enums;

import cn.bugstack.ai.types.enums.ResponseCode;
import cn.bugstack.ai.types.exception.AppException;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 解析类型枚举
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/3/3 07:47
 */
@Getter
public enum AnalysisTypeEnum {

    swagger,

    ;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public enum SwaggerAnalysisAction {

        requestBodyAnalysis("requestBodyAnalysis", "解析对象"),
        parametersAnalysis("parametersAnalysis", "解析属性"),

        ;

        private String code;
        private String info;

        public static SwaggerAnalysisAction get(JSONObject operation) {

            JSONObject requestBody = operation.getJSONObject("requestBody");
            JSONArray parameters = operation.getJSONArray("parameters");

            if (null != requestBody) {
                return requestBodyAnalysis;
            }

            if (null != parameters) {
                return parametersAnalysis;
            }

            throw new AppException(ResponseCode.ENUM_NOT_FOUND.getCode(), ResponseCode.ENUM_NOT_FOUND.getInfo());
        }

    }

}
