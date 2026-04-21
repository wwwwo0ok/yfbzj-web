package com.company.project.common.listener;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
public class GenericExcelListener<T> extends AnalysisEventListener<T> {

    /**
     * 每解析完一行数据的回调函数（业务处理逻辑，如入库、校验）
     */
    private final Consumer<T> rowConsumer;

    /**
     * 成功解析并处理的行数据（可根据需要调整存储方式）
     */
    @Getter
    private final List<T> successRows = new ArrayList<>();

    /**
     * 失败的行记录（行号 + 原始数据 + 异常信息）
     */
    @Getter
    private final List<ExcelErrorRow> errorRows = new ArrayList<>();

    /**
     * 是否跳过空行
     */
    private final boolean skipEmptyRow;

    /**
     * 最大允许解析行数（0表示不限制）
     */
    private final int maxRowLimit;

    /**
     * 当前解析的行索引（从0开始）
     */
    private int currentRowIndex = 0;

    /**
     * 构造方法
     * @param rowConsumer 每行数据的业务处理逻辑（可抛出运行时异常）
     * @param skipEmptyRow 是否跳过空行
     * @param maxRowLimit 最大允许行数（0不限制）
     */
    public GenericExcelListener(Consumer<T> rowConsumer, boolean skipEmptyRow, int maxRowLimit) {
        this.rowConsumer = rowConsumer;
        this.skipEmptyRow = skipEmptyRow;
        this.maxRowLimit = maxRowLimit;
    }

    // 简化的构造方法（跳过空行，无行数限制）
    public GenericExcelListener(Consumer<T> rowConsumer) {
        this(rowConsumer, true, 0);
    }

    @Override
    public void invoke(T data, AnalysisContext context) {
        currentRowIndex++;
        // 行号从1开始（Excel中的实际行号，因为头部占一行）
        Integer rowNum = context.readRowHolder().getRowIndex() + 1;

        // 跳过空行（可根据业务判断：data中所有字段为null或空字符串）
        if (skipEmptyRow && isEmptyRow(data)) {
            log.debug("跳过空行，行号：{}", rowNum);
            return;
        }

        // 限制最大行数
        if (maxRowLimit > 0 && successRows.size() + errorRows.size() >= maxRowLimit) {
            log.warn("已达到最大行数限制：{}，停止解析", maxRowLimit);
            return;
        }

        try {
            // 执行业务处理（可能抛出异常）
            rowConsumer.accept(data);
            successRows.add(data);
        } catch (Exception e) {
            log.error("处理第{}行数据失败：{}", rowNum, data, e);
            errorRows.add(new ExcelErrorRow(rowNum, data, e.getMessage()));
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("Excel解析完成，总成功行数：{}，失败行数：{}", successRows.size(), errorRows.size());
    }

    /**
     * 判断一行数据是否为空（所有字段为null或空字符串）
     * 这里简单使用反射判断，也可由调用方传入校验器
     */
    private boolean isEmptyRow(T data) {
        if (data == null) return true;
        try {
            for (java.lang.reflect.Field field : data.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(data);
                if (value != null) {
                    if (value instanceof String && ((String) value).trim().isEmpty()) {
                        continue;
                    }
                    return false;
                }
            }
        } catch (IllegalAccessException e) {
            log.warn("判断空行时反射异常", e);
            return false;
        }
        return true;
    }

    /**
     * 异常行记录类
     */
    @Getter
    public static class ExcelErrorRow {
        private final int rowNum;
        private final Object rawData;
        private final String errorMessage;

        public ExcelErrorRow(int rowNum, Object rawData, String errorMessage) {
            this.rowNum = rowNum;
            this.rawData = rawData;
            this.errorMessage = errorMessage;
        }
    }
}