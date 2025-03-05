package com.yizhi.training.application.param;

import lombok.Data;

/**
 * 培训项目查询参数
 *
 * @Author: shengchenglong
 * @Date: 2018/5/12 11:38
 */
@Data
public class TrainingProjectParam {

    private String name;

    private Long companyId;

    private Long orgId;

    private Long siteId;

    private Status status;

    public enum Status {

        UP(1), DOWN(2);

        private int code;

        Status(int code) {
            this.code = code;
        }

        public int getCode() {
            return this.code;
        }
    }

}
