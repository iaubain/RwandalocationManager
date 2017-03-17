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
public class SectorBean {
    private String districtId;
    private String sectorId;
    private String name;
    private List<CellBean> cells;

    public SectorBean() {
    }

    public SectorBean(String districtId, String sectorId, String name, List<CellBean> cells) {
        this.districtId = districtId;
        this.sectorId = sectorId;
        this.name = name;
        this.cells = cells;
    }

    public List<CellBean> getCells() {
        return cells;
    }

    public void setCells(List<CellBean> cells) {
        this.cells = cells;
    }

    public String getSectorId() {
        return sectorId;
    }

    public void setSectorId(String sectorId) {
        this.sectorId = sectorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDistrictId() {
        return districtId;
    }

    public void setDistrictId(String districtId) {
        this.districtId = districtId;
    }
    
}
