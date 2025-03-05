package com.yizhi.training.application.vo.manage;

import com.yizhi.training.application.vo.domain.TpAuthorizationRangeVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 按项目分组统计
 *
 * @author mei
 */

@Data
public class ReportStudyTrainingProjectVo {

    @ApiModelProperty(value = "项目ID")
    private Long id;

    @ApiModelProperty(value = "项目名称")
    private String name;

    @ApiModelProperty(value = "开始时间（项目周期）")
    private Date startTime;

    @ApiModelProperty(value = "结束时间（项目周期）")
    private Date endTime;

    @ApiModelProperty(value = "可参加人数（空返回0）")
    private Integer totalAsk = 0;

    @ApiModelProperty(value = "实际参加人数（空返回0）")
    private Integer totalIn = 0;

    @ApiModelProperty(value = "完成人数（空返回0）")
    private Integer totalFinish = 0;

    @ApiModelProperty(value = "完成率（空返回0），保留小时2位（98.00），前端补%")
    private BigDecimal avgFinish;

    //中台组装
    @ApiModelProperty(value = "项目是否0：指定学员可见，1平台用户可见（创建人管理权限范围）")
    private Integer visibleRange;

    @ApiModelProperty(value = "指定平台可见的取值")
    private List<TpAuthorizationRangeVo> listTpAuthorizationRange;
}
