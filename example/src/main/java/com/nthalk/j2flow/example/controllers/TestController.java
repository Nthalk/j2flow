package com.nthalk.j2flow.example.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping
public class TestController {

  @ResponseBody
  public String post() {
    return null;
  }

}
