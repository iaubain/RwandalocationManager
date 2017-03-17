/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oltranz.location.facades;

import com.oltranz.location.config.AppDesc;
import com.oltranz.location.entities.Street;
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
public class StreetFacade extends AbstractFacade<Street> {

    @PersistenceContext(unitName = "LocationManagementPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public StreetFacade() {
        super(Street.class);
    }
    
    public List<Street> getStreetsByCellId(Long cellId){
        try{
            if(cellId == null)
                return null;
            Query q= em.createQuery("Select S from Street S WHERE S.cellId = :cellId");
            q.setParameter("cellId", cellId);
            List<Street> list = (List<Street>)q.getResultList();
            return list.isEmpty() ? null : list;
        }catch(Exception ex){
            out.print(AppDesc.APP_DESC+" StreetFacade getStreetsByCellId failed due to:"+ex.getMessage());
            return null;
        }
    }
}
