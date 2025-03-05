package com.yizhi.training.application.vo.domain;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 评论点赞记录
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-27
 */
@Data
@Api(tags = "TpCommentThumbsUpVo", description = "评论点赞记录")
public class TpCommentThumbsUpVo extends Model<TpCommentThumbsUpVo> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "评论id")
    private Long tpCommentId;

    @ApiModelProperty(value = "点赞人id")
    private Long accountId;

    @ApiModelProperty(value = "是否已经点赞")
    private Boolean hasThumbsUp;

    @Override
    public Serializable pkVal() {
        return this.id;
    }

}
