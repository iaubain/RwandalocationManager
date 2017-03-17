/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.oltranz.location.logic;

import com.oltranz.location.config.AppDesc;
import com.oltranz.location.entities.Cell;
import com.oltranz.location.entities.District;
import com.oltranz.location.entities.Province;
import com.oltranz.location.entities.Sector;
import com.oltranz.location.entities.Street;
import com.oltranz.location.facades.CellFacade;
import com.oltranz.location.facades.DistrictFacade;
import com.oltranz.location.facades.ProvinceFacade;
import com.oltranz.location.facades.SectorFacade;
import com.oltranz.location.facades.StreetFacade;
import com.oltranz.location.simplebean.commonbean.CellBean;
import com.oltranz.location.simplebean.commonbean.DistrictBean;
import com.oltranz.location.simplebean.commonbean.ProvinceBean;
import com.oltranz.location.simplebean.commonbean.SectorBean;
import com.oltranz.location.simplebean.commonbean.StreetBean;
import com.oltranz.location.utilities.DataFactory;
import com.oltranz.location.utilities.RequestResponse;
import static java.lang.System.out;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.core.Response;

/**
 *
 * @author ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr /
 * aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662
 */
@Stateless
public class AppProcessor {
    @EJB
            StreetFacade streetFacade;
    @EJB
            CellFacade cellFacade;
    @EJB
            SectorFacade sectorFacade;
    @EJB
            DistrictFacade districtFacade;
    @EJB
            ProvinceFacade provinceFacade;
    
    public Response getAllData(){
        try{
            List<ProvinceBean> mProvince = new ArrayList<>();
            List<DistrictBean> mDistricts = new ArrayList<>();
            List<SectorBean> mSectors = new ArrayList<>();
            List<CellBean> mCells = new ArrayList<>();
            List<StreetBean> mStreets = new ArrayList<>();
            
            DistrictBean districtBean = new DistrictBean();
            SectorBean sectorBean = new SectorBean();
            CellBean cellBean = new CellBean();
            StreetBean streetBean;
            ProvinceBean provinceBean = new ProvinceBean();
            
            List<Street> streets;
            List<Cell> cells;
            List<Sector> sectors;
            List<District> districts;
            List<Province> provinces = getAllProvince();
            if(provinces == null){
                out.print(AppDesc.APP_DESC+" AppProcessor getAllData failed due to: empty province results");
                return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, "Province not found.");
            }
            
            for(Province province : provinces){
                districts = getDistrictByProvince(province.getId());
                if(districts == null){
                    out.print(AppDesc.APP_DESC+" AppProcessor getAllData failed due to: empty district results");
                    return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, "District not found.");
                }
                provinceBean.setName(province.getName());
                provinceBean.setProvinceId(province.getId()+"");
                
                for(District district : districts){
                    districtBean.setProvinceId(district.getProvinceId()+"");
                    districtBean.setDistrictId(district.getId()+"");
                    districtBean.setName(district.getName());
                    
                    sectors = getSectorByDistrict(district.getId());
                    if(sectors == null){
                        out.print(AppDesc.APP_DESC+" AppProcessor getAllData failed due to: empty sector results");
                        return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, "Sectors not found.");
                    }
                    
                    for(Sector sector : sectors){
                        sectorBean.setDistrictId(sector.getDistrictId()+"");
                        sectorBean.setSectorId(sector.getId()+"");
                        sectorBean.setName(sector.getName());
                        
                        cells = getCellBySector(sector.getId());
                        if(cells == null){
                            out.print(AppDesc.APP_DESC+" AppProcessor getAllData failed due to: empty cell results");
                            return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, "Cells not found.");
                        }
                        
                        for(Cell cell : cells){
                            cellBean.setSectorId(cell.getSectorId()+"");
                            cellBean.setCellId(cell.getId()+"");
                            cellBean.setCellName(cell.getName());
                            
                            streets = getStreetByCell(cell.getId());
                            
                            if(streets != null){
                                for(Street street : streets){
                                    streetBean = new StreetBean(cell.getId()+"",
                                            street.getId()+"",
                                            street.getName(),
                                            street.getLongitude() != null ? street.getLongitude() : "",
                                            street.getLatitude() != null ? street.getLatitude() : "");
                                    mStreets.add(streetBean);
                                }
                            }
                            cellBean.setStreets(mStreets);
                            mCells.add(cellBean);
                            mStreets = new ArrayList<>();
                            cellBean = new CellBean();
                        }
                        
                        sectorBean.setCells(mCells);
                        mSectors.add(sectorBean);
                        mCells= new ArrayList<>();
                        sectorBean = new SectorBean();
                    }
                    
                    districtBean.setSectors(mSectors);
                    mDistricts.add(districtBean);
                    mSectors = new ArrayList<>();
                    districtBean = new DistrictBean();
                }
                provinceBean.setDistricts(mDistricts);
                mProvince.add(provinceBean);
                mDistricts = new ArrayList<>();
                provinceBean = new ProvinceBean();
            }
            
            return RequestResponse.success(DataFactory.objectToString(mProvince));
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+" AppProcessor getAllData failed due to: "+e.getMessage());
            return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, e.getLocalizedMessage());
        }
    }
    
    public Response getProvinceByName(String body){
        try{
            List<DistrictBean> mDistricts = new ArrayList<>();
            List<SectorBean> mSectors = new ArrayList<>();
            List<CellBean> mCells = new ArrayList<>();
            List<StreetBean> mStreets = new ArrayList<>();
            
            DistrictBean districtBean = new DistrictBean();
            SectorBean sectorBean = new SectorBean();
            CellBean cellBean = new CellBean();
            StreetBean streetBean;
            ProvinceBean provinceBean = new ProvinceBean();
            
            List<Street> streets;
            List<Cell> cells;
            List<Sector> sectors;
            List<District> districts;
            Province province = getProvinceName(body);
            if(province == null){
                out.print(AppDesc.APP_DESC+" AppProcessor getAllData failed due to: empty province results");
                return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, "Province not found.");
            }
            
            districts = getDistrictByProvince(province.getId());
            if(districts == null){
                out.print(AppDesc.APP_DESC+" AppProcessor getAllData failed due to: empty district results");
                return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, "District not found.");
            }
            provinceBean.setName(province.getName());
            provinceBean.setProvinceId(province.getId()+"");
            
            for(District district : districts){
                districtBean.setProvinceId(district.getProvinceId()+"");
                districtBean.setDistrictId(district.getId()+"");
                districtBean.setName(district.getName());
                
                sectors = getSectorByDistrict(district.getId());
                if(sectors == null){
                    out.print(AppDesc.APP_DESC+" AppProcessor getAllData failed due to: empty sector results");
                    return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, "Sectors not found.");
                }
                
                for(Sector sector : sectors){
                    sectorBean.setDistrictId(sector.getDistrictId()+"");
                    sectorBean.setSectorId(sector.getId()+"");
                    sectorBean.setName(sector.getName());
                    
                    cells = getCellBySector(sector.getId());
                    if(cells == null){
                        out.print(AppDesc.APP_DESC+" AppProcessor getAllData failed due to: empty cell results");
                        return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, "Cells not found.");
                    }
                    
                    for(Cell cell : cells){
                        cellBean.setSectorId(cell.getSectorId()+"");
                        cellBean.setCellId(cell.getId()+"");
                        cellBean.setCellName(cell.getName());
                        
                        streets = getStreetByCell(cell.getId());
                        
                        if(streets != null){
                            for(Street street : streets){
                                streetBean = new StreetBean(cell.getId()+"",
                                        street.getId()+"",
                                        street.getName(),
                                        street.getLongitude() != null ? street.getLongitude() : "",
                                        street.getLatitude() != null ? street.getLatitude() : "");
                                mStreets.add(streetBean);
                            }
                        }
                        cellBean.setStreets(mStreets);
                        mCells.add(cellBean);
                        mStreets = new ArrayList<>();
                        cellBean = new CellBean();
                    }
                    
                    sectorBean.setCells(mCells);
                    mSectors.add(sectorBean);
                    mCells= new ArrayList<>();
                    sectorBean = new SectorBean();
                }
                
                districtBean.setSectors(mSectors);
                mDistricts.add(districtBean);
                mSectors = new ArrayList<>();
                districtBean = new DistrictBean();
            }
            provinceBean.setDistricts(mDistricts);
            
            return RequestResponse.success(DataFactory.objectToString(provinceBean));
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+" AppProcessor getProvinceByName failed due to: "+e.getMessage());
            return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, e.getLocalizedMessage());
        }
    }
    
    public Response getProvinceById(String body){
        try{
            List<DistrictBean> mDistricts = new ArrayList<>();
            List<SectorBean> mSectors = new ArrayList<>();
            List<CellBean> mCells = new ArrayList<>();
            List<StreetBean> mStreets = new ArrayList<>();
            
            DistrictBean districtBean = new DistrictBean();
            SectorBean sectorBean = new SectorBean();
            CellBean cellBean = new CellBean();
            StreetBean streetBean;
            ProvinceBean provinceBean = new ProvinceBean();
            
            List<Street> streets;
            List<Cell> cells;
            List<Sector> sectors;
            List<District> districts;
            Province province = getProvinceId(stringToLong(body));
            if(province == null){
                out.print(AppDesc.APP_DESC+" AppProcessor getAllData failed due to: empty province results");
                return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, "Province not found.");
            }
            
            districts = getDistrictByProvince(province.getId());
            if(districts == null){
                out.print(AppDesc.APP_DESC+" AppProcessor getAllData failed due to: empty district results");
                return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, "District not found.");
            }
            provinceBean.setName(province.getName());
            provinceBean.setProvinceId(province.getId()+"");
            
            for(District district : districts){
                districtBean.setProvinceId(district.getProvinceId()+"");
                districtBean.setDistrictId(district.getId()+"");
                districtBean.setName(district.getName());
                
                sectors = getSectorByDistrict(district.getId());
                if(sectors == null){
                    out.print(AppDesc.APP_DESC+" AppProcessor getAllData failed due to: empty sector results");
                    return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, "Sectors not found.");
                }
                
                for(Sector sector : sectors){
                    sectorBean.setDistrictId(sector.getDistrictId()+"");
                    sectorBean.setSectorId(sector.getId()+"");
                    sectorBean.setName(sector.getName());
                    
                    cells = getCellBySector(sector.getId());
                    if(cells == null){
                        out.print(AppDesc.APP_DESC+" AppProcessor getAllData failed due to: empty cell results");
                        return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, "Cells not found.");
                    }
                    
                    for(Cell cell : cells){
                        cellBean.setSectorId(cell.getSectorId()+"");
                        cellBean.setCellId(cell.getId()+"");
                        cellBean.setCellName(cell.getName());
                        
                        streets = getStreetByCell(cell.getId());
                        
                        if(streets != null){
                            for(Street street : streets){
                                streetBean = new StreetBean(cell.getId()+"",
                                        street.getId()+"",
                                        street.getName(),
                                        street.getLongitude() != null ? street.getLongitude() : "",
                                        street.getLatitude() != null ? street.getLatitude() : "");
                                mStreets.add(streetBean);
                            }
                        }
                        cellBean.setStreets(mStreets);
                        mCells.add(cellBean);
                        mStreets = new ArrayList<>();
                        cellBean = new CellBean();
                    }
                    
                    sectorBean.setCells(mCells);
                    mSectors.add(sectorBean);
                    mCells= new ArrayList<>();
                    sectorBean = new SectorBean();
                }
                
                districtBean.setSectors(mSectors);
                mDistricts.add(districtBean);
                mSectors = new ArrayList<>();
                districtBean = new DistrictBean();
            }
            provinceBean.setDistricts(mDistricts);
            
            return RequestResponse.success(DataFactory.objectToString(provinceBean));
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+" AppProcessor getProvinceByName failed due to: "+e.getMessage());
            return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, e.getLocalizedMessage());
        }
    }
    
    public Response getDistrictByName(String body){
        try{
            List<SectorBean> mSectors = new ArrayList<>();
            List<CellBean> mCells = new ArrayList<>();
            List<StreetBean> mStreets = new ArrayList<>();
            DistrictBean districtBean = new DistrictBean();
            SectorBean sectorBean = new SectorBean();
            CellBean cellBean = new CellBean();
            StreetBean streetBean;
            List<Street> streets;
            List<Cell> cells;
            List<Sector> sectors;
            
            District district = getDistrictName(body);
            if(district == null){
                out.print(AppDesc.APP_DESC+" AppProcessor getAllData failed due to: empty district results");
                return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, "District not found.");
            }
            districtBean.setProvinceId(district.getProvinceId()+"");
            districtBean.setDistrictId(district.getId()+"");
            districtBean.setName(district.getName());
            sectors = getSectorByDistrict(district.getId());
            if(sectors == null){
                out.print(AppDesc.APP_DESC+" AppProcessor getAllData failed due to: empty sector results");
                return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, "Sectors not found.");
            }
            
            for(Sector sector : sectors){
                sectorBean.setDistrictId(sector.getDistrictId()+"");
                sectorBean.setSectorId(sector.getId()+"");
                sectorBean.setName(sector.getName());
                cells = getCellBySector(sector.getId());
                if(cells == null){
                    out.print(AppDesc.APP_DESC+" AppProcessor getAllData failed due to: empty cell results");
                    return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, "Cells not found.");
                }
                
                for(Cell cell : cells){
                    cellBean.setSectorId(cell.getSectorId()+"");
                    cellBean.setCellId(cell.getId()+"");
                    cellBean.setCellName(cell.getName());
                    streets = getStreetByCell(cell.getId());
                    if(streets != null){
                        for(Street street : streets){
                            streetBean = new StreetBean(cell.getId()+"",
                                    street.getId()+"",
                                    street.getName(),
                                    street.getLongitude() != null ? street.getLongitude() : "",
                                    street.getLatitude() != null ? street.getLatitude() : "");
                            mStreets.add(streetBean);
                        }
                    }
                    cellBean.setStreets(mStreets);
                    mCells.add(cellBean);
                    mStreets = new ArrayList<>();
                    cellBean = new CellBean();
                }
                sectorBean.setCells(mCells);
                mSectors.add(sectorBean);
                mCells= new ArrayList<>();
                sectorBean = new SectorBean();
            }
            districtBean.setSectors(mSectors);
            
            return RequestResponse.success(DataFactory.objectToString(districtBean));
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+" AppProcessor getProvinceByName failed due to: "+e.getMessage());
            return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, e.getLocalizedMessage());
        }
    }
    
    public Response getDistrictById(String body){
        try{
            List<SectorBean> mSectors = new ArrayList<>();
            List<CellBean> mCells = new ArrayList<>();
            List<StreetBean> mStreets = new ArrayList<>();
            DistrictBean districtBean = new DistrictBean();
            SectorBean sectorBean = new SectorBean();
            CellBean cellBean = new CellBean();
            StreetBean streetBean;
            List<Street> streets;
            List<Cell> cells;
            List<Sector> sectors;
            District district = getDistrictId(stringToLong(body));
            if(district == null){
                out.print(AppDesc.APP_DESC+" AppProcessor getAllData failed due to: empty district results");
                return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, "District not found.");
            }
            districtBean.setProvinceId(district.getProvinceId()+"");
            districtBean.setDistrictId(district.getId()+"");
            districtBean.setName(district.getName());
            sectors = getSectorByDistrict(district.getId());
            if(sectors == null){
                out.print(AppDesc.APP_DESC+" AppProcessor getAllData failed due to: empty sector results");
                return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, "Sectors not found.");
            }
            for(Sector sector : sectors){
                sectorBean.setDistrictId(sector.getDistrictId()+"");
                sectorBean.setSectorId(sector.getId()+"");
                sectorBean.setName(sector.getName());
                
                cells = getCellBySector(sector.getId());
                if(cells == null){
                    out.print(AppDesc.APP_DESC+" AppProcessor getAllData failed due to: empty cell results");
                    return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, "Cells not found.");
                }
                
                for(Cell cell : cells){
                    cellBean.setSectorId(cell.getSectorId()+"");
                    cellBean.setCellId(cell.getId()+"");
                    cellBean.setCellName(cell.getName());
                    
                    streets = getStreetByCell(cell.getId());
                    
                    if(streets != null){
                        for(Street street : streets){
                            streetBean = new StreetBean(cell.getId()+"",
                                    street.getId()+"",
                                    street.getName(),
                                    street.getLongitude() != null ? street.getLongitude() : "",
                                    street.getLatitude() != null ? street.getLatitude() : "");
                            mStreets.add(streetBean);
                        }
                    }
                    cellBean.setStreets(mStreets);
                    mCells.add(cellBean);
                    mStreets = new ArrayList<>();
                    cellBean = new CellBean();
                }
                sectorBean.setCells(mCells);
                mSectors.add(sectorBean);
                mCells= new ArrayList<>();
                sectorBean = new SectorBean();
            }
            districtBean.setSectors(mSectors);
            
            return RequestResponse.success(DataFactory.objectToString(districtBean));
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+" AppProcessor getProvinceByName failed due to: "+e.getMessage());
            return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, e.getLocalizedMessage());
        }
    }
    
    public Response getSectorByName(String body){
        try{
            List<SectorBean> mSectors = new ArrayList<>();
            List<CellBean> mCells = new ArrayList<>();
            List<StreetBean> mStreets = new ArrayList<>();
            SectorBean sectorBean = new SectorBean();
            CellBean cellBean = new CellBean();
            StreetBean streetBean;
            List<Street> streets;
            List<Cell> cells;
            List<Sector> sectors;
            sectors = getSectorName(body);
            if(sectors == null){
                out.print(AppDesc.APP_DESC+" AppProcessor getAllData failed due to: empty sector results");
                return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, "Sector not found.");
            }
            for(Sector sector : sectors){
                sectorBean.setDistrictId(sector.getDistrictId()+"");
                sectorBean.setSectorId(sector.getId()+"");
                sectorBean.setName(sector.getName());
                cells = getCellBySector(sector.getId());
                if(cells == null){
                    out.print(AppDesc.APP_DESC+" AppProcessor getAllData failed due to: empty cell results");
                    return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, "Cells not found.");
                }
                for(Cell cell : cells){
                    cellBean.setSectorId(cell.getSectorId()+"");
                    cellBean.setCellId(cell.getId()+"");
                    cellBean.setCellName(cell.getName());
                    streets = getStreetByCell(cell.getId());
                    if(streets != null){
                        for(Street street : streets){
                            streetBean = new StreetBean(cell.getId()+"",
                                    street.getId()+"",
                                    street.getName(),
                                    street.getLongitude() != null ? street.getLongitude() : "",
                                    street.getLatitude() != null ? street.getLatitude() : "");
                            mStreets.add(streetBean);
                        }
                    }
                    cellBean.setStreets(mStreets);
                    mCells.add(cellBean);
                    mStreets = new ArrayList<>();
                    cellBean = new CellBean();
                }
                sectorBean.setCells(mCells);
                mSectors.add(sectorBean);
                mCells= new ArrayList<>();
                sectorBean = new SectorBean();
            }
            return RequestResponse.success(DataFactory.objectToString(mSectors));
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+" AppProcessor getProvinceByName failed due to: "+e.getMessage());
            return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, e.getLocalizedMessage());
        }
    }
    
    public Response getSectorById(String body){
        try{
            List<CellBean> mCells = new ArrayList<>();
            List<StreetBean> mStreets = new ArrayList<>();
            SectorBean sectorBean = new SectorBean();
            CellBean cellBean = new CellBean();
            StreetBean streetBean;
            List<Street> streets;
            List<Cell> cells;
            Sector sector = getSectorId(stringToLong(body));
            if(sector == null){
                out.print(AppDesc.APP_DESC+" AppProcessor getAllData failed due to: empty sector results");
                return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, "Sector not found.");
            }
            sectorBean.setDistrictId(sector.getDistrictId()+"");
            sectorBean.setSectorId(sector.getId()+"");
            sectorBean.setName(sector.getName());
            cells = getCellBySector(sector.getId());
            if(cells == null){
                out.print(AppDesc.APP_DESC+" AppProcessor getAllData failed due to: empty cell results");
                return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, "Cells not found.");
            }
            for(Cell cell : cells){
                cellBean.setSectorId(cell.getSectorId()+"");
                cellBean.setCellId(cell.getId()+"");
                cellBean.setCellName(cell.getName());
                streets = getStreetByCell(cell.getId());
                if(streets != null){
                    for(Street street : streets){
                        streetBean = new StreetBean(
                                cell.getId()+"",
                                street.getId()+"",
                                street.getName(),
                                street.getLongitude() != null ? street.getLongitude() : "",
                                street.getLatitude() != null ? street.getLatitude() : "");
                        mStreets.add(streetBean);
                    }
                }
                cellBean.setStreets(mStreets);
                mCells.add(cellBean);
                mStreets = new ArrayList<>();
                cellBean = new CellBean();
            }
            sectorBean.setCells(mCells);
            return RequestResponse.success(DataFactory.objectToString(sectorBean));
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+" AppProcessor getProvinceByName failed due to: "+e.getMessage());
            return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, e.getLocalizedMessage());
        }
    }
    
    public Response getCellByName(String body){
        try{
            List<CellBean> mCells = new ArrayList<>();
            List<StreetBean> mStreets = new ArrayList<>();
            CellBean cellBean = new CellBean();
            StreetBean streetBean;
            List<Street> streets;
            List<Cell> cells;
            cells = getCellName(body);
            if(cells == null){
                out.print(AppDesc.APP_DESC+" AppProcessor getAllData failed due to: empty cell results");
                return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, "Cells not found.");
            }
            for(Cell cell : cells){
                cellBean.setCellId(cell.getId()+"");
                cellBean.setCellName(cell.getName());
                cellBean.setSectorId(cell.getSectorId()+"");
                streets = getStreetByCell(cell.getId());
                if(streets != null){
                    for(Street street : streets){
                        streetBean = new StreetBean(
                                cell.getId()+"",
                                street.getId()+"",
                                street.getName(),
                                street.getLongitude() != null ? street.getLongitude() : "",
                                street.getLatitude() != null ? street.getLatitude() : "");
                        mStreets.add(streetBean);
                    }
                }
                cellBean.setStreets(mStreets);
                mCells.add(cellBean);
                mStreets = new ArrayList<>();
                cellBean = new CellBean();
            }
            return RequestResponse.success(DataFactory.objectToString(mCells));
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+" AppProcessor getProvinceByName failed due to: "+e.getMessage());
            return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, e.getLocalizedMessage());
        }
    }
    
    public Response getCellById(String body){
        try{
            List<StreetBean> mStreets = new ArrayList<>();
            CellBean cellBean = new CellBean();
            StreetBean streetBean;
            List<Street> streets;
            Cell cell = getCellId(stringToLong(body));
            if(cell == null){
                out.print(AppDesc.APP_DESC+" AppProcessor getAllData failed due to: empty cell results");
                return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, "Cell not found.");
            }
            cellBean.setSectorId(cell.getSectorId()+"");
            cellBean.setCellId(cell.getId()+"");
            cellBean.setCellName(cell.getName());
            
            streets = getStreetByCell(cell.getId());
            
            if(streets != null){
                for(Street street : streets){
                    streetBean = new StreetBean(
                            cell.getId()+"",
                            street.getId()+"",
                            street.getName(),
                            street.getLongitude() != null ? street.getLongitude() : "",
                            street.getLatitude() != null ? street.getLatitude() : "");
                    mStreets.add(streetBean);
                }
            }
            cellBean.setStreets(mStreets);
            return RequestResponse.success(DataFactory.objectToString(cellBean));
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+" AppProcessor getProvinceByName failed due to: "+e.getMessage());
            return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, e.getLocalizedMessage());
        }
    }
    
    private List<Province> getAllProvince(){
        List<Province> provinces = provinceFacade.getAllData();
        return provinces != null ? provinces : null;
    }
    
    private Province getProvinceName(String provinceName){
        Province province = provinceFacade.getProvinceByName(provinceName);
        return province != null ? province : null;
    }
    
    private Province getProvinceId(Long provinceId){
        Province province = provinceFacade.getProvinceById(provinceId);
        return province != null ? province : null;
    }
    
    private List<District> getDistrictByProvince(Long provinceId){
        List<District> districts = districtFacade.getDistrictByProvince(provinceId);
        return districts != null ? districts : null;
    }
    
    private District getDistrictName(String districtName){
        District district = districtFacade.getDistrictByName(districtName);
        return district != null ? district : null;
    }
    
    private District getDistrictId(Long districtId){
        District district = districtFacade.getDistrictById(districtId);
        return district != null ? district : null;
    }
    
    private List<Sector> getSectorByDistrict(Long districtId){
        List<Sector> sectors = sectorFacade.getSectorByDistrict(districtId);
        return sectors != null ? sectors : null;
    }
    private List<Sector> getSectorName(String sectorName){
        List<Sector> sectors = sectorFacade.getSectorByName(sectorName);
        return sectors != null ? sectors : null;
    }
    private Sector getSectorId(Long sectorId){
        Sector sector = sectorFacade.getSectorById(sectorId);
        return sector != null ? sector : null;
    }
    private List<Cell> getCellBySector(Long sectorId){
        List<Cell> cells = cellFacade.getListCellBySector(sectorId);
        return cells != null ? cells : null;
    }
    
    private List<Cell> getCellName(String sectorName){
        List<Cell> cells = cellFacade.getCellListByName(sectorName);
        return cells != null ? cells : null;
    }
    private Cell getCellId(Long cellId){
        Cell cell = cellFacade.getCellById(cellId);
        return cell != null ? cell : null;
    }
    
    private List<Street> getStreetByCell(Long cellId){
        List<Street> streets = streetFacade.getStreetsByCellId(cellId);
        return streets != null ? streets : null;
    }
    
    private Long stringToLong(String value){
        return Long.parseLong(value);
    }
}
