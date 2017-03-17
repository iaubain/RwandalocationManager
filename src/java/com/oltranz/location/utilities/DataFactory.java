/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oltranz.location.utilities;

import com.oltranz.location.config.AppDesc;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import static java.lang.System.out;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

/**
 *
 * @author Hp
 */
public class DataFactory {
    
    public DataFactory() {
    }
    
    public static final String objectToString(Object object){
        ObjectMapper mapper= new ObjectMapper();

        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationConfig.Feature.WRITE_NULL_MAP_VALUES, false)
                .configure(SerializationConfig.Feature.WRITE_EMPTY_JSON_ARRAYS, false)
                .configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
        try {
            out.println(AppDesc.APP_DESC+" Mapping object: "+object.getClass().getName());
            String jsonData=mapper.writeValueAsString(object);
            return jsonData;
        } catch (IOException e) {
            out.println(AppDesc.APP_DESC+" Failed to map object: "+object.getClass().getName()+"  due to Error: "+e.getLocalizedMessage());
        }
        return null;
    }
    
    public static final Object stringToObject(Class className, String jsonString){
        ObjectMapper mapper= new ObjectMapper();

            mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            try {
                out.println(AppDesc.APP_DESC+" Mapping string: "+ jsonString);
                Object result=mapper.readValue(jsonString,className);
                if(result != null)
                    out.println(AppDesc.APP_DESC+" Mapping string Completed succesfully: "+ jsonString);
                else
                    out.println(AppDesc.APP_DESC+" Mapping string Completed with null result: "+ jsonString);
                return result;
            } catch (IOException e) {
                out.println(AppDesc.APP_DESC+" Failed mapping string: "+ jsonString+"  due to Error: "+e.getLocalizedMessage());
            }
        return null;
    }
    
    public static final Object xmlStringToObject(Class className, String xmlString){
        try{
            out.println(AppDesc.APP_DESC+" Mapping string: "+ xmlString);
            XMLInputFactory xmlFactory = XMLInputFactory.newFactory();
            StreamSource streamSource = new StreamSource(new StringReader(xmlString));
            XMLStreamReader streamReader= xmlFactory.createXMLStreamReader(streamSource);
            
            JAXBContext jc = JAXBContext.newInstance(className);
            Unmarshaller unMarshaller = jc.createUnmarshaller();
            Object object= unMarshaller.unmarshal(streamReader);
            
            if(object != null)
                    out.println(AppDesc.APP_DESC+" Mapping string Completed succesfully: "+ xmlString);
                else
                    out.println(AppDesc.APP_DESC+" Mapping string Completed with null result: "+ xmlString);
                
            return object; 
        }catch(JAXBException | XMLStreamException e){
            out.print(AppDesc.APP_DESC+"Error due to "+e.getLocalizedMessage());
            return null;
        }
    }
    
    public static final String objectToXmlString(Object object){
        try{
            out.println(AppDesc.APP_DESC+" Convertion of Object to XML String is starting");
            JAXBContext ctx = JAXBContext.newInstance(object.getClass());
            Marshaller msh = ctx.createMarshaller();
            msh.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            StringWriter writer = new StringWriter();
            msh.marshal(object, writer);
            String result = writer.toString();
            out.println(AppDesc.APP_DESC+" Mapping string Completed succesfully with: "+ result);
            return result;      
        }catch(JAXBException e){
            out.print(AppDesc.APP_DESC+" Error due to "+e.getLocalizedMessage());
            return null;
        }
    }
    
    public static final String streamToString(InputStream inputStream){
        try {
            StringBuilder sb=new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String read;
            
            while((read=br.readLine()) != null) {
                out.println(AppDesc.APP_DESC+" decoding stream "+read);
                sb.append(read);
            }
            
            br.close();
            
            String result = sb.toString();
            out.print(AppDesc.APP_DESC+"Decoded the stream "+result);
            return result;
        } catch (IOException e) {
            out.print(AppDesc.APP_DESC+"Error converting stream due to: "+ e.getLocalizedMessage());
            return null;
        }
    }
    
}
