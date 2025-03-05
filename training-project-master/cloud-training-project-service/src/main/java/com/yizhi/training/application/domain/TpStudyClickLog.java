package com.yizhi.training.application.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("tp_study_click_log")
public class TpStudyClickLog extends Model<TpStudyClickLog> {

    private Long id;

    /**
     * 用户id
     */
    private Long accountId;

    /**
     * 站点id
     */
    private Long siteId;

    /**
     * 企业id
     */
    private Long companyId;

    /**
     * 发生时间
     */
    private Date createdAt;

    /**
     * 点击的培训项目id
     */
    private Long trainingProjectId;

    @Override
    public Serializable pkVal() {
        return id;
    }

}
