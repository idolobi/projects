package com.es.samples;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {
	@RequestMapping(value="/index.z")
	public String index(ModelMap model) {
		model.addAttribute("title", "Examples");
		model.addAttribute("subTitle", "제목");
		return "index";
	}
}
