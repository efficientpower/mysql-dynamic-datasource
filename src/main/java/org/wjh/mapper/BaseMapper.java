package org.wjh.mapper;

import org.apache.ibatis.annotations.Param;
import org.wjh.dynamic.datasource.DataSource;

import java.util.List;

import static org.wjh.dynamic.datasource.DataSourceType.MASTER;
import static org.wjh.dynamic.datasource.DataSourceType.SLAVE;

/**
 * Mapper基类
 * <p>
 * 批量查询、批量插入、批量更新、批量删除
 *
 * @Author wangjihui
 * @Date 2023/12/26
 */
public interface BaseMapper<T> {

    public static final String FOR_UPDATE = "forUpdate";

    /**
     * 批量新增
     *
     * @param list
     * @return
     */
    @DataSource(MASTER)
    public int batchInsert(@Param("list") List<T> list);

    /**
     * 批量修改
     *
     * @param list
     * @return
     */
    @DataSource(MASTER)
    public int batchUpdate(@Param("list") List<T> list);

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @DataSource(MASTER)
    public int batchDelete(@Param("list") List<String> ids);

    /**
     * 批量查询
     *
     * @param ids
     * @param forUpdate
     * @return
     */
    @DataSource(SLAVE)
    public List<T> batchSelect(@Param("list") List<String> ids, @Param(FOR_UPDATE) boolean forUpdate);
}
