package com.yizhi.training.application.vo.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: shengchenglong
 * @Date: 2018/4/11 14:42
 */
@Data
@ApiModel(value = "培训内容--学习活动vo")
public class TrainingProjectContentActivityVo {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(
        value = "活动类型：活动类型： 0课程 1考试 2调研 3直播 4投票 5作业 6证书 7外部链接 8报名 9签到 10线下课程")
    private Integer type;

    @ApiModelProperty(value = "关联的活动的id")
    private Long relationId;

    @ApiModelProperty(value = "外部链接的url")
    private String url;

    @ApiModelProperty(value = "活动名称，从活动那边取过来，不能自定义")
    private String name;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "当前活动开启条件：完成以下学习活动")
    private List<Long> conditionPreActivityIds = new ArrayList<>();

    @ApiModelProperty(value = "当前活动开启条件：完成指定学习活动数")
    private Integer conditionPreActivityNums = 0;

    @ApiModelProperty(value = "当前活动是否已经开始")
    private Boolean started = Boolean.valueOf(false);

    @ApiModelProperty(value = "当前活动是否已经完成")
    private Boolean finished = Boolean.valueOf(false);

    /**
     * 该字段仅仅用于有未完成（recorde 记录为0的那种业务记录） 如：考试、线下课程等
     */
    private Boolean unPassed = Boolean.valueOf(false);

    /**
     * 该字段仅仅用于有手动导入完成状态的活动 如：线下课程等
     */
    private Boolean unImported = Boolean.valueOf(false);

    private Boolean clicked = Boolean.valueOf(false);

    @ApiModelProperty(value = "当前状态")
    private String status;

    @ApiModelProperty(value = "活动是否已经过期")
    private Boolean expired;

    @ApiModelProperty(value = "该业务是否包含证书 --是否发放证书： true :配置开启证书，false :不开启证书")
    private Boolean hasCertificate;

    @ApiModelProperty(value = "观看类型：0: 公开播放 ; 1: 站内授权播放; ")
    private Integer viewType;

}
