package org.wjh.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.wjh.domain.AccountFlow;
import org.wjh.service.MysqlService;

/**
 * @Author wangjihui
 * @Date 2025/5/13
 */
@RestController
@RequestMapping("/mysql")
public class MysqlController {

    @Autowired
    private MysqlService mysqlService;

    @GetMapping("getFlow.do")
    public AccountFlow getFlow(@RequestParam("id") String id){
        return mysqlService.getFlow(id);
    }

    @GetMapping("insertFlow.do")
    public AccountFlow insertFlow(){
        return mysqlService.insertFlow();
    }

    @GetMapping("updateFlow.do")
    public AccountFlow updateFlow(@RequestParam("id") String id){
        mysqlService.getFlow(id);
        return mysqlService.updateFlow(id);
    }
}
