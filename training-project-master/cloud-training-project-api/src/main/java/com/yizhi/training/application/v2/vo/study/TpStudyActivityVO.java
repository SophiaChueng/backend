package com.yizhi.training.application.v2.vo.study;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("学习页计划中的活动")
public class TpStudyActivityVO {

    @ApiModelProperty("活动Id")
    private Long activityId;

    @ApiModelProperty("业务Id,eg：课程Id、考试Id")
    private Long relationId;

    @ApiModelProperty("业务名")
    private String relationName;

    @ApiModelProperty("图片地址")
    private String logoUrl;

    @ApiModelProperty("true:必修。fasle:非必须")
    private Boolean mustStudy = false;

    @ApiModelProperty(
        "（0：课程 1：考试 2：调研 3：直播 4：投票 5：作业 7：外部链接 8：报名 9：签到 10：线下课程 11:案例活动 12：精选案例 13：资料 14：帖子  18：智能陪练 19：答题活动）")
    private Integer relationType;

    @ApiModelProperty("true:完成 false:未完成")
    private Boolean finished = false;

    @ApiModelProperty("外链URL")
    private String url;

    @ApiModelProperty("当relationType为直播时：0: 公开播放 ; 1: 站内授权播放;")
    private Integer serviceType;

    @ApiModelProperty("当relationType为直播时，表示频道号")
    private String channel;

    @ApiModelProperty(value = "水印||跑马灯开关 0 关闭 1开启")
    private Boolean switchConf;

    @ApiModelProperty(value = "资料类型 '资料类型  ：1、文档；2、图片；3、音频；4、视频；5、其他'")
    private Integer type;

}
