package com.yizhi.training.application.v2.param;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("证书补发结果")
public class CetificateReissueFileVO {

    @ExcelProperty("用户名")
    @ColumnWidth(25)
    private String accountName;

    @ExcelProperty("姓名")
    @ColumnWidth(20)
    private String fullName;

    @ExcelProperty("证书名")
    @ColumnWidth(50)
    private String cetificateName;
}
