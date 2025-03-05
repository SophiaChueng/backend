package com.yizhi.training.application.v2.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * 项目可见范围用户id
 */
@Builder
@Data
public class VisibleRangeModel implements Serializable {

    private static final long serialVersionUID = -7065261983004128044L;

    private Integer type;

    private Set<Long> accountSet;
}
