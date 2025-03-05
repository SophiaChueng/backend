package com.yizhi.training.application.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 授权范围
 * </p>
 *
 * @author shengchenglong
 * @since 2018-04-19
 */
@Data
@Api(tags = "TpAuthorizationRangeVo", description = "授权范围")
@TableName("tp_authorization_range")
public class TpAuthorizationRange extends Model<TpAuthorizationRange> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    private Long id;

    private Long companyId;

    @ApiModelProperty(value = "业务主体表id（如：培训项目id，考试id，调研id等等）")
    @TableField("biz_id")
    private Long bizId;

    @ApiModelProperty(value = "1.部门，2.用户，3.用户组")
    private Integer type;

    @ApiModelProperty(value = "关联id，类型由type判定")
    @TableField("relation_id")
    private Long relationId;

    @ApiModelProperty(value = "冗余授权对象的名称")
    private String name;

    @ApiModelProperty(value = "所属siteId")
    @TableField("site_id")
    private Long siteId;

    @ApiModelProperty(value = "是否删除")
    @TableLogic
    private Integer deleted;

    @ApiModelProperty(value = "用户名")
    @TableField(exist = false)
    private String fullName;

    @ApiModelProperty(value = "工号")
    @TableField(exist = false)
    private String workNum;

    @Override
    public Serializable pkVal() {
        return this.id;
    }

}
