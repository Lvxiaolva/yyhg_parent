package com.atguigu.yygh.cmn.service.impl;

import com.alibaba.excel.EasyExcel;
import com.atguigu.yygh.cmn.listener.DictListener;
import com.atguigu.yygh.cmn.mapper.DictMapper;
import com.atguigu.yygh.cmn.service.DictService;
import com.atguigu.yygh.model.cmn.Dict;
import com.atguigu.yygh.vo.cmn.DictEeVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {
    @Override
    public List<Dict> findChildData(Long id) {

        QueryWrapper<Dict> dictQueryWrapper = new QueryWrapper<Dict>();

        dictQueryWrapper.eq("parent_id",id);

        List<Dict> list = baseMapper.selectList(dictQueryWrapper);

        for (Dict dict : list) {
            Long dictId = dict.getId();
            boolean childen = this.isChilden(dictId);
            dict.setHasChildren(childen);
        }

        return list;
    }

    @Override
    public void exportDictData(HttpServletResponse response) {
        try {
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            String fileName = "dict";
            URLEncoder.encode("数据字典", "UTF-8");
            response.setHeader("Content-disposition", "attachment;filename="+ fileName + ".xlsx");

            List<Dict> dictList = baseMapper.selectList(null);
            List<DictEeVo> dictVoList = new ArrayList<>(dictList.size());
            for(Dict dict : dictList) {
                DictEeVo dictVo = new DictEeVo();
                BeanUtils.copyProperties(dict, dictVo, DictEeVo.class);
                dictVoList.add(dictVo);
            }

            EasyExcel.write(response.getOutputStream(), DictEeVo.class).sheet("数据字典").doWrite(dictVoList);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    @CacheEvict(value = "dict", allEntries=true)
    public void importDictData(MultipartFile file) {

        try {
            EasyExcel.read(file.getInputStream(),DictEeVo.class,new DictListener(baseMapper)).sheet().doRead();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private boolean isChilden(Long id){

        QueryWrapper<Dict> dictQueryWrapper = new QueryWrapper<Dict>();

        dictQueryWrapper.eq("parent_id",id);

        Integer count = baseMapper.selectCount(dictQueryWrapper);

        return count > 0;

    }

}
