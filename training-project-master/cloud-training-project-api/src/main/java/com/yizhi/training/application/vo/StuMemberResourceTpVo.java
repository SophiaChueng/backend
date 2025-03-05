package com.yizhi.training.application.vo;

import com.yizhi.system.application.vo.member.StuMemberResourceBaseVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class StuMemberResourceTpVo extends StuMemberResourceBaseVo {

    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    private Date endTime;
}
