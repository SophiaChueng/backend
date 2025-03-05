package com.yizhi.training.application.vo.domain;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author fulan123
 * @since 2018-10-19
 */
@Data
@Api(tags = "statistics_training_project", description = "")
public class StatisticsTrainingProjectVo extends Model<StatisticsTrainingProjectVo> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "培训项目id")
    private Long trainingProjectId;

    @ApiModelProperty(value = "培训项目名称")
    private String trainingProjectName;

    @ApiModelProperty(value = "培训项目创建时间")
    private Date trainingProjectCreateTime;

    @ApiModelProperty(value = "培训项目开始时间")
    private Date trainingProjectStartTime;

    @ApiModelProperty(value = "培训项目结束时间")
    private Date trainingProjectEndTime;

    @ApiModelProperty(value = "培训项目状态")
    private Integer trainingProjectState;

    @ApiModelProperty(value = "培训项目部门id")
    private Long trainingProjectOrgId;

    @ApiModelProperty(value = "培训项目部门名字")
    private String trainingProjectOrgName;

    @ApiModelProperty(value = "培训项目站点id")
    private Long trainingProjectSiteId;

    @ApiModelProperty(value = "培训项目公司id")
    private Long trainingProjectCompanyId;

    @ApiModelProperty(value = "工号")
    private String workNum;

    @ApiModelProperty(value = "用户Id")
    private Long accountId;

    @ApiModelProperty(value = "用户名")
    private String name;

    @ApiModelProperty(value = "用户姓名")
    private String fullname;

    @ApiModelProperty(value = "部门编码")
    private Long orgId;

    @ApiModelProperty(value = "部门编码")
    private String orgNo;

    @ApiModelProperty(value = "所在部门")
    private String orgName;

    @ApiModelProperty(value = "部门所有父节点名称")
    private String orgParentNames;

    @ApiModelProperty(value = "0未参加 1已参加")
    private Integer joinState;

    @ApiModelProperty(value = "用户状态（0禁用1启用）")
    private Integer accountState;

    @ApiModelProperty(value = "用户部门ID")
    private Long accountOrgId;

    @ApiModelProperty(value = "用户站点ID")
    private Long accountSiteId;

    @ApiModelProperty(value = "用户公司ID")
    private Long accountCompanyId;

    @ApiModelProperty(value = "记录创建时间")
    private Date recordCreateTime;

    @Override
    public Serializable pkVal() {
        return this.id;
    }

}
