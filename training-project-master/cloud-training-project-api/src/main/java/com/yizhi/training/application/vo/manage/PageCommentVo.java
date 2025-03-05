package com.yizhi.training.application.vo.manage;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yizhi.training.application.vo.domain.TpCommentReplyVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class PageCommentVo {

    @ApiModelProperty(value = "回复列表")
    List<TpCommentReplyVo> tpCommentReplies = new ArrayList<>();

    private Long id;

    @ApiModelProperty(value = "账号id")
    private Long accountId;

    @ApiModelProperty(value = "评论人")
    private String commentator;

    @ApiModelProperty(value = "评论内容")
    private String content;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "点赞数")
    private Integer thumbsUps;

    @ApiModelProperty(value = "用户头像")
    private String userAvatar;

    @ApiModelProperty(value = "是否点赞 1 是 0否")
    private Integer status;

    @ApiModelProperty(value = "是否已经点赞")
    private Boolean hasThumbsUp;

    @ApiModelProperty(value = "回复数")
    private Integer replys;

    @ApiModelProperty(value = "姓名")
    private String commentatorName;

    @ApiModelProperty("状态(0：上架|1：下架  默认为0)")
    private Integer state;

    @ApiModelProperty(value = "评论对象")
    private String replyName;

}
