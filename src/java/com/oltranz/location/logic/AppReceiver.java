/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.oltranz.location.logic;

import com.oltranz.location.config.AppDesc;
import com.oltranz.location.config.CommandConfig;
import com.oltranz.location.config.HeaderConfig;
import com.oltranz.location.utilities.CommandValidator;
import com.oltranz.location.utilities.RequestResponse;
import static java.lang.System.out;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

/**
 *
 * @author ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr /
 * aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662
 */
@Stateless
public class AppReceiver {
    @EJB
            AppProcessor appProcessor;
    public Response receiver(String remoteAddress, int remotePort, HttpHeaders headers, String body){
        if(headers == null){
            out.print(AppDesc.APP_DESC+" AppReceiver receiver failed due to: null headers");
            return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, "empty headers");
        }
        if(headers.getHeaderString(HeaderConfig.CMD) == null){
            out.print(AppDesc.APP_DESC+" AppReceiver receiver failed due to: null headers");
            return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, "empty command, in header field CMD");
        }
        String cmd = headers.getHeaderString(HeaderConfig.CMD);
//        if(!cmd.equals(CommandConfig.GET_ALL)&& !cmd.equals(CommandConfig.GET_ALL_SPECS) && headers.getHeaderString(HeaderConfig.VALUE) == null){
//            out.print(AppDesc.APP_DESC+" AppReceiver receiver failed due to: null headers");
//            return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, "empty value, in header field VALUE");
//        }
String value;
if(headers.getHeaderString(HeaderConfig.VALUE) == null)
    value = "";
else
    value = headers.getHeaderString(HeaderConfig.VALUE);

if(!CommandValidator.validate(cmd)){
    out.print(AppDesc.APP_DESC+" AppReceiver receiver failed due to: invalid command: "+cmd);
    return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, "Invalid command: "+cmd);
}
return dispatchQuery(cmd, value);
    }
    
    private Response dispatchQuery(String cmd, String body){
        switch(cmd){
            case CommandConfig.GET_ALL:
                return appProcessor.getAllData();
            case CommandConfig.GET_PROVINCE_BY_NAME:
                return appProcessor.getProvinceByName(body);
            case CommandConfig.GET_PROVINCE_BY_ID:
                return appProcessor.getProvinceById(body);
            case CommandConfig.GET_DISTRICT_BY_NAME:
                return appProcessor.getDistrictByName(body);
            case CommandConfig.GET_DISTRICT_BY_ID:
                return appProcessor.getDistrictById(body);
            case CommandConfig.GET_SECTOR_BY_NAME:
                return appProcessor.getSectorByName(body);
            case CommandConfig.GET_SECTOR_BY_ID:
                return appProcessor.getSectorById(body);
            case CommandConfig.GET_CELL_BY_NAME:
                return appProcessor.getCellByName(body);
            case CommandConfig.GET_CELL_BY_ID:
                return appProcessor.getCellById(body);
                //specs
            case CommandConfig.GET_ALL_SPECS:
                return appProcessor.getAllDataSpecs();
            case CommandConfig.GET_PROVINCE_BY_NAME_SPECS:
                return appProcessor.getProvinceByNameSpecs(body);
            case CommandConfig.GET_PROVINCE_BY_ID_SPECS:
                return appProcessor.getProvinceByIdSpecs(body);
            case CommandConfig.GET_DISTRICT_BY_NAME_SPECS:
                return appProcessor.getDistrictByNameSpecs(body);
            case CommandConfig.GET_DISTRICT_BY_ID_SPECS:
                return appProcessor.getDistrictByIdSpecs(body);
            case CommandConfig.GET_SECTOR_BY_NAME_SPECS:
                return appProcessor.getSectorByNameSpecs(body);
            case CommandConfig.GET_SECTOR_BY_ID_SPECS:
                return appProcessor.getSectorByIdSpecs(body);
            case CommandConfig.GET_CELL_BY_NAME_SPECS:
                return appProcessor.getCellByNameSpecs(body);
            case CommandConfig.GET_CELL_BY_ID_SPECS:
                return appProcessor.getCellByIdSpecs(body);
                
                //specs
            case CommandConfig.GET_PROVINCE_DISTRICTS:
                return appProcessor.getProvinceDistricts(body);
            case CommandConfig.GET_DISTRICT_SECTORS:
                return appProcessor.getDistrictSectors(body);
            case CommandConfig.GET_SECTOR_CELLS:
                return appProcessor.getSectorCells(body);
            default:
                out.print(AppDesc.APP_DESC+" AppReceiver receiver failed due to: ambigious CMD: "+cmd+" and body(value): "+body);
                return RequestResponse.faillure(Response.Status.EXPECTATION_FAILED, "Ambigious input cmd: "+cmd+" and value: "+body);
        }
    }
}
