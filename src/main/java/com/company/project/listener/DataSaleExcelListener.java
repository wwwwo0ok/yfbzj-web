// DataSaleExcelListener.java
package com.company.project.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.util.ListUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.company.project.entity.DataSaleEntity;
import com.company.project.service.DataSaleService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 销售信息Excel导入监听器
 */
@Slf4j
public class DataSaleExcelListener extends AnalysisEventListener<DataSaleEntity> {

    /**
     * 每隔1000条存储数据库，实际使用中可以3000条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 1000;
    private List<DataSaleEntity> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
    /**
     * 假设这个是一个DAO，当然有业务逻辑的话也可以是Service。当然这里不用注入，因为每次读取excel都会new一个新的
     */
    private final DataSaleService dataSaleService;

    // 通过构造器传入Service，保证线程安全（每个线程一个监听器实例）
    public DataSaleExcelListener(DataSaleService dataSaleService) {
        this.dataSaleService = dataSaleService;
    }

    /**
     * 这个方法会一行一行的读
     *
     * @param data    one row value. Is is same as {@link AnalysisContext#readRowHolder()}
     * @param context
     */
    @Override
    public void invoke(DataSaleEntity data, AnalysisContext context) {
        log.info("解析到一条数据: {}", data);
        // 这里可以做一些数据校验，例如：
        // if (StringUtils.isBlank(data.getAgent())) {
        //     throw new RuntimeException("经销商不能为空");
        // }
        cachedDataList.add(data);
        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (cachedDataList.size() >= BATCH_COUNT) {
            saveData();
            // 存储完成清理 list
            cachedDataList.clear();
        }
    }

    /**
     * 所有数据解析完成了 都会来调用
     *
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        saveData();
        log.info("所有数据解析完成！共 {} 条", cachedDataList.size());
    }

    /**
     * 加上存储数据库
     */
    private void saveData() {
        log.info("{}条数据，开始存储数据库！", cachedDataList.size());
        // 使用MyBatis-Plus的批量保存方法
        dataSaleService.saveBatch(cachedDataList);
        log.info("存储数据库成功！");
    }
}
