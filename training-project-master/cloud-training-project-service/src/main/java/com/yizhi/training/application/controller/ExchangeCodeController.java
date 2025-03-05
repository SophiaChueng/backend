package com.yizhi.training.application.controller;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.yizhi.application.orm.id.IdGenerator;
import com.yizhi.core.application.context.ContextHolder;
import com.yizhi.core.application.exception.BizException;
import com.yizhi.core.application.exception.CustomException;
import com.yizhi.core.application.file.task.TaskVO;
import com.yizhi.training.application.qo.ExchangeCodeQO;
import com.yizhi.training.application.service.IExchangeCodeService;
import com.yizhi.training.application.task.ExchangeCodeExporter;
import com.yizhi.training.application.vo.ExchangeCodeVO;
import com.yizhi.training.application.vo.api.ExchangeTrainingProjectVO;
import com.yizhi.util.application.domain.BizResponse;
import com.yizhi.util.application.page.PageInfo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 兑换码 前端控制器
 * </p>
 *
 * @author xiaoyu
 * @since 2021-05-14
 */
@RestController
@RequestMapping("/exchangeCode")
public class ExchangeCodeController {

    @Autowired
    private IExchangeCodeService service;

    @Autowired
    private ExchangeCodeExporter exporter;

    @Autowired
    private IdGenerator idGen;

    @ApiOperation("获取已生成的激活码数量")
    @GetMapping("/count")
    public BizResponse<Integer> codeCount(@RequestParam("tpId") Long tpId, @RequestParam("type") Integer type) {
        return BizResponse.ok(service.codeCount(tpId, type));
    }

    @ApiOperation("生成指定数量的激活码")
    @GetMapping("/create")
    public BizResponse<Boolean> create(@RequestParam("tpId") Long tpId, @RequestParam("type") Integer type,
        @RequestParam("num") Integer num) {
        try {
            service.createCode(tpId, type, num);
        } catch (BizException e) {
            return BizResponse.fail(e.getCode(), e.getMsg());
        }
        return BizResponse.ok(true);
    }

    @ApiOperation("列表页查询")
    @PostMapping("/list")
    public BizResponse<PageInfo<ExchangeCodeVO>> list(@RequestBody ExchangeCodeQO qo) {
        return BizResponse.ok(service.listPage(qo));
    }

    @ApiOperation("批量删除")
    @PostMapping("/batch/delete")
    public BizResponse<Boolean> batchDel(@RequestBody List<Long> ids) {
        String error = service.batchDelete(ids);
        if (!StrUtil.isBlank(error)) {
            return BizResponse.fail(error);
        }
        return BizResponse.ok(true);
    }

    @GetMapping("/change")
    @ApiOperation("兑换项目")
    public CustomException<String> codeExchangeTraining(@RequestParam("exchangeCode") String exchangeCode,
        @RequestParam(value = "trainingProjectId", required = false) Long tpId) {
        return service.codeExchangeTraining(exchangeCode, tpId);

    }

    @GetMapping("/change/course")
    @ApiOperation("兑换项目")
    public CustomException<String> codeExchangeCourse(@RequestParam("exchangeCode") String exchangeCode,
        @RequestParam(value = "courseId", required = false) Long courseId) {
        return service.codeExchangeCourse(exchangeCode, courseId);

    }

    @GetMapping("/exchange/list")
    @ApiOperation("兑换列表")
    public CustomException<PageInfo<ExchangeTrainingProjectVO>> getProjectExchangeList(
        @ApiParam("pageNo") @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
        @ApiParam("pageSize") @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
        @ApiParam("pageSize") @RequestParam(value = "type", required = false) Integer type) {
        return service.getExchangeTrainingList(pageNo, pageSize, type);
    }

    @PostMapping("/export")
    public BizResponse<String> export(@RequestBody ExchangeCodeQO qo) {
        String taskName = "导出兑换码" + DateUtil.format(new Date(), DatePattern.PURE_DATETIME_PATTERN);
        TaskVO<ExchangeCodeQO> param = new TaskVO<ExchangeCodeQO>();
        param.setContext(ContextHolder.get());
        param.setQueryVo(qo);
        param.setTaskId(idGen.generate());
        param.setSerialNo(taskName);
        param.setTaskName(taskName);
        exporter.execute(param, true);
        return BizResponse.ok(taskName);
    }

    @GetMapping("/by/user/relationId")
    public Boolean userRelationExchange(@RequestParam("relationId") Long relationId,
        @RequestParam("accountId") Long accountId) {
        return service.userRelationExchange(relationId, accountId);
    }

    /**
     * 根据类型获取兑换记录 资源id
     *
     * @param type
     * @return
     */
    @GetMapping("/stu/record/by/account")
    public List<Long> getEnrollRelationIdsByAccountId(@RequestParam("type") Integer type) {
        return service.getEnrollRelationIdsByAccountId(type);
    }

    /**
     * 兑换码获取资源详细信息
     *
     * @param exchangeCode
     * @return
     */
    @GetMapping("/stu/ref/info/by/code")
    public CustomException<ExchangeTrainingProjectVO> getRefInfoByExchangeCode(
        @RequestParam("exchangeCode") String exchangeCode) {
        return service.getRefInfoByExchangeCode(exchangeCode);
    }
}

