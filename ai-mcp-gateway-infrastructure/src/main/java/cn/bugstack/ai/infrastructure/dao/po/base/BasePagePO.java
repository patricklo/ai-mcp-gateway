package cn.bugstack.ai.infrastructure.dao.po.base;

import lombok.Data;

/**
 * 基础分页对象
 *
 * @author xiaofuge bugstack.cn @小傅哥
 */
@Data
public class BasePagePO {

    /**
     * 当前页
     */
    private Integer page;

    /**
     * 每页数量
     */
    private Integer rows;

    /**
     * 起始行 (MyBatis 分页参数)
     */
    private Integer limitStart;

    /**
     * 查询行数 (MyBatis 分页参数)
     */
    private Integer limitCount;

    public void setPage(Integer page) {
        this.page = page;
        calculateLimit();
    }

    public void setRows(Integer rows) {
        this.rows = rows;
        calculateLimit();
    }

    private void calculateLimit() {
        if (page != null && rows != null) {
            this.limitStart = (page - 1) * rows;
            this.limitCount = rows;
        }
    }

}
