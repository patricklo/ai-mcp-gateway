package cn.bugstack.ai.infrastructure.dao.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import cn.bugstack.ai.infrastructure.dao.po.base.BasePagePO;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class McpProtocolHttpPO extends BasePagePO {

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 所属网关ID
     */
    private Long protocolId;
    /**
     * HTTP接口地址
     */
    private String httpUrl;
    /**
     * HTTP请求方法：GET/POST/PUT/DELETE
     */
    private String httpMethod;
    /**
     * HTTP请求头（JSON格式）
     */
    private String httpHeaders;
    /**
     * 超时时间（毫秒）
     */
    private Integer timeout;
    /**
     * 重试次数
     */
    private Integer retryTimes;
    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;

}
