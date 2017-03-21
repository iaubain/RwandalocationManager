/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oltranz.location.simplebean.commonbean;

import java.util.List;

/**
 *
 * @author ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr /
 * aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662
 */
public class DistrictCommonBean {
    private String provinceId;
    private String districtId;
    private String name;
    private List<SectorCommonBean> sectors;

    public DistrictCommonBean() {
    }

    public DistrictCommonBean(String provinceId, String districtId, String name, List<SectorCommonBean> sectors) {
        this.provinceId = provinceId;
        this.districtId = districtId;
        this.name = name;
        this.sectors = sectors;
    }

    public List<SectorCommonBean> getSectors() {
        return sectors;
    }

    public void setSectors(List<SectorCommonBean> sectors) {
        this.sectors = sectors;
    }

    public String getDistrictId() {
        return districtId;
    }

    public void setDistrictId(String districtId) {
        this.districtId = districtId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }
    
}
