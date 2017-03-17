/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oltranz.location.facades;

import com.oltranz.location.config.AppDesc;
import com.oltranz.location.entities.District;
import static java.lang.System.out;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr /
 * aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662
 */
@Stateless
public class DistrictFacade extends AbstractFacade<District> {

    @PersistenceContext(unitName = "LocationManagementPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DistrictFacade() {
        super(District.class);
    }
    
    public District getDistrictById(Long districtId){
        try{
            if(districtId == null)
                return null;
            Query q= em.createQuery("Select D from District D WHERE D.id = :districtId");
            q.setParameter("districtId", districtId);
            List<District> list = (List<District>)q.getResultList();
            return list.isEmpty() ? null : list.get(0);
        }catch(Exception ex){
            out.print(AppDesc.APP_DESC+" SectorFacade getSectorById failed due to:"+ex.getMessage());
            return null;
        }
    }
    
    public District getDistrictByName(String districtName){
        try{
            if(districtName.isEmpty())
                return null;
            Query q= em.createQuery("Select D from District D WHERE D.name = :districtName");
            q.setParameter("districtName", districtName);
            List<District> list = (List<District>)q.getResultList();
            return list.isEmpty() ? null : list.get(0);
        }catch(Exception ex){
            out.print(AppDesc.APP_DESC+" SectorFacade getDistrictByName failed due to:"+ex.getMessage());
            return null;
        }
    }
    
    public List<District> getDistrictByProvince(Long provinceId){
        try{
            if(provinceId == null)
                return null;
            Query q= em.createQuery("Select D from District D WHERE D.provinceId = :provinceId");
            q.setParameter("provinceId", provinceId);
            List<District> list = (List<District>)q.getResultList();
            return list.isEmpty() ? null : list;
        }catch(Exception ex){
            out.print(AppDesc.APP_DESC+" SectorFacade getDistrictByProvince failed due to:"+ex.getMessage());
            return null;
        }
    }
}
