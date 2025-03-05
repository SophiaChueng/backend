package com.yizhi.training.application.v2.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel
public class TpHeadTeacherVO implements Serializable {

    private Long accountId;

    private Long trainingProjectId;

    private String name;

    private String fullName;

    private String workNum;
}
