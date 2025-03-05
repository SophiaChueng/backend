package com.yizhi.training.application.service.impl;

import com.yizhi.training.application.service.IExchangeCodeService;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Ignore

@SpringBootTest
public class ExchangeCodeServiceImplTest {

    @Autowired
    private IExchangeCodeService service;

    @Test
    public void testCreateCode() {
        service.createCode(20L, 15, 1000);
    }
}
