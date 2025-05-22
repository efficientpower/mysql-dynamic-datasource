package org.wjh.dynamic.datasource;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 多数据源切换切面类
 * Order值越小优先级越高。优先级必须高于事务切面
 */
@Aspect
@Order(1)
@Component
public class DataSourceAspect implements ApplicationListener<ContextRefreshedEvent> {

    /*从库名列表，对多个从数据源提供支持*/
    public static List<String> SLAVE_DS_NAME_LIST = new ArrayList<String>();
    /*从数据源切换标记，通过取模来切换，平衡各个从库的负载*/
    public static AtomicLong SLAVE_INDEX = new AtomicLong(0L);

    @Autowired
    DruidConfig druidConfig;

    @Pointcut("@annotation(org.wjh.dynamic.datasource.DataSource) || @within(org.wjh.dynamic.datasource.DataSource) || " +
            "@annotation(org.springframework.transaction.annotation.Transactional) || @within(org.springframework.transaction.annotation.Transactional)")
    public void dsPointCut() {
    }

    /**
     * 方法执行前切换数据源
     *
     * @param point
     * @return
     * @throws Throwable
     */
    @Around("dsPointCut()")
    public Object before(ProceedingJoinPoint point) throws Throwable {
        //有从库并且不在事务中，才有切换数据源的必要
        if (SLAVE_DS_NAME_LIST.size() > 0 && !TransactionSynchronizationManager.isActualTransactionActive()) {
            setDataSourceType(point);
        }
        try {
            return point.proceed();
        } finally {
            // 销毁数据源 在执行方法之后
            DynamicDataSource.clearDataSourceType();
        }
    }

    public void setDataSourceType(ProceedingJoinPoint point) {
        //事务不管读写都要到主库
        Transactional transactional = getTransactional(point);
        if (transactional != null) {
            DynamicDataSource.setDataSourceType(DataSourceType.MASTER.name());
            return;
        }

        //没有事务，根据配置的数据源标识切换，没有就默认主库
        DataSource dataSource = getDataSource(point);
        if(dataSource != null){
            String name = dataSource.value().name();
            if (DataSourceType.SLAVE.name().equals(name)) {
                int index = (int) (SLAVE_INDEX.getAndIncrement() % SLAVE_DS_NAME_LIST.size());
                DynamicDataSource.setDataSourceType(SLAVE_DS_NAME_LIST.get(index));
            } else {
                DynamicDataSource.setDataSourceType(name);
            }
        }
    }

    /**
     * 获取数据源注解
     */
    public DataSource getDataSource(ProceedingJoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        //优先获取方法级别注解
        DataSource dataSource = AnnotationUtils.findAnnotation(signature.getMethod(), DataSource.class);
        if (dataSource != null) {
            return dataSource;
        }
        //方法级别注解没有，就找类级别
        return AnnotationUtils.findAnnotation(signature.getDeclaringType(), DataSource.class);
    }

    /**
     * 获取事务注解
     *
     * @param point
     * @return
     */
    public Transactional getTransactional(ProceedingJoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        //优先获取方法级别注解
        Transactional transactional = AnnotationUtils.findAnnotation(signature.getMethod(), Transactional.class);
        if (transactional != null) {
            return transactional;
        }
        //方法级别注解没有，就找类级别
        return AnnotationUtils.findAnnotation(signature.getDeclaringType(), Transactional.class);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        //把所有的从库数据源名都放入上面的列表中。
        for (Object key : DruidConfig.TARGET_DATA_SOURCES.keySet()) {
            SLAVE_DS_NAME_LIST.add((String) key);
        }
        //排除主库
        SLAVE_DS_NAME_LIST.remove(DataSourceType.MASTER.name());
    }

}
