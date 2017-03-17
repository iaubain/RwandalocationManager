/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oltranz.location.facades;

import com.oltranz.location.config.AppDesc;
import com.oltranz.location.entities.Province;
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
public class ProvinceFacade extends AbstractFacade<Province> {

    @PersistenceContext(unitName = "LocationManagementPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProvinceFacade() {
        super(Province.class);
    }
    
    public Province getProvinceById(Long provinceId){
        try{
            if(provinceId == null)
                return null;
            Query q= em.createQuery("Select P from Province P WHERE P.id = :provinceId");
            q.setParameter("provinceId", provinceId);
            List<Province> list = (List<Province>)q.getResultList();
            return list.isEmpty() ? null : list.get(0);
        }catch(Exception ex){
            out.print(AppDesc.APP_DESC+" ProvinceFacade getProvinceById failed due to:"+ex.getMessage());
            return null;
        }
    }
    
    public Province getProvinceByName(String provinceName){
        try{
            if(provinceName.isEmpty())
                return null;
            Query q= em.createQuery("Select P from Province P WHERE P.name = :provinceName");
            q.setParameter("provinceName", provinceName);
            List<Province> list = (List<Province>)q.getResultList();
            return list.isEmpty() ? null : list.get(0);
        }catch(Exception ex){
            out.print(AppDesc.APP_DESC+" ProvinceFacade getProvinceByName failed due to:"+ex.getMessage());
            return null;
        }
    }
    
    public List<Province> getAllData (){
        try{
            Query q= em.createQuery("Select P from Province P");
            List<Province> list = (List<Province>)q.getResultList();
            return list.isEmpty() ? null : list;
        }catch(Exception ex){
            out.print(AppDesc.APP_DESC+" ProvinceFacade getAllData failed due to:"+ex.getMessage());
            return null;
        }
    }
}
