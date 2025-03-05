package com.yizhi.training.application.vo.manage;

import com.yizhi.core.application.context.RequestContext;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 2019/09/19
 *
 * @author wangfeida
 */
@Data
public class VisibleRangeExport {

    @ApiModelProperty(name = "bizId", value = "具体业务id")
    private Long bizId;

    @ApiModelProperty(name = "bizName", value = "具体业务名字")
    private String bizName;

    @ApiModelProperty(name = "个人ID集合", value = "个人id集合")
    private List<Long> accountIds;

    @ApiModelProperty(name = "组织ID集合", value = "组织ID集合")
    private List<Long> orgIds;

    @ApiModelProperty(name = "上下文对象", value = "上下文对象")
    private RequestContext context;

}
