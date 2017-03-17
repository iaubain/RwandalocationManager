/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.oltranz.location.utilities;

import com.oltranz.location.config.CommandConfig;

/**
 *
 * @author ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr /
 * aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662
 */
public class CommandValidator {
    public static final boolean validate(String command){
        switch(command){
            case CommandConfig.GET_ALL:
                return true;
            case CommandConfig.GET_PROVINCE_BY_NAME:
                return true;
            case CommandConfig.GET_PROVINCE_BY_ID:
                return true;
            case CommandConfig.GET_DISTRICT_BY_NAME:
                return true;
            case CommandConfig.GET_DISTRICT_BY_ID:
                return true;
            case CommandConfig.GET_SECTOR_BY_NAME:
                return true;
            case CommandConfig.GET_SECTOR_BY_ID:
                return true;
            case CommandConfig.GET_CELL_BY_NAME:
                return true;
            case CommandConfig.GET_CELL_BY_ID:
                return true;
            default:
                return false;                
        }
    }
}
