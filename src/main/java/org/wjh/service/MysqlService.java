package org.wjh.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.wjh.domain.AccountFlow;
import org.wjh.mapper.AccountFlowMapper;
import org.wjh.mapper.AgentMapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * service
 *
 * @Author wangjihui
 * @Date 2025/5/13
 */
@Service
public class MysqlService {

    @Autowired
    private AccountFlowMapper accountFlowMapper;
    @Autowired
    private AgentMapper agentMapper;

    /**
     * 查询
     *
     * @param id
     * @return
     */
    public AccountFlow getFlow(String id){
        List<String> ids = new ArrayList<>();
        ids.add(id);
        List<AccountFlow> flows = accountFlowMapper.batchSelect(ids, TransactionSynchronizationManager.isActualTransactionActive());
        return flows == null || flows.size() ==0? null : flows.get(0);
    }

    /**
     * 新增
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public AccountFlow insertFlow(){
        //保存
        AccountFlow flow = new AccountFlow();
        flow.setId(UUID.randomUUID().toString().replace("-", ""));
        flow.setSerialId(flow.getId());
        flow.setPayee(flow.getId());
        flow.setCreateTime(new Date());
        List<AccountFlow> flows = new ArrayList<>();
        flows.add(flow);
        accountFlowMapper.batchInsert(flows);

        //查询
        List<String> ids = new ArrayList<>();
        ids.add(flow.getId());
        List<AccountFlow> flows1 = accountFlowMapper.batchSelect(ids, TransactionSynchronizationManager.isActualTransactionActive());
        return flows1.get(0);
    }

    /**
     * 修改
     *
     * @param id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public AccountFlow updateFlow(String id){
        List<String> ids = new ArrayList<>();
        ids.add(id);
        List<AccountFlow> flows = accountFlowMapper.batchSelect(ids, TransactionSynchronizationManager.isActualTransactionActive());

        AccountFlow flow = flows.get(0);
        flow.setUpdateTime(new Date());
        accountFlowMapper.batchUpdate(flows);

        return flow;
    }
}
