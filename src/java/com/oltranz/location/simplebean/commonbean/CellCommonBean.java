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
public class CellCommonBean {
    private String sectorId;
    private String cellId;
    private String cellName;
    private List<StreetCommonBean> streets;

    public CellCommonBean() {
    }

    public CellCommonBean(String sectorId, String cellId, String cellName, List<StreetCommonBean> streets) {
        this.sectorId = sectorId;
        this.cellId = cellId;
        this.cellName = cellName;
        this.streets = streets;
    }

    public List<StreetCommonBean> getStreets() {
        return streets;
    }

    public void setStreets(List<StreetCommonBean> streets) {
        this.streets = streets;
    }

    public String getCellId() {
        return cellId;
    }

    public void setCellId(String cellId) {
        this.cellId = cellId;
    }
    
    public String getCellName() {
        return cellName;
    }

    public void setCellName(String cellName) {
        this.cellName = cellName;
    }

    public String getSectorId() {
        return sectorId;
    }

    public void setSectorId(String sectorId) {
        this.sectorId = sectorId;
    }
    
}
