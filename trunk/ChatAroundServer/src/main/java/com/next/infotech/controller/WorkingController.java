package com.next.infotech.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.next.core.exception.AppException;

@Controller
public class WorkingController {

	@RequestMapping(value="/api/2.0/working", method = RequestMethod.GET)
    @ResponseBody
	public String working() throws AppException{
		System.out.println("Working");
		return "Working";
	}
}
