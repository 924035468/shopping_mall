package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.listclient.ListFeignClient;
import com.atguigu.gmall.model.list.SearchParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping
@Slf4j
public class ListController {
    @Autowired
    ListFeignClient listFeignClient;

    @RequestMapping({"search.html","list.html"})
    public String getlistMode(SearchParam searchParam, Model model,ModelMap modelMap){

        // 通过list服务的api搜索es中的商品数据，放到页面进行渲染
        Result<Map> result = listFeignClient.list(searchParam);// 调用es的api
        Map data = result.getData();
        model.addAllAttributes(data);

        // 封装其他功能数据
        String urlParam = makeUrlParam(searchParam);
        modelMap.put("urlParam",urlParam);
        modelMap.put("searchParam",searchParam);

        // 品牌面包屑
        String trademarkIdName = searchParam.getTrademark();
        if(StringUtils.isNotBlank(trademarkIdName)){
            String[] split = trademarkIdName.split(":");
            // 获得商标名称
            modelMap.put("trademarkParam",split[1]);
        }

        // 属性面包屑
        String[] props = searchParam.getProps();
        if(null!=props&&props.length>0){
            List<Map> attrListForCrumb = new ArrayList<>();
            for (String prop : props) {
                String[] split = prop.split(":");

                Map<String,String> map = new HashMap<>();
                String attrId = split[0];
                map.put("attrId",attrId);
                String attrValue = split[1];
                map.put("attrValue",attrValue);
                String attrName = split[2];
                map.put("attrName",attrName);
                attrListForCrumb.add(map);
            }
            modelMap.put("propsParamList",attrListForCrumb);
        }

        // 排序
        String order = searchParam.getOrder();
        if(StringUtils.isNotBlank(order)){
            String[] split = order.split(":");
            String type = split[0];
            String sort = split[1];
//            if(sort.equals("asc")){
//                sort = "desc";
//            }else {
//                sort = "asc";
//            }
            Map<String,String> orderMap = new HashMap<>();
            orderMap.put("type",type);
            orderMap.put("sort",sort);
            modelMap.put("orderMap",orderMap);
        }

        return "list/index";
    }


    public static String makeUrlParam(SearchParam searchParam){
        StringBuffer urlParam = new StringBuffer("search.html?");
        Long category3Id = searchParam.getCategory3Id();

        String trademark = searchParam.getTrademark();
        String keyword = searchParam.getKeyword();
        String order = searchParam.getOrder();
        String[] props = searchParam.getProps();

        if(StringUtils.isNotBlank(keyword)){
            urlParam.append("&keyword="+keyword);

        }


        if(null != category3Id){
            urlParam.append("&category3Id="+category3Id);

        }
        if(null != props && props.length>0){
            for (String prop:
            props) {
                urlParam.append("&props="+prop);

            }
        }
        if(StringUtils.isNotBlank(trademark)){
            urlParam.append("&trademark="+trademark);
        }

        return urlParam.toString();

    }

}
