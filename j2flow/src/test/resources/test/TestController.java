package com.nthalk.j2flow.test;

import com.nthalk.j2flow.api.J2FlowConfiguration;
import com.nthalk.j2flow.api.J2FlowEndpoint;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.cors.CorsConfiguration;

@J2FlowConfiguration(root = "target/test-classes/test/")
@J2FlowEndpoint
@Controller
@RequestMapping("/test/{baseVariable}")
public class TestController {

  @PostMapping("/post/{pathVariable}")
  @ResponseBody
  public CorsConfiguration post(
      @PathVariable String baseVariable,
      @RequestParam String param,
      @PathVariable String pathVariable,
      @RequestBody TestRequestModel request
  ) {
    return null;
  }

  @PostMapping("/post/{pathVariable}")
  @ResponseBody
  public CorsConfiguration post(
      @RequestParam String param,
      @PathVariable String pathVariable,
      @RequestBody TestRequestModel request
  ) {
    return null;
  }


  public static class TestModel {

    private String testResponse;

    public String getTestResponse() {
      return testResponse;
    }

    public TestModel setTestResponse(String testResponse) {
      this.testResponse = testResponse;
      return this;
    }
  }

  public static class TestRequestModel {

    private Boolean testRequest;

    public Boolean getTestRequest() {
      return testRequest;
    }

    public TestRequestModel setTestRequest(Boolean testRequest) {
      this.testRequest = testRequest;
      return this;
    }
  }
}
