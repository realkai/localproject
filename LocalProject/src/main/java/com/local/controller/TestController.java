package com.local.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.local.dao.DataDao;

@Controller
public class TestController {
	
	@Autowired
	private DataDao test;
	
	@RequestMapping("/test")
	public String index() {
		test.insert();
		return "index";
	}
	
	@RequestMapping("/test1")
	public String batch() {
		test.batchinsert();
		return "index";
	}
	
	@RequestMapping("/test2")
	public String index2() {
		test.batchinsert2();
		return "index";
	}

}
