package com.yizhi.training.application.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @ClassName RemoteDaIndicatorVo
 * @Description TODO
 * @Author shengchenglong
 * @DATE 2019-10-11 17:23
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RemoteDaIndicatorVo {

    private Long siteId;

    private Date startDate;

    private Date endDate;

    private Date day;

    private Date processTime;

}
