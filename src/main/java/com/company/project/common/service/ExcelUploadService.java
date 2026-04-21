package com.company.project.common.service;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.exception.ExcelAnalysisException;
import com.company.project.common.listener.GenericExcelListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ExcelUploadService {

    /**
     * 通用上传方法
     * @param file 上传的Excel文件
     * @param targetClass 目标实体类的Class
     * @param rowConsumer 每一行数据的处理逻辑（可抛出运行时异常）
     * @param headRowNumber 表头行数（EasyExcel中头部占用的行数，通常为1）
     * @param skipEmptyRow 是否跳过空行
     * @param maxRowLimit 最大行数限制
     * @return 上传结果（成功行数、失败行详情）
     */
    public <T> UploadResult<T> upload(MultipartFile file, Class<T> targetClass,
                                      Consumer<T> rowConsumer,
                                      int headRowNumber,
                                      boolean skipEmptyRow,
                                      int maxRowLimit) {
        // 校验文件
        if (file.isEmpty()) {
            throw new IllegalArgumentException("上传文件为空");
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || (!originalFilename.endsWith(".xls") && !originalFilename.endsWith(".xlsx"))) {
            throw new IllegalArgumentException("仅支持.xls或.xlsx格式的Excel文件");
        }

        GenericExcelListener<T> listener = new GenericExcelListener<>(rowConsumer, skipEmptyRow, maxRowLimit);
        try {
            EasyExcel.read(file.getInputStream(), targetClass, listener)
                    .headRowNumber(headRowNumber)
                    .sheet()
                    .doRead();
        } catch (IOException e) {
            log.error("读取Excel文件失败", e);
            throw new ExcelAnalysisException("读取Excel文件失败: " + e.getMessage());
        }

        // 构建返回结果
        UploadResult<T> result = new UploadResult<>();
        result.setSuccessRows(listener.getSuccessRows());
        result.setSuccessCount(listener.getSuccessRows().size());
        result.setErrorRows(listener.getErrorRows());
        result.setErrorCount(listener.getErrorRows().size());
        return result;
    }

    // 简化方法：默认头部占1行，跳过空行，无行数限制
    public <T> UploadResult<T> upload(MultipartFile file, Class<T> targetClass, Consumer<T> rowConsumer) {
        return upload(file, targetClass, rowConsumer, 1, true, 0);
    }

    /**
     * 上传结果封装
     */
    public static class UploadResult<T> {
        private List<T> successRows;
        private int successCount;
        private List<GenericExcelListener.ExcelErrorRow> errorRows;
        private int errorCount;

        // getter/setter 省略（可用lombok）
        public List<T> getSuccessRows() { return successRows; }
        public void setSuccessRows(List<T> successRows) { this.successRows = successRows; }
        public int getSuccessCount() { return successCount; }
        public void setSuccessCount(int successCount) { this.successCount = successCount; }
        public List<GenericExcelListener.ExcelErrorRow> getErrorRows() { return errorRows; }
        public void setErrorRows(List<GenericExcelListener.ExcelErrorRow> errorRows) { this.errorRows = errorRows; }
        public int getErrorCount() { return errorCount; }
        public void setErrorCount(int errorCount) { this.errorCount = errorCount; }
    }
}