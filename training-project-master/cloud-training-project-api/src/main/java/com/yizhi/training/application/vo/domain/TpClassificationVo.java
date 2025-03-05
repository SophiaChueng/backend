package com.yizhi.training.application.vo.domain;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 培训项目分类表
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-27
 */
@Data
@Api(tags = "TpClassificationVo", description = "培训项目分类表")
public class TpClassificationVo extends Model<TpClassificationVo> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "分类名")
    private String name;

    @ApiModelProperty(value = "分类描述")
    private String description;

    @ApiModelProperty(value = "是否删除（0：否，1：是）")
    private Integer deleted;

    @ApiModelProperty(value = "创建者id")
    private Long createById;

    @ApiModelProperty(value = "创建者名称")
    private String createByName;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新者id")
    private Long updateById;

    @ApiModelProperty(value = "修改者名称")
    private String updateByName;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    @ApiModelProperty(value = "站点id")
    private Long siteId;

    @ApiModelProperty(value = "组织id")
    private Long orgId;

    @ApiModelProperty(value = "企业id")
    private Long companyId;

    private Integer relationNum;

    private Integer sort;

    @Override
    public Serializable pkVal() {
        return this.id;
    }

}
