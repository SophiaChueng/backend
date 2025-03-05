package com.yizhi.training.application.v2.vo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@ApiModel(description = "查询项目列表入参")
@Data
public class SearchProjectVO implements Serializable {

    @ApiModelProperty("部门ID")
    private List<Long> orgIds;

    @ApiModelProperty("项目分类ID")
    private Long tpClassificationId;

    @ApiModelProperty("筛选的项目ID")
    private List<Long> trainingProjectIds;

    @ApiModelProperty("筛选的项目")
    private String name;

    @ApiModelProperty("可见范围（null:全部, 0：指定用户，1：平台用户）")
    private Integer visibleRange;

    /**
     * @see com.yizhi.training.application.v2.enums.TpEnrollStatusEnum
     */
    @ApiModelProperty(value = "报名条件(10:无需报名；20:免费报名；30:付费报名)")
    private Integer enrollStatus;

    @ApiModelProperty("项目状态（null:全部，0：草稿， 1:已上架， 2： 已下架）")
    private Integer status;

    /**
     * @see com.yizhi.training.application.v2.enums.TpProcessStatusEnum
     */
    @ApiModelProperty("项目进行状态（null:全部， 1：未开始， 2： 进行中， 3：已结束）")
    private Integer processStatus;

    @ApiModelProperty("页号")
    private Integer pageNo;

    @ApiModelProperty("页大小")
    private Integer pageSize;
}
