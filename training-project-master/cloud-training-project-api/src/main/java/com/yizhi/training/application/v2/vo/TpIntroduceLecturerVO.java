package com.yizhi.training.application.v2.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TpIntroduceLecturerVO {

    @ApiModelProperty("讲师名字")
    private String lecturerName;

    @ApiModelProperty("讲师标题")
    private String lecturerTitle;

    @ApiModelProperty("讲师头像")
    private String lecturerAvatar;

    @ApiModelProperty("讲师ID")
    private Long lecturerId;
}
