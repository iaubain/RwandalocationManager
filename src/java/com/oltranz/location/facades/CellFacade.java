/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oltranz.location.facades;

import com.oltranz.location.config.AppDesc;
import com.oltranz.location.entities.Cell;
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
public class CellFacade extends AbstractFacade<Cell> {

    @PersistenceContext(unitName = "LocationManagementPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CellFacade() {
        super(Cell.class);
    }
    
    public Cell getCellById(Long cellId){
        try{
            if(cellId == null)
                return null;
            Query q= em.createQuery("Select C from Cell C WHERE C.id = :cellId");
            q.setParameter("cellId", cellId);
            List<Cell> list = (List<Cell>)q.getResultList();
            return list.isEmpty() ? null : list.get(0);
        }catch(Exception ex){
            out.print(AppDesc.APP_DESC+" CellFacade getCellById failed due to:"+ex.getMessage());
            return null;
        }
    }
    
    public List<Cell> getCellListByName(String cellName){
        try{
            if(cellName.isEmpty())
                return null;
            Query q= em.createQuery("Select C from Cell C WHERE C.name = :cellName");
            q.setParameter("cellName", cellName);
            List<Cell> list = (List<Cell>)q.getResultList();
            return list.isEmpty() ? null : list;
        }catch(Exception ex){
            out.print(AppDesc.APP_DESC+" CellFacade getCellListByName failed due to:"+ex.getMessage());
            return null;
        }
    }
    
    public List<Cell> getListCellBySector(Long sectorId){
        try{
            if(sectorId == null)
                return null;
            Query q= em.createQuery("Select C from Cell C WHERE C.sectorId = :sectorId");
            q.setParameter("sectorId", sectorId);
            List<Cell> list = (List<Cell>)q.getResultList();
            return list.isEmpty() ? null : list;           
        }catch(Exception ex){
            out.print(AppDesc.APP_DESC+" CellFacade getCellListBySector failed due to:"+ex.getMessage());
            return null;
        }
    }
}
