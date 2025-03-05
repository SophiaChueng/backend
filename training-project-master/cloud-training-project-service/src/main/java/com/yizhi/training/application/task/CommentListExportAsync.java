package com.yizhi.training.application.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yizhi.core.application.context.TaskContext;
import com.yizhi.core.application.file.constant.FileConstant;
import com.yizhi.core.application.file.task.AbstractDefaultTask;
import com.yizhi.core.application.file.util.OssUpload;
import com.yizhi.system.application.system.remote.AccountClient;
import com.yizhi.system.application.vo.AccountVO;
import com.yizhi.training.application.domain.TpCommentReply;
import com.yizhi.training.application.service.ITpCommentReplyService;
import com.yizhi.training.application.service.ITpCommentService;
import com.yizhi.training.application.vo.manage.PageCommentVo;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class CommentListExportAsync extends AbstractDefaultTask<String, Map<String, Object>> {

    private static final Logger logger = LoggerFactory.getLogger(CommentListExportAsync.class);

    @Autowired
    private ITpCommentService tpCommentService;

    @Autowired
    private ITpCommentReplyService tpCommentReplyService;

    @Autowired
    private AccountClient accountClient;

    @Override
    protected String execute(Map<String, Object> arg0) {
        Long accountId = (Long)arg0.get("accountId");
        Long siteId = (Long)arg0.get("siteId");
        Long companyId = (Long)arg0.get("companyId");
        Long taskId = (Long)arg0.get("taskId");
        Date submitTime = (Date)arg0.get("submitTime");
        String serialNo = (String)arg0.get("serialNo");
        String taskName = (String)arg0.get("taskName");
        Long trainingProjectId = (Long)arg0.get("trainingProjectId");
        String trainingProjectName = (String)arg0.get("trainingProjectName");
        /**
         * 走异步任务
         */
        TaskContext taskContext = new TaskContext(taskId, serialNo, taskName, accountId, submitTime, siteId, companyId);
        working(taskContext);
        String upLoadUrl = null;
        String requestPath = FileConstant.SAVE_PATH;
        File fileDir = new File(requestPath);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
        //项目评论开始组装
        List<PageCommentVo> list = tpCommentService.getList(trainingProjectId, accountId);
        List<PageCommentVo> listVO = new ArrayList<PageCommentVo>();
        TpCommentReply reply = new TpCommentReply();
        PageCommentVo vo = new PageCommentVo();
        for (PageCommentVo commentVo : list) {
            AccountVO accountVO = accountClient.findById(commentVo.getAccountId());
            commentVo.setCommentator(accountVO.getName());
            commentVo.setCommentatorName(
                null == accountVO.getFullName() || "" == accountVO.getFullName() ? accountVO.getName()
                    : accountVO.getFullName());
            commentVo.setReplyName(trainingProjectName);
            reply.setTpCommentId(commentVo.getId());
            reply.setAuditStatus("0");
            QueryWrapper<TpCommentReply> wrapper = new QueryWrapper<TpCommentReply>(reply);
            wrapper.orderByDesc("create_time");
            List<TpCommentReply> replyList = tpCommentReplyService.list(wrapper);
            listVO.add(commentVo);
            for (TpCommentReply r : replyList) {
                PageCommentVo vo1 = new PageCommentVo();
                if (null != r.getReplyParentId() && 0 != r.getReplyParentId()) {
                    TpCommentReply reply2 = tpCommentReplyService.getById(r.getReplyParentId());
                    if (null != reply2) {
                        AccountVO findById = accountClient.findById(reply2.getCreateById());
                        r.setParentAccountFullName(
                            null == findById.getFullName() || "" == findById.getFullName() ? findById.getName()
                                : findById.getFullName());
                    } else {
                        AccountVO findById =
                            accountClient.findById(tpCommentService.getById(r.getTpCommentId()).getCreateById());
                        r.setParentAccountFullName(
                            null == findById.getFullName() || "" == findById.getFullName() ? findById.getName()
                                : findById.getFullName());
                    }
                } else {
                    AccountVO findById =
                        accountClient.findById(tpCommentService.getById(r.getTpCommentId()).getCreateById());
                    r.setParentAccountFullName(
                        null == findById.getFullName() || "" == findById.getFullName() ? findById.getName()
                            : findById.getFullName());
                }
                logger.info("-----");
                AccountVO findById = accountClient.findById(r.getCreateById());
                vo1.setId(r.getId());
                vo1.setAccountId(r.getCreateById());
                vo1.setCommentator(findById.getName());
                vo1.setCommentatorName(
                    null == findById.getFullName() || "" == findById.getFullName() ? findById.getName()
                        : findById.getFullName());
                vo1.setReplyName(r.getParentAccountFullName());
                vo1.setContent(r.getContent());
                vo1.setCreateTime(r.getCreateTime());
                vo1.setThumbsUps(null);
                vo1.setState(r.getState());
                listVO.add(vo1);
            }
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            //excel生成过程: excel-->sheet-->row-->cell
            // 第一步，创建一个Excel文件
            XSSFWorkbook wb = new XSSFWorkbook();
            // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
            XSSFSheet sheet = wb.createSheet("评论信息");
            XSSFFont font = wb.createFont();
            font.setFontName("宋体");
            font.setFontHeightInPoints((short)11);
            font.setBold(true);
            XSSFCellStyle style = wb.createCellStyle();
            style.setFont(font);
            style.setAlignment(HorizontalAlignment.CENTER); // 创建一个居中格式
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
            XSSFRow row = sheet.createRow((int)0);
            row.setHeight((short)(20 * 20));
            // 合并单元格
            CellRangeAddress cra = new CellRangeAddress(0, 0, 0, 6); // 起始行, 终止行, 起始列, 终止列
            sheet.addMergedRegion(cra);
            XSSFCell cell = row.createCell((short)0);
            cell.setCellValue(trainingProjectName + "项目评论记录" + sf.format(submitTime));
            cell.setCellStyle(style);
            // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
            XSSFRow row2 = sheet.createRow((int)1);
            row2.setRowStyle(style);
            row2.setHeight((short)(14 * 20));
            // 第四步，创建单元格
            row2.createCell((short)0).setCellValue("评论人/回复人");
            row2.createCell((short)1).setCellValue("姓名");
            row2.createCell((short)2).setCellValue("评论对象");
            row2.createCell((short)3).setCellValue("评论内容");
            row2.createCell((short)4).setCellValue("提交时间");
            row2.createCell((short)5).setCellValue("点赞数");
            row2.createCell((short)6).setCellValue("状态");
            for (int i = 0; i < listVO.size(); i++) {
                font.setBold(false);
                row = sheet.createRow(i + 2);
                row.setRowStyle(style);
                row.setHeight((short)(13 * 20));
                // 第四步，创建单元格，并设置值
                vo = listVO.get(i);
                if (vo != null && vo.getCommentator() != null) {
                    row.createCell((short)0).setCellValue(vo.getCommentator());
                }
                if (vo != null && vo.getCommentatorName() != null) {
                    row.createCell((short)1)
                        .setCellValue(null == vo.getCommentatorName() ? "" : vo.getCommentatorName());
                }
                if (vo != null && vo.getReplyName() != null) {
                    row.createCell((short)2).setCellValue(null == vo.getReplyName() ? "" : vo.getReplyName());
                }
                if (vo != null && vo.getContent() != null) {
                    row.createCell((short)3).setCellValue(vo.getContent());
                }
                if (vo != null && vo.getCreateTime() != null) {
                    row.createCell((short)4).setCellValue(df.format(vo.getCreateTime()));
                }
                if (vo != null) {
                    row.createCell((short)5)
                        .setCellValue(null == vo.getThumbsUps() ? "——" : vo.getThumbsUps().toString());
                }
                if (vo != null && vo.getState() != null) {
                    row.createCell((short)6).setCellValue(0 == vo.getState() ? "已上架" : "已下架");
                }
            }

            // 第五步，写入实体数据 实际应用中这些数据从数据库得到，
            StringBuffer fileNameSb =
                new StringBuffer().append("项目评论记录").append(sf.format(submitTime)).append(".xlsx");
            String fileName = fileNameSb.toString();
            String path = new StringBuffer().append(requestPath).append(fileNameSb).toString();
            FileOutputStream os = null;
            File file = null;
            try {
                os = new FileOutputStream(path);
                wb.write(os);
                //阿里云返回url
                upLoadUrl = OssUpload.upload(path, fileName);
                file = new File(path);
                success(taskContext, "成功", upLoadUrl);
            } catch (Exception e1) {
                e1.printStackTrace();
                fail(taskContext, "写入数据到Excel的过程中或者上传到阿里云中发生错误" + e1.getMessage());
                logger.error("写入数据到Excel的过程中或者上传到阿里云中发生错误" + e1.getMessage());
            } finally {
                if (os != null) {
                    os.close();
                }
                if (wb != null) {
                    wb.close();
                }
                if (file != null) {
                    file.delete();
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail(taskContext, "评论信息导出过程中发生错误，请查看日志" + e.getMessage());
            logger.error("评论信息导出过程中发生错误，请查看日志" + e.getMessage());
        }
        return upLoadUrl;
    }

}
