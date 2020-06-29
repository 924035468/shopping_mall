package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.SpuImageMapper;
import com.atguigu.gmall.product.mapper.SpuInfoMapper;
import com.atguigu.gmall.product.mapper.SpuSaleAttrMapper;
import com.atguigu.gmall.product.mapper.SpuSaleAttrValueMapper;
import com.atguigu.gmall.product.service.SpuSaleManageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.security.ssl.Debug;

import java.util.List;

@Service
@Slf4j
public class SpuSaleManageServiceImpl implements SpuSaleManageService {
    @Autowired
    public SpuInfoMapper spuInfoMapper;

    @Autowired
    public SpuSaleAttrMapper spuSaleAttrMapper;

    @Autowired
    public SpuSaleAttrValueMapper spuSaleAttrValueMapper;

    @Autowired
    public SpuImageMapper spuImageMapper;

    @Override
    public void saveSpuSaleInfo(SpuInfo spuInfo) {
        //保存spuInfo

        spuInfoMapper.insert(spuInfo);
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();


        //保存图片
        for (SpuImage spuImage:spuImageList
             ) {
            spuImage.setSpuId(spuInfo.getId());
            int insert = spuImageMapper.insert(spuImage);

            log.debug(insert+"");
        }
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();


                //保存spusaleattr

        for (SpuSaleAttr spuSaleAttr:spuSaleAttrList
        ) {
            spuSaleAttr.setSpuId(spuInfo.getId());
            spuSaleAttrMapper.insert(spuSaleAttr);
            List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
            for (SpuSaleAttrValue spuSaleAttrValue:spuSaleAttrValueList
            ) {
                spuSaleAttrValue.setSpuId(spuSaleAttr.getSpuId());
                spuSaleAttrValue.setBaseSaleAttrId(spuSaleAttr.getBaseSaleAttrId());
                spuSaleAttrValueMapper.insert(spuSaleAttrValue);
            }
        }

        //保存spusaleattrvlue



    }
    @Override
    public List<SpuSaleAttr> getSpuSaleAttrList(Long spuId) {
        return spuSaleAttrMapper.selectSpuSaleAttrList(spuId);
    }



}
