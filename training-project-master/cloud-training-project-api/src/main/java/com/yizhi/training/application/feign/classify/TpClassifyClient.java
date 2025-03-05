package com.yizhi.training.application.feign.classify;

import com.yizhi.core.application.classify.feign.CommClassifyTemple;
import com.yizhi.core.application.classify.vo.ClassifyDetailSimpleVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author : Gxr
 * @since 2024-04-22 14:50
 */
@FeignClient(name = "trainingProject", contextId = "TpClassifyClient")
public interface TpClassifyClient extends CommClassifyTemple {

    /**
     * 根据分类id获取分类名称
     *
     * @param ids 分类id列表
     * @return List<ClassifyDetailSimpleVO>
     * @author Gxr
     * @since 2024/4/22 14:52
     */
    @Override
    @PostMapping(value = "/remote/tp/classify/tmp/list")
    List<ClassifyDetailSimpleVO> listClassifyByIds(@RequestBody List<Long> ids);

    /**
     * 根据分类id列表查询个数（判断id列表中是否含有不存在的分类id）
     *
     * @param ids 分类id列表
     * @return 存在数量
     * @author Gxr
     * @since 2024/4/22 14:52
     */
    @Override
    @PostMapping(value = "/remote/tp/classify/tmp/cnt")
    Integer cntClassifyByIds(@RequestBody List<Long> ids);
}
