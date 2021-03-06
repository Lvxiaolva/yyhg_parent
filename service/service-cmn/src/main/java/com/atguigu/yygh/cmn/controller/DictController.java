package com.atguigu.yygh.cmn.controller;

import com.atguigu.yygh.cmn.service.DictService;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.model.cmn.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(description = "数据字典接口")
@RestController
@RequestMapping("/admin/cmn/dict")
public class DictController {

    @Autowired
    private DictService dictService;

    @ApiOperation(value = "根据数据id查询子数据列表")
    @GetMapping("findChildData/{id}")
    @Cacheable(value = "dict",keyGenerator = "keyGenerator")
    public Result findChildData(Long id){

        List<Dict> dictList = dictService.findChildData(id);

        return Result.ok(dictList);

    }

    @GetMapping("exportData")
    public Result exportData(HttpServletResponse response){

        dictService.exportDictData(response);

        return Result.ok();

    }

    @PostMapping("importData")
    @CacheEvict(value = "dict", allEntries=true)
    public Result importDict(MultipartFile file) {

        dictService.importDictData(file);

        return Result.ok();

    }

}
