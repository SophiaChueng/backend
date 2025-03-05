package com.yizhi.training.application.feign;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yizhi.training.application.vo.domain.TpCommentVo;
import com.yizhi.training.application.vo.manage.PageCommentVo;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @Author: shengchenglong
 * @Date: 2018/3/23 22:20
 */
@FeignClient(name = "trainingProject", contextId = "TpCommentClient")
public interface TpCommentClient {

    @GetMapping("/tpComment/list")
    Page<PageCommentVo> list(@RequestParam(name = "trainingProjectId") Long trainingProjectId,
        @RequestParam(name = "accountId") Long accountId, @RequestParam(name = "pageNo") Integer pageNo,
        @RequestParam(name = "pageSize") Integer pageSize, @RequestParam(name = "type") Integer type);

    @PostMapping("/tpComment/delete")
    Boolean delete(@RequestBody TpCommentVo tpComment);

    @PostMapping("/tpComment/save")
    Boolean save(@RequestBody TpCommentVo tpComment);

    @GetMapping("/tpComment/up")
    Boolean up(@RequestParam("id") Long id);

    @GetMapping("/tpComment/down")
    Boolean down(@RequestParam("id") Long id);

    @GetMapping("/tpComment/list/export")
    public Map<String, Object> export(@ApiParam(name = "trainingProjectId", value = "项目id") @RequestParam(
        name = "trainingProjectId") Long trainingProjectId,
        @ApiParam(name = "trainingProjectName", value = "项目名称") @RequestParam(
            name = "trainingProjectName") String trainingProjectName);

    /**
     * 交银康联中调用的老接口
     *
     * @param trainingProjectId
     * @param accountId
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Deprecated
    @GetMapping("/tpComment/listjykl")
    Page<PageCommentVo> list(@RequestParam(name = "trainingProjectId") Long trainingProjectId,
        @RequestParam(name = "accountId") Long accountId, @RequestParam(name = "pageNo") Integer pageNo,
        @RequestParam(name = "pageSize") Integer pageSize);

    /**
     * 交银康联中调用的老接口
     *
     * @param tpComment
     * @return
     */
    @Deprecated
    @PostMapping("/tpComment/downjykl")
    Boolean down(@RequestBody TpCommentVo tpComment);
}
