package com.yizhi.training.application.vo.domain;

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
public class TpAuthorizationRangeVo extends Model<TpAuthorizationRangeVo> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    private Long id;

    private Long companyId;

    @ApiModelProperty(value = "业务主体表id（如：培训项目id，考试id，调研id等等）")
    private Long bizId;

    @ApiModelProperty(value = "1.部门，2.用户，3.用户组")
    private Integer type;

    @ApiModelProperty(value = "关联id，类型由type判定")
    private Long relationId;

    @ApiModelProperty(value = "冗余授权对象的名称")
    private String name;

    @ApiModelProperty(value = "所属siteId")
    private Long siteId;

    @ApiModelProperty(value = "是否删除")
    private Integer deleted;

    @ApiModelProperty(value = "用户名")
    private String fullName;

    @ApiModelProperty(value = "工号")
    private String workNum;

    @Override
    public Serializable pkVal() {
        return this.id;
    }

}
