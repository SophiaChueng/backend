package com.yizhi.training.application.controller.remote.classify;

import com.yizhi.core.application.classify.vo.ClassifyDetailSimpleVO;
import com.yizhi.training.application.service.ITpClassificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author : Gxr
 * @since 2024-05-07 16:56
 */
@RestController
@RequestMapping("/remote/tp/classify/tmp")
public class RemoteTpClassifyController {

    @Autowired
    private ITpClassificationService service;

    /**
     * 根据分类id获取分类名称
     *
     * @param ids 分类id列表
     * @return List<ClassifyDetailSimpleVO>
     * @author Gxr
     * @since 2024/4/22 14:52
     */
    @PostMapping(value = "/list")
    public List<ClassifyDetailSimpleVO> listClassifyByIds(@RequestBody List<Long> ids) {
        return service.listClassifyByIds(ids);
    }

    /**
     * 根据分类id列表查询个数（判断id列表中是否含有不存在的分类id）
     *
     * @param ids 分类id列表
     * @return 存在数量
     * @author Gxr
     * @since 2024/4/22 14:52
     */
    @PostMapping(value = "/cnt")
    public Integer cntClassifyByIds(@RequestBody List<Long> ids) {
        return service.cntClassifyByIds(ids);
    }
}