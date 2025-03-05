package com.yizhi.training.application.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 自定义项目获取作业打卡和被赞次数请求类
 *
 * @Author: dingxiaowei
 * @Date: 2019/10/22
 */
@Data
@ApiModel("自定义项目获取作业打卡和被赞次数请求类")
public class AssignmentDetailsParam {

    @ApiModelProperty(value = "培训项目ids")
    private List<Long> projectIds;

    @ApiModelProperty(value = "是否取实时数据 0:前天截止数据 1:实时数据")
    private Integer isReaTime;

}
