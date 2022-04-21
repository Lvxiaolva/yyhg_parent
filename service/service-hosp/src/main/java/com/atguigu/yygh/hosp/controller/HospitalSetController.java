package com.atguigu.yygh.hosp.controller;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.common.until.MD5;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.vo.hosp.HospitalSetQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
public class HospitalSetController {

    @Autowired
    private HospitalSetService hospitalSetService;

    //查询所有
    @GetMapping("/findAll")
    public Result<List<HospitalSet>> findAllHospitalSet(){

        List<HospitalSet> list = hospitalSetService.list();

        log.info(list.toString());

        return Result.ok(list);

    }

    //根据ID删除
    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable Long id){

        log.info("id = {}", id);

        boolean flag = hospitalSetService.removeById(id);

        if (flag) {
            return Result.ok();
        }else {
            return Result.fail();
        }

    }

    //分页条件查询
    @PostMapping("/findPageHospSet/{current}/{limit}")
    public Result findPageHospSet(@PathVariable int current,
                                  @PathVariable int limit,
                                  @RequestBody(required = false) HospitalSetQueryVo hospitalSetQueryVo){

        log.info("page = {},pageSize = {},hospitalSetQueryVo = {}" ,current,limit,hospitalSetQueryVo.toString());

        Page<HospitalSet> voPage = new Page<HospitalSet>(current,limit);

        String hosname = hospitalSetQueryVo.getHosname();

        String hoscode = hospitalSetQueryVo.getHoscode();

        QueryWrapper<HospitalSet> hospitalSetQueryWrapper = new QueryWrapper<>();

        hospitalSetQueryWrapper.like(StringUtils.isNotEmpty(hosname),"hosname",hosname);

        hospitalSetQueryWrapper.like(StringUtils.isNotEmpty(hoscode),"hoscode",hoscode);

        Page<HospitalSet> pages = hospitalSetService.page(voPage,hospitalSetQueryWrapper);

        return Result.ok(pages);

    }

    //添加
    @PostMapping("/saveHospitalSet")
    public Result saveHospitalSet(@RequestBody HospitalSet hospitalSet){

        log.info("新增，医院信息：{}",hospitalSet.toString());

        hospitalSet.setStatus(1);

        int v = (int) (Math.random() * 1000);

        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis() +""+ v));

        boolean save = hospitalSetService.save(hospitalSet);

        if (save){
            return Result.ok();
        }else {
            return Result.fail();
        }

    }

    //根据ID查询
    @GetMapping("/getHospSet/{id}")
    public Result findById(@PathVariable Long id){

        log.info("id = {}", id);

        HospitalSet byId = hospitalSetService.getById(id);

        if (byId != null){
            return Result.ok(byId);
        }else {
            return Result.fail();
        }

    }

    //修改
    @PutMapping("/updateHospitalSet")
    public Result update(@RequestBody HospitalSet hospitalSet){

        log.info("修改，医院信息：{}", hospitalSet.toString());

        boolean flag = hospitalSetService.updateById(hospitalSet);

        if (flag){
            return Result.ok();
        }else {
            return Result.fail();
        }

    }

    //批量删除
    @DeleteMapping("/batchRemove")
    public Result deleteByIds(@RequestParam List<Long> ids){

        log.info("ids = {}", ids);

        boolean flag = hospitalSetService.removeByIds(ids);

        if (flag){
            return Result.ok();
        }else {
            return Result.fail();
        }

    }

    //锁定和解锁
    @PutMapping("/lockHospitalSet/{id}/{status}")
    public Result lock(@PathVariable Long id,
                       @PathVariable Integer status){

        HospitalSet hospitalSet = hospitalSetService.getById(id);

        hospitalSet.setStatus(status);

        hospitalSetService.updateById(hospitalSet);

        return Result.ok();
    }

    //发送签名密钥
    @PutMapping("sendKey/{id}")
    public Result lockHospitalSet(@PathVariable Long id) {
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        String signKey = hospitalSet.getSignKey();
        String hoscode = hospitalSet.getHoscode();
        //TODO 发送短信
        return Result.ok();
    }

}

