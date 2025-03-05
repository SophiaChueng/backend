package com.yizhi.training.application.domain;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
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
@TableName("statistics_training_project")
public class StatisticsTrainingProject extends Model<StatisticsTrainingProject> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "培训项目id")
    @TableField("training_project_id")
    private Long trainingProjectId;

    @ApiModelProperty(value = "培训项目名称")
    @TableField("training_project_name")
    private String trainingProjectName;

    @ApiModelProperty(value = "培训项目创建时间")
    @TableField("training_project_create_time")
    private Date trainingProjectCreateTime;

    @ApiModelProperty(value = "培训项目开始时间")
    @TableField("training_project_start_time")
    private Date trainingProjectStartTime;

    @ApiModelProperty(value = "培训项目结束时间")
    @TableField("training_project_end_time")
    private Date trainingProjectEndTime;

    @ApiModelProperty(value = "培训项目状态")
    @TableField("training_project_state")
    private Integer trainingProjectState;

    @ApiModelProperty(value = "培训项目部门id")
    @TableField("training_project_org_id")
    private Long trainingProjectOrgId;

    @ApiModelProperty(value = "培训项目部门名字")
    @TableField("training_project_org_name")
    private String trainingProjectOrgName;

    @ApiModelProperty(value = "培训项目站点id")
    @TableField("training_project_site_id")
    private Long trainingProjectSiteId;

    @ApiModelProperty(value = "培训项目公司id")
    @TableField("training_project_company_id")
    private Long trainingProjectCompanyId;

    @ApiModelProperty(value = "工号")
    @TableField("work_num")
    private String workNum;

    @ApiModelProperty(value = "用户Id")
    @TableField("account_id")
    private Long accountId;

    @ApiModelProperty(value = "用户名")
    private String name;

    @ApiModelProperty(value = "用户姓名")
    private String fullname;

    @ApiModelProperty(value = "部门编码")
    @TableField("org_id")
    private Long orgId;

    @ApiModelProperty(value = "部门编码")
    @TableField("org_no")
    private String orgNo;

    @ApiModelProperty(value = "所在部门")
    @TableField("org_name")
    private String orgName;

    @ApiModelProperty(value = "部门所有父节点名称")
    @TableField("org_parent_names")
    private String orgParentNames;

    @ApiModelProperty(value = "0未参加 1已参加")
    @TableField("join_state")
    private Integer joinState;

    @ApiModelProperty(value = "用户状态（0禁用1启用）")
    @TableField("account_state")
    private Integer accountState;

    @ApiModelProperty(value = "用户部门ID")
    @TableField("account_org_id")
    private Long accountOrgId;

    @ApiModelProperty(value = "用户站点ID")
    @TableField("account_site_id")
    private Long accountSiteId;

    @ApiModelProperty(value = "用户公司ID")
    @TableField("account_company_id")
    private Long accountCompanyId;

    @ApiModelProperty(value = "记录创建时间")
    @TableField("record_create_time")
    private Date recordCreateTime;

    @Override
    public Serializable pkVal() {
        return this.id;
    }

}
