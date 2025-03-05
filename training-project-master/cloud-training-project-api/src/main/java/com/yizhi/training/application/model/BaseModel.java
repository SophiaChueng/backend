package com.yizhi.training.application.model;

import com.yizhi.core.application.context.RequestContext;
import lombok.Data;

import java.util.Date;

/**
 * @Author: shengchenglong
 * @Date: 2018/3/27 15:55
 */
@Data
public class BaseModel<T> {

    private RequestContext context;

    private Date date;

    private T obj;

}
