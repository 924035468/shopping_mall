package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.list.SearchAttr;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SearchAttrMapper extends BaseMapper<SearchAttr> {
    List<SearchAttr> selectSearchAttrs(@Param("skuId") Long skuId);

}
