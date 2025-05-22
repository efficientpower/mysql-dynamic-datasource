package org.wjh.domain;

import lombok.Data;

/**
 * 银行交易流水对象
 */
@Data
public class AccountFlow extends BaseEntity {
    /** 主键 */
    private String id;

    /** 交易流水id */
    private String serialId;

    /** 收款人 */
    private String payee;
}
