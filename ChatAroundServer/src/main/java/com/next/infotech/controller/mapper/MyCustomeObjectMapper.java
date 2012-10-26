package com.next.infotech.controller.mapper;

import java.text.SimpleDateFormat;

import javax.annotation.PostConstruct;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

public class MyCustomeObjectMapper extends ObjectMapper {

	public static final String DATE_FORMAT = "dd-MMM-yyyy KK:mm:ss";
	/*
	public MyCustomeObjectMapper(){
		System.out.println("&&&&&&&&&&&&&&&&&&&&&& MyCustomeObjectMapper - Created ");
		CustomSerializerFactory sf = new CustomSerializerFactory();
		sf.addSpecificMapping(Date.class, new DateSerializer());
		this.setSerializerFactory(sf);
	}
	*/
	
	@PostConstruct
    public void afterPropertiesSet() throws Exception {
        SerializationConfig serialConfig = getSerializationConfig().withDateFormat(new SimpleDateFormat(DATE_FORMAT));
        serialConfig = serialConfig.withSerializationInclusion(Inclusion.NON_NULL);
        this.setSerializationConfig(serialConfig);
        
    }

}
