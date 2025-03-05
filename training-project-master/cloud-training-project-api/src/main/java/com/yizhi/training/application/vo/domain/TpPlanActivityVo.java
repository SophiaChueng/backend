package com.yizhi.training.application.vo.domain;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.yizhi.training.application.vo.manage.TpPlanActivityConditionVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 学习计划中的活动
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-27
 */
@Data
@Api(tags = "TpPlanActivityVo", description = "学习计划中的活动")
public class TpPlanActivityVo extends Model<TpPlanActivityVo> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "冗余培训项目id")
    private Long trainingProjectId;

    @ApiModelProperty(value = "学习计划id")
    private Long tpPlanId;

    @ApiModelProperty(
        value = "活动类型： 0课程 1考试 2调研 3直播 4投票 5作业 6证书 7外部链接 8报名 9签到 10线下课程 11案例活动 12精选案例 13 资料 14论坛帖子")
    private Integer type;

    @ApiModelProperty(value = "关联的活动的id")
    private Long relationId;

    @ApiModelProperty(value = "活动名称，从活动那边取过来，不能自定义")
    private String name;

    @ApiModelProperty(value = "外部链接地址")
    private String address;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "是否删除（1是，0否），默认否")
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

    @ApiModelProperty(value = "开启条件")
    private List<TpPlanActivityConditionPreVo> conditionPres;

    @ApiModelProperty(value = "完成条件（目前只有考试有完成条件）")
    private List<TpPlanActivityConditionPostVo> conditionPosts;

    @ApiModelProperty(value = "条件设置，不持久化")
    private TpPlanActivityConditionVo condition = new TpPlanActivityConditionVo();

    @ApiModelProperty(value = "是否已经完成")
    private Boolean finished;

    @Override
    public Serializable pkVal() {
        return this.id;
    }

}
