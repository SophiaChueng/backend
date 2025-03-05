package com.yizhi.training.application.v2.vo.study;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class TpStudyForumVO {

    @ApiModelProperty("帖子名称")
    private String title;

    @ApiModelProperty("帖子内容")
    private String content;

    @ApiModelProperty("点赞数")
    private Integer thumbsNum;

    @ApiModelProperty("阅读人数")
    private Integer readNum;

    @ApiModelProperty("最新回复时间  初始值为创建时间 置顶时间为时间最大值； ")
    private Date commentTime;

    @ApiModelProperty("帖子ID")
    private Long forumId;

    @ApiModelProperty("学习单元/计划名 全部讨论使用。单元出处")
    private String planName;

}
