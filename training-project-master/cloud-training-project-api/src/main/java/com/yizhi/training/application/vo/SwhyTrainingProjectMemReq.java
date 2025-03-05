package com.yizhi.training.application.vo;

import lombok.Data;

import java.util.List;

@Data
public class SwhyTrainingProjectMemReq {

    private Long companyId;

    private Long siteId;

    private Integer type;

    private List<Long> relationIds;

}
