package cn.bugstack.ai.infrastructure.adapter.port;

import cn.bugstack.ai.domain.session.adapter.port.ISessionPort;
import cn.bugstack.ai.domain.session.model.valobj.gateway.McpToolProtocolConfigVO;
import cn.bugstack.ai.infrastructure.gateway.GenericHttpGateway;
import cn.bugstack.ai.types.enums.ResponseCode;
import cn.bugstack.ai.types.exception.AppException;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.springframework.stereotype.Component;
import retrofit2.Call;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 回话端口服务
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/1/30 20:56
 */
@Component
public class SessionPort implements ISessionPort {

    @Resource
    private GenericHttpGateway gateway;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Object toolCall(McpToolProtocolConfigVO.HTTPConfig httpConfig, Object params) throws IOException {
        // 1. 构建请求头
        String httpHeadersJson = httpConfig.getHttpHeaders();

        Map<String, Object> headers = objectMapper.readValue(httpHeadersJson, Map.class);

        // 2. 判断请求方法
        String httpMethod = httpConfig.getHttpMethod().toLowerCase();

        // 3. 参数校验
        if (!(params instanceof Map<?, ?> arguments)) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }

        switch (httpMethod) {
            // 1. 构建请求体
            case "post": {
                RequestBody requestBody = RequestBody.create(JSON.toJSONString(arguments.values().toArray()[0]),
                        MediaType.parse("application/json"));

                Call<ResponseBody> call = gateway.post(httpConfig.getHttpUrl(), headers, requestBody);
                ResponseBody responseBody = call.execute().body();

                assert responseBody != null;

                return responseBody.string();
            }
            // 2. 执行get请求
            case "get": {
                Map<String, Object> objMapRequest = new java.util.HashMap<>((Map<String, Object>) arguments.values().toArray()[0]);

                String url = httpConfig.getHttpUrl();
                // 替换路径参数
                Matcher matcher = Pattern.compile("\\{([^}]+)\\}").matcher(url);
                while (matcher.find()) {
                    String name = matcher.group(1);
                    if (objMapRequest.containsKey(name)) {
                        url = url.replace("{" + name + "}", String.valueOf(objMapRequest.get(name)));
                        objMapRequest.remove(name);
                    }
                }

                Call<ResponseBody> call = gateway.get(url, headers, objMapRequest);

                ResponseBody responseBody = call.execute().body();

                assert responseBody != null;

                return responseBody.string();
            }
        }

        throw new AppException(ResponseCode.METHOD_NOT_FOUND.getCode(), ResponseCode.METHOD_NOT_FOUND.getInfo());
    }


}
