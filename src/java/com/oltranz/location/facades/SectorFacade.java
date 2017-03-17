/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oltranz.location.facades;

import com.oltranz.location.config.AppDesc;
import com.oltranz.location.entities.Sector;
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
public class SectorFacade extends AbstractFacade<Sector> {

    @PersistenceContext(unitName = "LocationManagementPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SectorFacade() {
        super(Sector.class);
    }
    
    public Sector getSectorById(Long sectorId){
        try{
            if(sectorId == null)
                return null;
            Query q= em.createQuery("Select S from Sector S WHERE S.id = :sectorId");
            q.setParameter("sectorId", sectorId);
            List<Sector> list = (List<Sector>)q.getResultList();
            return list.isEmpty() ? null : list.get(0);
        }catch(Exception ex){
            out.print(AppDesc.APP_DESC+" SectorFacade getSectorById failed due to:"+ex.getMessage());
            return null;
        }
    }
    
    public List<Sector> getSectorByName(String sectorName){
        try{
            if(sectorName.isEmpty())
                return null;
            Query q= em.createQuery("Select S from Sector S WHERE S.name = :sectorName");
            q.setParameter("sectorName", sectorName);
            List<Sector> list = (List<Sector>)q.getResultList();
            return list.isEmpty() ? null : list;
        }catch(Exception ex){
            out.print(AppDesc.APP_DESC+" SectorFacade getSectorByName failed due to:"+ex.getMessage());
            return null;
        }
    }
    
    public List<Sector> getSectorByDistrict(Long districtId){
        try{
            if(districtId == null)
                return null;
            Query q= em.createQuery("Select S from Sector S WHERE S.districtId = :districtId");
            q.setParameter("districtId", districtId);
            List<Sector> list = (List<Sector>)q.getResultList();
            return list.isEmpty() ? null : list;
        }catch(Exception ex){
            out.print(AppDesc.APP_DESC+" SectorFacade getSectorByDistrict failed due to:"+ex.getMessage());
            return null;
        }
    }
}
