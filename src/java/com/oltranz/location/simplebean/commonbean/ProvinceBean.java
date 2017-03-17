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
public class ProvinceBean {
    private String provinceId;
    private String name;
    private List<DistrictBean> districts;

    public ProvinceBean() {
    }

    public ProvinceBean(String provinceId, String name, List<DistrictBean> districts) {
        this.provinceId = provinceId;
        this.name = name;
        this.districts = districts;
    }

    public List<DistrictBean> getDistricts() {
        return districts;
    }

    public void setDistricts(List<DistrictBean> districts) {
        this.districts = districts;
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}
