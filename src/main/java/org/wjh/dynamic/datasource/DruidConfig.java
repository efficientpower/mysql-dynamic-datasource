package org.wjh.dynamic.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * druid 配置多数据源
 *
 * @author ruoyi
 */
@Configuration
public class DruidConfig {

    public static Map<Object, Object> TARGET_DATA_SOURCES = new HashMap<>();

    @Bean
    @ConfigurationProperties("spring.datasource.druid.master")
    public DataSource masterDataSource(DruidProperties druidProperties) {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        return druidProperties.dataSource(dataSource);
    }

    @Bean
    @ConfigurationProperties("spring.datasource.druid.slave")
    @ConditionalOnProperty(prefix = "spring.datasource.druid.slave", name = "enabled", havingValue = "true")
    public DataSource slaveDataSource(DruidProperties druidProperties) {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        return druidProperties.dataSource(dataSource);
    }

    @Bean
    @ConfigurationProperties("spring.datasource.druid.slave1")
    @ConditionalOnProperty(prefix = "spring.datasource.druid.slave1", name = "enabled", havingValue = "true")
    public DataSource slaveDataSource1(DruidProperties druidProperties) {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        return druidProperties.dataSource(dataSource);
    }

    @Bean
    @Primary
    public DynamicDataSource dataSource(DataSource masterDataSource, DataSource slaveDataSource, DataSource slaveDataSource1) {
        TARGET_DATA_SOURCES.put(DataSourceType.MASTER.name(), masterDataSource);

        if (slaveDataSource != null) {
            TARGET_DATA_SOURCES.put(DataSourceType.SLAVE.name(), slaveDataSource);
        }
        /**
         * 后续陆续追加从数据源，只要保证key不同即可，但是数据源注解从库还是DataSourceType.SLAVE，数据源切换时候会根据规则平衡选择不同的从库。
         */
        if(slaveDataSource1 != null){
            TARGET_DATA_SOURCES.put(DataSourceType.SLAVE.name() + "1", slaveDataSource);
        }
        return new DynamicDataSource(masterDataSource, TARGET_DATA_SOURCES);
    }
}
