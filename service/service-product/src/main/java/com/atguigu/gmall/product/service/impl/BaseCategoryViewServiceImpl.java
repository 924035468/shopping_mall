package com.atguigu.gmall.product.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.product.mapper.BaseCategoryViewMapper;
import com.atguigu.gmall.product.service.BaseCategoryViewService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BaseCategoryViewServiceImpl implements BaseCategoryViewService {

    @Autowired
    BaseCategoryViewMapper baseCategoryViewMapper;
    @Override
    public BaseCategoryView getCategoryViewByCategory3Id(Long category3Id) {

        QueryWrapper<BaseCategoryView> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category3_id",category3Id);
        BaseCategoryView baseCategoryView = baseCategoryViewMapper.selectOne(queryWrapper);

        return baseCategoryView;
    }
/*
    public static void main(String[] args) {
        BaseCategoryViewServiceImpl baseCategoryViewService = new BaseCategoryViewServiceImpl();

        baseCategoryViewService.getBaseCategoryList();

    }*/

    @Override
    public List<JSONObject> getBaseCategoryList() {
       // 声明几个json 集合
        ArrayList<JSONObject> list = new ArrayList<>();
        // 声明获取所有分类数据集合
        QueryWrapper<BaseCategoryView> queryWrapper = new QueryWrapper<>();
        List<BaseCategoryView> baseCategoryViewList = baseCategoryViewMapper.selectList(null);
        // 循环上面的集合并安一级分类Id 进行分组
        Map<Long, List<BaseCategoryView>> category1Map  = baseCategoryViewList.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id));

        String s = category1Map.toString();

        System.out.println(s);
        int index = 1;
        // 获取一级分类下所有数据//Map.Entry
        Set<Map.Entry<Long, List<BaseCategoryView>>> entries = category1Map.entrySet();
        for (Map.Entry<Long, List<BaseCategoryView>> entry1  : entries) {
            // 获取一级分类Id
            Long category1Id  = entry1.getKey();
            // 获取一级分类下面的所有集合
            List<BaseCategoryView> category2List1  = entry1.getValue();

            JSONObject category1 = new JSONObject();
            category1.put("index", index);
            category1.put("categoryId",category1Id);
            // 一级分类名称
            BaseCategoryView baseCategoryView = category2List1.get(0);
            category1.put("categoryName",category2List1.get(0).getCategory1Name());
            // 变量迭代
            index++;
            // 循环获取二级分类数据
            Map<Long, List<BaseCategoryView>> category2Map  = category2List1.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));
            // 声明二级分类对象集合
            List<JSONObject> category2Child = new ArrayList<>();
            // 循环遍历
            for (Map.Entry<Long, List<BaseCategoryView>> entry2  : category2Map.entrySet()) {
                // 获取二级分类Id
                Long category2Id  = entry2.getKey();
                // 获取二级分类下的所有集合
                List<BaseCategoryView> category3List  = entry2.getValue();
                // 声明二级分类对象
                JSONObject category2 = new JSONObject();

                category2.put("categoryId",category2Id);
                category2.put("categoryName",category3List.get(0).getCategory2Name());
                // 添加到二级分类集合
                category2Child.add(category2);

                List<JSONObject> category3Child = new ArrayList<>();

                // 循环三级分类数据
                category3List.stream().forEach(category3View -> {
                    JSONObject category3 = new JSONObject();
                    category3.put("categoryId",category3View.getCategory3Id());
                    category3.put("categoryName",category3View.getCategory3Name());
                    category3Child.add(category3);
                });
                // 将三级数据放入二级里面
                category2.put("categoryChild",category3Child);
            }
            // 将二级数据放入一级里面
            category1.put("categoryChild",category2Child);
            list.add(category1);
        }
        return list;
    }

}
