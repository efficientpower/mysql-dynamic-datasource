package org.wjh.domain;

import lombok.Data;

/**
 * 代理商对象 agent
 *
 * @author etime
 * @date 2024-01-23
 */
@Data
public class Agent extends BaseEntity {
    /**
     * 主键
     */
    private String id;

    /**
     * 代理商名
     */
    private String agentName;

    /**
     * 代理商编号
     */
    private String agentNo;
}
