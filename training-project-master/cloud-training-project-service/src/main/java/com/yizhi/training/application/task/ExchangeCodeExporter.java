package com.yizhi.training.application.task;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.alibaba.excel.EasyExcel;
import com.yizhi.core.application.context.TaskContext;
import com.yizhi.core.application.file.task.AbstractDefaultTask;
import com.yizhi.core.application.file.task.TaskVO;
import com.yizhi.core.application.file.util.OssUpload;
import com.yizhi.training.application.qo.ExchangeCodeQO;
import com.yizhi.training.application.service.IExchangeCodeService;
import com.yizhi.training.application.vo.ExchangeCodeExportVO;
import com.yizhi.util.application.file.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class ExchangeCodeExporter extends AbstractDefaultTask<String, TaskVO<ExchangeCodeQO>> {

    @Autowired
    private IExchangeCodeService service;

    //    @Autowired
    //    private ITrainingProjectService projService;

    @Override
    protected String execute(TaskVO<ExchangeCodeQO> param) {
        TaskContext taskContext = new TaskContext(param.getTaskId(), param.getSerialNo(), param.getTaskName(),
            param.getContext().getAccountId(), new Date(), param.getContext().getSiteId(),
            param.getContext().getCompanyId());
        working(taskContext);

        File tmpFile = null;
        try {
            List<ExchangeCodeExportVO> data = service.listExport(param.getQueryVo(), param.getContext().getSiteId());
            //            TrainingProject proj = projService.getById(param.getQueryVo().getTpId());
            tmpFile = FileUtils.createLocalTmpFile(FileUtils.EXCEL_SUFFIX_07);

            String ossExcelDate = DateUtil.format(DateUtil.date(), DatePattern.PURE_DATETIME_PATTERN);
            String ossExcelName =
                param.getQueryVo().getResourceName() + "-兑换码清单-" + ossExcelDate + FileUtils.EXCEL_SUFFIX_07;
            //            ;
            //            if (null != proj) {
            //                ossExcelName = proj.getName()
            //            } else {
            //                ossExcelName = "兑换码清单-" + ossExcelDate + FileUtils.EXCEL_SUFFIX_07;
            //            }

            EasyExcel.write(tmpFile, ExchangeCodeExportVO.class).sheet(ossExcelDate).doWrite(data);

            String ossAddress = OssUpload.upload(tmpFile.toPath().toString(), ossExcelName);

            success(taskContext, "成功", ossAddress);
        } catch (IOException e) {
            log.error("导出兑换码过程中发生错误。", e);
            fail(taskContext, "导出兑换码过程中发生错误。");
        } finally {
            if (null != tmpFile) {
                tmpFile.delete();
            }
        }

        return null;
    }

}
