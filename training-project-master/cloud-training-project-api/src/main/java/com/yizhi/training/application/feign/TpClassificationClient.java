package com.yizhi.training.application.feign;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yizhi.core.application.context.RequestContext;
import com.yizhi.training.application.vo.domain.TpClassificationVo;
import com.yizhi.training.application.vo.domain.TrainingProjectVo;
import com.yizhi.util.application.domain.BizResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Author: shengchenglong
 * @Date: 2018/3/23 22:20
 */
@FeignClient(name = "trainingProject", contextId = "TpClassificationClient")
public interface TpClassificationClient {

    @GetMapping(value = "/tpClassification/list")
    List<TpClassificationVo> list(@RequestBody RequestContext context);

    @PostMapping(value = "/tpClassification/save")
    BizResponse<Boolean> save(@RequestBody TpClassificationVo tpClassification);

    @PostMapping(value = "/tpClassification/update")
    BizResponse<Boolean> update(@RequestBody TpClassificationVo tpClassification);

    @GetMapping(value = "/tpClassification/view")
    TpClassificationVo view(@RequestParam("id") Long id);

    /**
     * 分类逻辑删除  删除之前检验有误项目关联 有则删除失败 无则删除成功
     *
     * @param param 接参实体 只取id
     * @return 布尔值  true删除成功 false 删除失败
     */
    @PostMapping(value = "/tpClassification/delete")
    Boolean tpClassificationDelete(@RequestBody TpClassificationVo param);

    @GetMapping(value = "/tpClassification/page/list")
    Page<TpClassificationVo> pageList(@RequestParam(value = "keyword", required = false) String keyword,
        @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
        @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize);

    @GetMapping(value = "/tpClassification/change/sort")
    boolean changeSort(@RequestParam(value = "id") Long id, @RequestParam(value = "type") Integer type,
        @RequestParam(value = "sort") Integer sort);

    @GetMapping(value = "/tpClassification/projects/list")
    Page<TrainingProjectVo> projectsListByClassifyId(@RequestParam(value = "id") Long id,
        @RequestParam(value = "keyword", required = false) String keyword,
        @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
        @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize);

    @GetMapping(value = "/tpClassification/projects/remove")
    boolean projectsRemoveRelation(@RequestParam(value = "classifyId") Long classifyId,
        @RequestParam(value = "projectId") Long projectId);
}
