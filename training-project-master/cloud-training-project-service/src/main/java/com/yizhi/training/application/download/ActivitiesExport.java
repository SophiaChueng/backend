package com.yizhi.training.application.download;

import com.alibaba.fastjson.JSON;
import com.yizhi.core.application.context.RequestContext;
import com.yizhi.core.application.context.TaskContext;
import com.yizhi.core.application.file.constant.FileConstant;
import com.yizhi.core.application.file.task.AbstractDefaultTask;
import com.yizhi.core.application.file.util.OssUpload;
import com.yizhi.course.application.feign.CourseClient;
import com.yizhi.training.application.domain.TpPlan;
import com.yizhi.training.application.domain.TpPlanActivity;
import com.yizhi.training.application.domain.TrainingProject;
import com.yizhi.training.application.service.ITpPlanActivityService;
import com.yizhi.training.application.service.ITpPlanService;
import com.yizhi.training.application.util.ExportUtil;
import com.yizhi.training.application.v2.enums.TpActivityTypeEnum;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class ActivitiesExport extends AbstractDefaultTask<String, Map<String, Object>> {

    private static final Logger logger = LoggerFactory.getLogger(ActivitiesExport.class);

    @Autowired
    private ITpPlanActivityService tpPlanActivityService;

    @Autowired
    private ITpPlanService tpPlanService;

    @Autowired
    private CourseClient courseClient;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    private SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd    HH:mm:ss");

    /**
     * 创建标题行  并返回标题行的标题个数
     *
     * @param workbook
     * @param sheet
     * @param row
     * @param cell
     * @return
     */
    public void createTile(HSSFWorkbook workbook, HSSFSheet sheet, HSSFRow row, HSSFCell cell, String[] title) {
        HSSFCellStyle style2 = workbook.createCellStyle();
        style2.setAlignment(HorizontalAlignment.CENTER);//创建居中格式
        HSSFFont font2 = (HSSFFont)workbook.createFont();//创建字体格式
        font2.setFontHeightInPoints((short)12);
        style2.setFont(font2);
        row = sheet.createRow((int)2);
        for (int i = 0; i < title.length; i++) {
            cell = row.createCell((short)i);
            cell.setCellStyle(style2);
            cell.setCellValue(title[i]);
        }
    }

    public Map<Long, String> getCourseCodeName(List<TpPlanActivity> activities) {
        Map<Long, String> codeNameMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(activities)) {
            List<Long> courseIds = new ArrayList<>();
            activities.forEach(a -> {
                if (a.getType().equals(0)) {
                    courseIds.add(a.getRelationId());
                }
            });
            if (!CollectionUtils.isEmpty(courseIds)) {
                codeNameMap = courseClient.getCourseCodeName(courseIds);
            }
        }
        return codeNameMap;
    }

    public void setColumnWidth(Map<Integer, Integer> maxWidth, int columnSize, String value) {
        for (int i = 0; i < columnSize; i++) {
            Integer newValueSize = value.getBytes().length;
            Integer oldValueSize = maxWidth.get(i);
            oldValueSize = (oldValueSize == null ? 0 : oldValueSize);
            maxWidth.put(i, Math.max(oldValueSize, newValueSize));
        }
    }

    // 0课程 1考试 2调研 3直播 4投票 5作业 6证书 7外部链接 8报名 9签到 10线下课程"
    @Deprecated
    private String getTypeName(Integer type) {
        String typeName = "";
        switch (type) {
            case 0:
                typeName = "在线课程";
                break;
            case 1:
                typeName = "考试";
                break;
            case 2:
                typeName = "调研";
                break;
            case 3:
                typeName = "直播";
                break;
            case 4:
                typeName = "投票";
                break;
            case 5:
                typeName = "作业";
                break;
            case 6:
                typeName = "证书";
                break;
            case 7:
                typeName = "外部链接";
                break;
            case 8:
                typeName = "报名";
                break;
            case 9:
                typeName = "签到";
                break;
            case 10:
                typeName = "线下课程";
                break;
            case 11:
                typeName = "原创活动";
                break;
            case 12:
                typeName = "精选作品";
                break;
            case 13:
                typeName = "资料";
                break;
            case 14:
                typeName = "帖子";
                break;
        }
        return typeName;
    }

    @Override
    protected String execute(Map<String, Object> map) {
        String str = JSON.toJSONString(map.get("trainingProject")); //转化成JSON字符串
        TrainingProject trainingProject = JSON.parseObject(str, TrainingProject.class); //将JSON转化成对象
        str = JSON.toJSONString(map.get("context")); //转化成JSON字符串
        RequestContext context = JSON.parseObject(str, RequestContext.class);
        String taskName = (String)map.get("taskName");
        String serialNo = (String)map.get("serialNo");
        Long taskId = (Long)map.get("taskId");

        Date submitTime = new Date();
        TaskContext taskContext =
            new TaskContext(taskId, serialNo, taskName, context.getAccountId(), submitTime, context.getSiteId(),
                context.getCompanyId());
        working(taskContext);

        String upLoadUrl = null;
        String requestPath = FileConstant.SAVE_PATH;
        //        String requestPath = "E\\";
        File fileDir = new File(requestPath);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }

        //excel生成：excel-sheet-row-cell
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(
            ExportUtil.replaceUnsupportedSheetName(trainingProject.getName()) + "_" + dateFormat.format(submitTime));
        // 在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short,

        // 创建第1行
        HSSFRow row = sheet.createRow((0));
        //创建单元格格式
        HSSFCellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);//创建居中格式
        HSSFFont font = (HSSFFont)workbook.createFont();//创建字体格式
        font.setFontHeightInPoints((short)16);//字号
        style.setFont(font);
        //合并单元格
        CellRangeAddress cra = new CellRangeAddress(0, 0, 0, 3); // 起始行, 终止行, 起始列, 终止列
        sheet.addMergedRegion(cra);
        HSSFCell cell = row.createCell((short)0);
        String title = trainingProject.getName() + "活动清单" + dateFormat.format(submitTime.getTime());
        cell.setCellStyle(style);
        cell.setCellValue(title);

        //创建第2行
        HSSFCellStyle style1 = workbook.createCellStyle();
        style1.setAlignment(HorizontalAlignment.CENTER);//创建居中格式
        HSSFFont font1 = (HSSFFont)workbook.createFont();//创建字体格式
        font1.setFontHeightInPoints((short)14);
        style1.setFont(font1);
        row = sheet.createRow((int)1);
        CellRangeAddress cra1 = new CellRangeAddress(1, 1, 0, 3); // 起始行, 终止行, 起始列, 终止列
        sheet.addMergedRegion(cra1);
        cell = row.createCell((short)0);
        String title1 = "报表生成时间: " + dateFormat1.format(submitTime);
        cell.setCellValue(title1);
        cell.setCellStyle(style1);

        //初始化列宽，存储最大列宽
        Map<Integer, Integer> maxWidth = new HashMap<Integer, Integer>();
        for (int i = 0; i < 4; i++) {
            maxWidth.put(i, title.getBytes().length * 256 + 200);
        }

        //创建第三行 创建标题
        String[] titles = new String[] {"活动名称", "单元名称", "活动类型", "课程编码"};
        createTile(workbook, sheet, row, cell, titles);
        Integer columenSize = titles.length;

        //获取数据
        List<TpPlanActivity> activities = new ArrayList<>();
        List<TpPlan> tpPlans = tpPlanService.listAll(trainingProject.getId());

        if (null == tpPlans || tpPlans.size() == 0) {
            logger.info("该培训无数据");
            fail(taskContext, "该培训无数据");
            return "该培训无数据";
        }

        Map<Long, String> planIdNameMap = new HashMap<>(tpPlans.size());
        Map<Long, String> courseCodeMap = new HashMap<>();

        tpPlans.forEach(a -> {
            if (!planIdNameMap.containsKey(a.getId())) {
                planIdNameMap.put(a.getId(), a.getName());
            }
            List<TpPlanActivity> activityList = tpPlanActivityService.getByTpPlanId(a.getId());
            if (!CollectionUtils.isEmpty(activityList)) {
                activities.addAll(activityList);
            }
        });

        //开始填充数据
        try {
            if (!CollectionUtils.isEmpty(activities)) {
                int dataIndex = 3;
                //获取课程编码map
                courseCodeMap = getCourseCodeName(activities);
                for (int i = 0; i < activities.size(); i++) {
                    row = sheet.createRow(dataIndex);
                    dataIndex++;
                    //第一列
                    String activityName = activities.get(i).getName();
                    row.createCell(0).setCellValue(activityName);
                    setColumnWidth(maxWidth, columenSize, activityName);
                    //第二列
                    String planName = planIdNameMap.get(activities.get(i).getTpPlanId());
                    row.createCell(1).setCellValue(planName);
                    setColumnWidth(maxWidth, columenSize, planName);

                    //第三列
                    String typeName = TpActivityTypeEnum.getDescription(activities.get(i).getType());
                    row.createCell(2).setCellValue(typeName);
                    setColumnWidth(maxWidth, columenSize, typeName);

                    //第四列
                    String codeName = "- -";
                    if (activities.get(i).getType().equals(0)) {
                        if (courseCodeMap != null) {
                            codeName = courseCodeMap.get(activities.get(i).getRelationId());
                        }
                    }
                    row.createCell(3).setCellValue(codeName);
                    setColumnWidth(maxWidth, columenSize, codeName);
                }
            }

            // 列宽自适应
            for (int i = 0; i < columenSize; i++) {
                sheet.setColumnWidth(i, maxWidth.get(i));
            }
            StringBuffer fileNameSb = new StringBuffer().append(
                ExportUtil.replaceUnsupportedSheetName(trainingProject.getName()) + "活动清单_" + dateFormat.format(
                    submitTime)).append(".xls");
            String fileName = fileNameSb.toString();
            String path = new StringBuffer().append(requestPath).append("/").append(fileNameSb).toString();
            FileOutputStream os = null;
            File file = null;
            try {
                os = new FileOutputStream(path);
                workbook.write(os);
                //阿里云返回url
                upLoadUrl = OssUpload.upload(path, fileName);
                file = new File(path);
                success(taskContext, "成功", upLoadUrl);
            } catch (Exception e1) {
                e1.printStackTrace();
                fail(taskContext, "写入过程中发生错误");
                logger.error("写入数据到Excel的过程中或者上传到阿里云中发生错误");
            } finally {
                if (os != null) {
                    os.close();
                }
                if (workbook != null) {
                    workbook.close();
                }
                if (file != null) {
                    file.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail(taskContext, trainingProject.getName() + "导出过程中发生错误，请查看日志");
            logger.error("创建Excel的过程中发生错误");
        }
        return upLoadUrl;
    }
}

