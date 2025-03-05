package com.yizhi.training.application.exception;

import lombok.Data;

/**
 * @Author: shengchenglong
 * @Date: 2018/6/5 15:48
 */
@Data
public class TrainingProjectException extends Exception {

    private String message;

    public TrainingProjectException(String message) {
        this.message = message;
    }
}
