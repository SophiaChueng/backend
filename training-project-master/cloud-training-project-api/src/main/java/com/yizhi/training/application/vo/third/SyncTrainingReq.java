package com.yizhi.training.application.vo.third;

import com.yizhi.system.application.vo.third.ThirdCallbackConfigVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
public class SyncTrainingReq {

    @ApiModelProperty(value = "项目通过的查询时间范围-开始时间;格式：YYYY-MM-dd HH:mm:ss")
    private Date startTime;

    @ApiModelProperty(value = "项目通过的查询时间范围-结束时间;格式：YYYY-MM-dd HH:mm:ss")
    private Date endTime;

    @ApiModelProperty(value = "需要同步的项目ids")
    @NotNull
    private List<Long> projectIds;

    @ApiModelProperty(value = "需要同步的业务id；例如课程id")
    @NotNull
    private List<Long> relationIds;

    @ApiModelProperty(value = "需要同步的项目完成明细的学员ids")
    private List<Long> accountIds;

    private List<ThirdCallbackConfigVO> configVOList;

    @ApiModelProperty(value = "指定的根部门")
    private Long rootOrgId;

    private Long companyId;

    private Long siteId;

    private Integer pageNo;

    private Integer pageSize;

}
