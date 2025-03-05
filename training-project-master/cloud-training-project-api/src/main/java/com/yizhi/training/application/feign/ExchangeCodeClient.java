package com.yizhi.training.application.feign;

import com.yizhi.core.application.exception.CustomException;
import com.yizhi.training.application.qo.ExchangeCodeQO;
import com.yizhi.training.application.vo.ExchangeCodeVO;
import com.yizhi.training.application.vo.api.ExchangeTrainingProjectVO;
import com.yizhi.util.application.domain.BizResponse;
import com.yizhi.util.application.page.PageInfo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author Gyg
 * @date 2021/5/17 15:07
 */
@FeignClient(name = "trainingProject", contextId = "ExchangeCodeClient")
public interface ExchangeCodeClient {

    @GetMapping("/exchangeCode/change")
    CustomException<String> codeExchangeTraining(@RequestParam("exchangeCode") String exchangeCode,
        @RequestParam(value = "trainingProjectId", required = false) Long tpId);

    @GetMapping("/exchangeCode/change/course")
    CustomException<String> codeExchangeCourse(@RequestParam("exchangeCode") String exchangeCode,
        @RequestParam(value = "courseId", required = false) Long courseId);

    @GetMapping("/exchangeCode/exchange/list")
    CustomException<PageInfo<ExchangeTrainingProjectVO>> getProjectExchangeList(
        @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
        @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
        @ApiParam("pageSize") @RequestParam(value = "type", required = false) Integer type);

    @ApiOperation("获取已生成的激活码数量")
    @GetMapping("/exchangeCode/count")
    public BizResponse<Integer> codeCount(@ApiParam("关联的项目/活动等等的ID") @RequestParam("tpId") Long tpId,
        @ApiParam("关联的项目/活动等等的类型") @RequestParam("type") Integer type);

    @ApiOperation("生成指定数量的激活码")
    @GetMapping("/exchangeCode/create")
    public BizResponse<Boolean> create(@ApiParam("关联的项目/活动等等的ID") @RequestParam("tpId") Long tpId,
        @ApiParam("关联的项目/活动等等的类型") @RequestParam("type") Integer type, @RequestParam("num") Integer num);

    @ApiOperation("列表页查询")
    @PostMapping("/exchangeCode/list")
    public BizResponse<PageInfo<ExchangeCodeVO>> list(@RequestBody ExchangeCodeQO qo);

    @ApiOperation("批量删除")
    @PostMapping("/exchangeCode/batch/delete")
    public BizResponse<Boolean> batchDel(@RequestBody List<Long> ids);

    @PostMapping("/exchangeCode/export")
    public BizResponse<String> export(@RequestBody ExchangeCodeQO qo);

    /**
     * 兑换结果
     *
     * @param relationId
     * @param accountId
     * @return
     */
    @GetMapping("/exchangeCode/by/user/relationId")
    Boolean userRelationExchange(@RequestParam("relationId") Long relationId,
        @RequestParam("accountId") Long accountId);

    /**
     * 根据类型获取兑换记录 资源id
     *
     * @param type
     * @return
     */
    @GetMapping("/exchangeCode/stu/record/by/account")
    List<Long> getEnrollRelationIdsByAccountId(@RequestParam("type") Integer type);

    @GetMapping("/exchangeCode/stu/ref/info/by/code")
    CustomException<ExchangeTrainingProjectVO> getRefInfoByExchangeCode(
        @RequestParam("exchangeCode") String exchangeCode);
}
