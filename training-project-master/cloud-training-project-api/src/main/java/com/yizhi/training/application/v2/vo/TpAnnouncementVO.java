package com.yizhi.training.application.v2.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel(description = "项目公告")
public class TpAnnouncementVO implements Serializable {

    @ApiModelProperty("公告id")
    private Long id;

    @ApiModelProperty("项目id")
    private Long trainingProjectId;

    @ApiModelProperty("公告标题")
    private String title;

    @ApiModelProperty("公告内容")
    private String content;

    @ApiModelProperty("公告时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date publishTime;

    @ApiModelProperty("排序")
    private Integer sort;
}
