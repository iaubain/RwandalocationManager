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
import com.oltranz.location.simplebean.cell.CellBean;
import com.oltranz.location.simplebean.commonbean.CellCommonBean;
import com.oltranz.location.simplebean.commonbean.DistrictCommonBean;
import com.oltranz.location.simplebean.commonbean.ProvinceCommonBean;
import com.oltranz.location.simplebean.commonbean.SectorCommonBean;
import com.oltranz.location.simplebean.commonbean.StreetCommonBean;
import com.oltranz.location.simplebean.district.DistrictBean;
import com.oltranz.location.simplebean.province.ProvinceBean;
import com.oltranz.location.simplebean.sector.SectorBean;
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
            List<ProvinceCommonBean> mProvince = new ArrayList<>();
            List<DistrictCommonBean> mDistricts = new ArrayList<>();
            List<SectorCommonBean> mSectors = new ArrayList<>();
            List<CellCommonBean> mCells = new ArrayList<>();
            List<StreetCommonBean> mStreets = new ArrayList<>();
            
            DistrictCommonBean districtBean = new DistrictCommonBean();
            SectorCommonBean sectorBean = new SectorCommonBean();
            CellCommonBean cellBean = new CellCommonBean();
            StreetCommonBean streetBean;
            ProvinceCommonBean provinceBean = new ProvinceCommonBean();
            
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
                                    streetBean = new StreetCommonBean(cell.getId()+"",
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
                            cellBean = new CellCommonBean();
                        }
                        
                        sectorBean.setCells(mCells);
                        mSectors.add(sectorBean);
                        mCells= new ArrayList<>();
                        sectorBean = new SectorCommonBean();
                    }
                    
                    districtBean.setSectors(mSectors);
                    mDistricts.add(districtBean);
                    mSectors = new ArrayList<>();
                    districtBean = new DistrictCommonBean();
                }
                provinceBean.setDistricts(mDistricts);
                mProvince.add(provinceBean);
                mDistricts = new ArrayList<>();
                provinceBean = new ProvinceCommonBean();
            }
            
            return RequestResponse.success(DataFactory.objectToString(mProvince));
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+" AppProcessor getAllData failed due to: "+e.getMessage());
            return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, e.getLocalizedMessage());
        }
    }
    
    public Response getProvinceByName(String body){
        try{
            List<DistrictCommonBean> mDistricts = new ArrayList<>();
            List<SectorCommonBean> mSectors = new ArrayList<>();
            List<CellCommonBean> mCells = new ArrayList<>();
            List<StreetCommonBean> mStreets = new ArrayList<>();
            
            DistrictCommonBean districtBean = new DistrictCommonBean();
            SectorCommonBean sectorBean = new SectorCommonBean();
            CellCommonBean cellBean = new CellCommonBean();
            StreetCommonBean streetBean;
            ProvinceCommonBean provinceBean = new ProvinceCommonBean();
            
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
                                streetBean = new StreetCommonBean(cell.getId()+"",
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
                        cellBean = new CellCommonBean();
                    }
                    
                    sectorBean.setCells(mCells);
                    mSectors.add(sectorBean);
                    mCells= new ArrayList<>();
                    sectorBean = new SectorCommonBean();
                }
                
                districtBean.setSectors(mSectors);
                mDistricts.add(districtBean);
                mSectors = new ArrayList<>();
                districtBean = new DistrictCommonBean();
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
            List<DistrictCommonBean> mDistricts = new ArrayList<>();
            List<SectorCommonBean> mSectors = new ArrayList<>();
            List<CellCommonBean> mCells = new ArrayList<>();
            List<StreetCommonBean> mStreets = new ArrayList<>();
            
            DistrictCommonBean districtBean = new DistrictCommonBean();
            SectorCommonBean sectorBean = new SectorCommonBean();
            CellCommonBean cellBean = new CellCommonBean();
            StreetCommonBean streetBean;
            ProvinceCommonBean provinceBean = new ProvinceCommonBean();
            
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
                                streetBean = new StreetCommonBean(cell.getId()+"",
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
                        cellBean = new CellCommonBean();
                    }
                    
                    sectorBean.setCells(mCells);
                    mSectors.add(sectorBean);
                    mCells= new ArrayList<>();
                    sectorBean = new SectorCommonBean();
                }
                
                districtBean.setSectors(mSectors);
                mDistricts.add(districtBean);
                mSectors = new ArrayList<>();
                districtBean = new DistrictCommonBean();
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
            List<SectorCommonBean> mSectors = new ArrayList<>();
            List<CellCommonBean> mCells = new ArrayList<>();
            List<StreetCommonBean> mStreets = new ArrayList<>();
            DistrictCommonBean districtBean = new DistrictCommonBean();
            SectorCommonBean sectorBean = new SectorCommonBean();
            CellCommonBean cellBean = new CellCommonBean();
            StreetCommonBean streetBean;
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
                            streetBean = new StreetCommonBean(cell.getId()+"",
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
                    cellBean = new CellCommonBean();
                }
                sectorBean.setCells(mCells);
                mSectors.add(sectorBean);
                mCells= new ArrayList<>();
                sectorBean = new SectorCommonBean();
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
            List<SectorCommonBean> mSectors = new ArrayList<>();
            List<CellCommonBean> mCells = new ArrayList<>();
            List<StreetCommonBean> mStreets = new ArrayList<>();
            DistrictCommonBean districtBean = new DistrictCommonBean();
            SectorCommonBean sectorBean = new SectorCommonBean();
            CellCommonBean cellBean = new CellCommonBean();
            StreetCommonBean streetBean;
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
                            streetBean = new StreetCommonBean(cell.getId()+"",
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
                    cellBean = new CellCommonBean();
                }
                sectorBean.setCells(mCells);
                mSectors.add(sectorBean);
                mCells= new ArrayList<>();
                sectorBean = new SectorCommonBean();
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
            List<SectorCommonBean> mSectors = new ArrayList<>();
            List<CellCommonBean> mCells = new ArrayList<>();
            List<StreetCommonBean> mStreets = new ArrayList<>();
            SectorCommonBean sectorBean = new SectorCommonBean();
            CellCommonBean cellBean = new CellCommonBean();
            StreetCommonBean streetBean;
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
                            streetBean = new StreetCommonBean(cell.getId()+"",
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
                    cellBean = new CellCommonBean();
                }
                sectorBean.setCells(mCells);
                mSectors.add(sectorBean);
                mCells= new ArrayList<>();
                sectorBean = new SectorCommonBean();
            }
            return RequestResponse.success(DataFactory.objectToString(mSectors));
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+" AppProcessor getProvinceByName failed due to: "+e.getMessage());
            return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, e.getLocalizedMessage());
        }
    }
    
    public Response getSectorById(String body){
        try{
            List<CellCommonBean> mCells = new ArrayList<>();
            List<StreetCommonBean> mStreets = new ArrayList<>();
            SectorCommonBean sectorBean = new SectorCommonBean();
            CellCommonBean cellBean = new CellCommonBean();
            StreetCommonBean streetBean;
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
                        streetBean = new StreetCommonBean(
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
                cellBean = new CellCommonBean();
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
            List<CellCommonBean> mCells = new ArrayList<>();
            List<StreetCommonBean> mStreets = new ArrayList<>();
            CellCommonBean cellBean = new CellCommonBean();
            StreetCommonBean streetBean;
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
                        streetBean = new StreetCommonBean(
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
                cellBean = new CellCommonBean();
            }
            return RequestResponse.success(DataFactory.objectToString(mCells));
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+" AppProcessor getProvinceByName failed due to: "+e.getMessage());
            return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, e.getLocalizedMessage());
        }
    }
    
    public Response getCellById(String body){
        try{
            List<StreetCommonBean> mStreets = new ArrayList<>();
            CellCommonBean cellBean = new CellCommonBean();
            StreetCommonBean streetBean;
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
                    streetBean = new StreetCommonBean(
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
    
    //SPECS
    public Response getAllDataSpecs(){
        try{
            List<ProvinceBean> mProvince = new ArrayList<>();
            
            List<Province> provinces = getAllProvince();
            if(provinces == null){
                out.print(AppDesc.APP_DESC+" AppProcessor getAllDataSpecs failed due to: empty province results");
                return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, "Province not found.");
            }
            
            for(Province province : provinces){
                mProvince.add(new ProvinceBean(province.getId()+"", province.getName()));
            }
            return RequestResponse.success(DataFactory.objectToString(mProvince));
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+" AppProcessor getAllDataSpecs failed due to: "+e.getMessage());
            return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, e.getLocalizedMessage());
        }
    }
    
    public Response getProvinceByNameSpecs(String body){
        try{
            List<ProvinceBean> mProvince = new ArrayList<>();
            
            Province province = getProvinceName(body);
            if(province == null){
                out.print(AppDesc.APP_DESC+" AppProcessor getProvinceByNameSpecs failed due to: empty province results");
                return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, "Province not found.");
            }
            
            
            return RequestResponse.success(DataFactory.objectToString(new ProvinceBean(province.getId()+"", province.getName())));
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+" AppProcessor getProvinceByNameSpecs failed due to: "+e.getMessage());
            return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, e.getLocalizedMessage());
        }
    }
    
    public Response getProvinceByIdSpecs(String body){
        try{
            Province province = getProvinceId(stringToLong(body));
            if(province == null){
                out.print(AppDesc.APP_DESC+" AppProcessor getProvinceByIdSpecs failed due to: empty province results");
                return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, "Province not found.");
            }
            
            
            return RequestResponse.success(DataFactory.objectToString(new ProvinceBean(province.getId()+"", province.getName())));
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+" AppProcessor getProvinceByIdSpecs failed due to: "+e.getMessage());
            return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, e.getLocalizedMessage());
        }
    }
    
    public Response getDistrictByNameSpecs(String body){
        try{
            
            District district = getDistrictName(body);
            if(district == null){
                out.print(AppDesc.APP_DESC+" AppProcessor getAllData failed due to: empty district results");
                return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, "District not found.");
            }
            
            return RequestResponse.success(DataFactory.objectToString(new DistrictBean(district.getProvinceId()+"", district.getId()+"", district.getName())));
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+" AppProcessor getProvinceByName failed due to: "+e.getMessage());
            return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, e.getLocalizedMessage());
        }
    }
    
    public Response getDistrictByIdSpecs(String body){
        try{
            
            District district = getDistrictId(stringToLong(body));
            if(district == null){
                out.print(AppDesc.APP_DESC+" AppProcessor getAllData failed due to: empty district results");
                return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, "District not found.");
            }
            
            return RequestResponse.success(DataFactory.objectToString(new DistrictBean(district.getProvinceId()+"", district.getId()+"", district.getName())));
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+" AppProcessor getProvinceByName failed due to: "+e.getMessage());
            return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, e.getLocalizedMessage());
        }
    }
    
    public Response getSectorByNameSpecs(String body){
        try{
            List<SectorBean> mSectors = new ArrayList<>();
            
            List<Sector> sectors = getSectorName(body);
            if(sectors == null){
                out.print(AppDesc.APP_DESC+" AppProcessor getSectorByNameSpecs failed due to: empty sector results");
                return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, "Sector not found.");
            }
            for(Sector sector : sectors){
                mSectors.add(new SectorBean(sector.getId()+"", sector.getId()+"", sector.getName()));
            }
            return RequestResponse.success(DataFactory.objectToString(mSectors));
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+" AppProcessor getSectorByNameSpecs failed due to: "+e.getMessage());
            return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, e.getLocalizedMessage());
        }
    }
    
    public Response getSectorByIdSpecs(String body){
        try{
            
            Sector sector = getSectorId(stringToLong(body));
            if(sector == null){
                out.print(AppDesc.APP_DESC+" AppProcessor getSectorByIdSpecs failed due to: empty sector results");
                return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, "Sector not found.");
            }
            return RequestResponse.success(DataFactory.objectToString(new SectorBean(sector.getId()+"", sector.getId()+"", sector.getName())));
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+" AppProcessor getSectorByIdSpecs failed due to: "+e.getMessage());
            return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, e.getLocalizedMessage());
        }
    }
    
    public Response getCellByNameSpecs(String body){
        try{
            List<CellBean> mCells = new ArrayList<>();
            List<Cell> cells = getCellName(body);
            if(cells == null){
                out.print(AppDesc.APP_DESC+" AppProcessor getCellByNameSpecs failed due to: empty cell results");
                return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, "Cells not found.");
            }
            for(Cell cell : cells){
                mCells.add(new CellBean(cell.getSectorId()+"", cell.getId()+"", cell.getName()));
            }
            return RequestResponse.success(DataFactory.objectToString(mCells));
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+" AppProcessor getCellByNameSpecs failed due to: "+e.getMessage());
            return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, e.getLocalizedMessage());
        }
    }
    
    public Response getCellByIdSpecs(String body){
        try{
            Cell cell = getCellId(stringToLong(body));
            if(cell == null){
                out.print(AppDesc.APP_DESC+" AppProcessor getAllData failed due to: empty cell results");
                return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, "Cell not found.");
            }
            
            return RequestResponse.success(DataFactory.objectToString(new CellBean(cell.getSectorId()+"", cell.getId()+"", cell.getName())));
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+" AppProcessor getProvinceByName failed due to: "+e.getMessage());
            return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, e.getLocalizedMessage());
        }
    }
    
    public Response getProvinceDistricts(String body){
        try{
            Province province = getProvinceId(stringToLong(body));
            if(province == null){
                out.print(AppDesc.APP_DESC+" AppProcessor getProvinceDistricts failed due to: empty province results");
                return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, "Province not found.");
            }
            List<District> mDistrict = districtFacade.getDistrictByProvince(province.getId());
            if(mDistrict == null){
                out.print(AppDesc.APP_DESC+" AppProcessor getProvinceDistricts failed due to: empty Districts results");
                return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, "Districts not found.");
            }
            List<DistrictBean> districtBeans = new ArrayList<>();
            for(District district : mDistrict){
                districtBeans.add(new DistrictBean(district.getProvinceId()+"", district.getId()+"", district.getName()));
            }
            return RequestResponse.success(DataFactory.objectToString(districtBeans));
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+" AppProcessor getProvinceDistricts failed due to: "+e.getMessage());
            return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, e.getLocalizedMessage());
        }
    }
    public Response getDistrictSectors(String body){
        try{
            District district = getDistrictId(stringToLong(body));
            if(district == null){
                out.print(AppDesc.APP_DESC+" AppProcessor getDistrictSectors failed due to: empty District results");
                return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, "District not found.");
            }
            List<Sector> mSector = sectorFacade.getSectorByDistrict(district.getId());
            if(mSector == null){
                out.print(AppDesc.APP_DESC+" AppProcessor getDistrictSectors failed due to: empty Sectors results");
                return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, "Sectors not found.");
            }
            List<SectorBean> sectorBeans = new ArrayList<>();
            for(Sector sector : mSector){
                sectorBeans.add(new SectorBean(sector.getDistrictId()+"", sector.getId()+"", sector.getName()));
            }
            return RequestResponse.success(DataFactory.objectToString(sectorBeans));
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+" AppProcessor getDistrictSectors failed due to: "+e.getMessage());
            return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, e.getLocalizedMessage());
        }
    }
    public Response getSectorCells(String body){
        try{
            Sector sector = getSectorId(stringToLong(body));
            if(sector == null){
                out.print(AppDesc.APP_DESC+" AppProcessor getGetSectorCells failed due to: empty sector results");
                return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, "Sector not found.");
            }
            List<Cell> mCell = cellFacade.getListCellBySector(sector.getId());
            if(mCell == null){
                out.print(AppDesc.APP_DESC+" AppProcessor getGetSectorCells failed due to: empty cell results");
                return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, "Sectors not found.");
            }
            List<CellBean> cellBeans = new ArrayList<>();
            for(Cell cell : mCell){
                cellBeans.add(new CellBean(cell.getSectorId()+"", cell.getId()+"", cell.getName()));
            }
            return RequestResponse.success(DataFactory.objectToString(cellBeans));
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+" AppProcessor getGetSectorCells failed due to: "+e.getMessage());
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
