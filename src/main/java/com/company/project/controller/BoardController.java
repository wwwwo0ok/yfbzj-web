package com.company.project.controller;

import cn.dev33.satoken.stp.StpUtil;
import io.swagger.annotations.Api;

import java.io.IOException;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.company.project.baidu.BaiduMapService;

/**
 * 大屏
 *
 * @author Li Xiaolong
 * @version V1.0
 * @date 2025年11月28日
 */
@Api(tags = "大屏")
@Controller
@RequestMapping("")
public class BoardController {

	@Resource
	BaiduMapService baiduMapService;
   

    /**
     * 大屏首页
     */
    @GetMapping("/board/pointMap")
    public String pointMap() {
        return "board/pointMap";
    }
    /**
     * 大屏首页
     */
    @GetMapping("/board/pointMap2")
    public String pointMap2() {
    	return "board/pointMap2";
    }
    /**
     * 大屏首页
     */
    @GetMapping("/board/pointMap3")
    public String pointMap3() {
    	return "board/pointMap3";
    }
    /**
     * 大屏首页
     */
    @GetMapping("/board/pointMap4")
    public String pointMap4() {
    	return "board/pointMap4";
    }
    /**
     * 大屏首页
     */
    @GetMapping("/board/pointMap5")
    public String pointMap5() {
    	return "board/pointMap5";
    }
    /**
     * 大屏首页
     */
    @GetMapping("/board/pointMap6")
    public String pointMap6() {
    	return "board/pointMap6";
    }
    /**
     * 大屏首页
     */
    @GetMapping("/board/pointMap7")
    public String pointMap7() {
    	return "board/pointMap7";
    }

   
}
