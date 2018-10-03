package com.nthalk.j2flow;

import com.google.auto.service.AutoService;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.loader.ClasspathLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import com.nthalk.j2flow.api.J2FlowConfiguration;
import com.nthalk.j2flow.api.J2FlowEndpoint;
import com.nthalk.j2flow.service.Service;
import com.nthalk.j2flow.service.ServiceMethod;
import com.nthalk.j2flow.service.ServiceParameter;
import com.nthalk.j2flow.service.ServiceParameter.Type;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@AutoService(FlowEndpointProcessor.class)
public class FlowEndpointProcessor extends AbstractProcessor {

  private final Map<String, Element> classCache = new HashMap<>();
  private final List<Service> services = new ArrayList<>();
  private final PebbleEngine templateEngine;
  private GenerationConfiguration configuration = new GenerationConfiguration();

  public FlowEndpointProcessor() {
    ClasspathLoader templateLoader = new ClasspathLoader();
    templateLoader.setPrefix("com/nthalk/j2flow/templates/");
    templateLoader.setSuffix(".peb");
    templateEngine = new PebbleEngine.Builder()
        .loader(templateLoader)
        .build();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    for (Element element : roundEnv.getRootElements()) {
      indexElement(element);
    }

    for (Element element : roundEnv.getElementsAnnotatedWith(J2FlowConfiguration.class)) {
      J2FlowConfiguration conf = element.getAnnotation(J2FlowConfiguration.class);
      configuration
          .setRoot(conf.root())
          .setEnumerateOverloadedServiceMethods(conf.enumerateOverloadedServiceMethods())
          .setBuiltinConvertedTypes(conf.builtinConvertedTypes())
          .setConvertedTypes(conf.convertedTypes());
      break;
    }

    for (Element element : roundEnv.getElementsAnnotatedWith(J2FlowEndpoint.class)) {
      Service service = new Service();
      service.setName(element.getSimpleName().toString());
      RequestMapping serviceRequestMapping = element.getAnnotation(RequestMapping.class);
      if (serviceRequestMapping == null) {
        throw new RuntimeException(
            "Cannot map a @J2FLowEndpoint to class without a @RequestMapping: " + service
                .getName());
      }

      if (serviceRequestMapping.path().length > 0) {
        service.setPath(serviceRequestMapping.path()[0]);
      } else if (serviceRequestMapping.value().length > 0) {
        service.setPath(serviceRequestMapping.value()[0]);
      } else {
        service.setPath("");
      }

      for (Element enclosedElement : element.getEnclosedElements()) {
        if (enclosedElement instanceof ExecutableElement) {
          ExecutableElement method = (ExecutableElement) enclosedElement;
          ServiceMethod serviceMethod = getServiceMethod(service, method);
          if (serviceMethod == null) {
            continue;
          }
          for (VariableElement parameter : method.getParameters()) {
            addParameter(parameter, serviceMethod);
          }
          serviceMethod.setReturnType(method.getReturnType().toString());
          service.add(serviceMethod);
        }
      }

      services.add(service);
    }

    if (services.isEmpty()) {
      return true;
    }

    // Ensure the root dir exists
    String root = configuration.getRoot();
    File rootDir = new File(root);
    rootDir.mkdirs();

    Set<String> types = new HashSet<>();

    // Output configured client
    PebbleTemplate clientJs = templateEngine.getTemplate("client.js");
    try (FileWriter serviceWriter = new FileWriter(new File(rootDir, "client.js"));) {
      clientJs.evaluate(serviceWriter, new HashMap<String, Object>() {{
        put("configuration", configuration);
      }});
    } catch (IOException e) {
      throw new RuntimeException("Could not write service file", e);
    }

    // Output all service files
    PebbleTemplate serviceJs = templateEngine.getTemplate("service.js");
    for (Service service : services) {
      for (ServiceMethod method : service.getMethods()) {
        types.add(method.getReturnType());
        if (method.getBodyParameter() != null) {
          types.add(method.getBodyParameter().getType());
        }
        for (ServiceParameter parameter : method.getParameters()) {
          types.add(parameter.getType());
        }
      }

      String serviceName = service.getName();
      try (FileWriter serviceWriter = new FileWriter(new File(rootDir, serviceName + ".js"));) {
        serviceJs.evaluate(serviceWriter, new HashMap<String, Object>() {{
          put("service", service);
        }});
      } catch (IOException e) {
        throw new RuntimeException("Could not write service file", e);
      }
    }

    // Output the index containing all services
    PebbleTemplate indexJs = templateEngine.getTemplate("index.js");
    try (FileWriter serviceWriter = new FileWriter(new File(rootDir, "index.js"));) {
      indexJs.evaluate(serviceWriter, new HashMap<String, Object>() {{
        put("services", services);
      }});
    } catch (IOException e) {
      throw new RuntimeException("Could not write service file", e);
    }

    // Output the types file
    PebbleTemplate typesJs = templateEngine.getTemplate("types.js");
    for (String type : types) {
      System.out.println(type);
    }

    return true;
  }

  private void indexElement(Element element) {
    if (element.getKind() != ElementKind.CLASS) {
      return;
    }
    if (!element.getModifiers().contains(Modifier.PUBLIC)) {
      return;
    }
    classCache.put(element.toString(), element);
    for (Element enclosedElement : element.getEnclosedElements()) {
      indexElement(enclosedElement);
    }
  }

  private void addParameter(VariableElement parameter, ServiceMethod method) {
    RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
    if (requestParam != null) {
      ServiceParameter methodParameter = new ServiceParameter(configuration,
          requestParam.required(), parameter, Type.QUERY);
      method.add(methodParameter);
      return;
    }

    RequestHeader requestHeader = parameter.getAnnotation(RequestHeader.class);
    if (requestHeader != null) {
      ServiceParameter methodParameter = new ServiceParameter(configuration,
          requestHeader.required(), parameter, Type.HEADER);
      method.add(methodParameter);
      return;
    }

    PathVariable pathVariable = parameter.getAnnotation(PathVariable.class);
    if (pathVariable != null) {
      ServiceParameter methodParameter = new ServiceParameter(configuration,
          pathVariable.required(), parameter, Type.PATH);
      method.add(methodParameter);
      return;
    }

    RequestBody requestBody = parameter.getAnnotation(RequestBody.class);
    if (requestBody != null) {
      ServiceParameter methodParameter = new ServiceParameter(
          configuration, requestBody.required(),
          parameter, Type.BODY);
      method.setBodyParameter(methodParameter);
      return;
    }
  }

  private ServiceMethod getServiceMethod(Service service,
      Element enclosedElement) {
    PostMapping postMapping = enclosedElement.getAnnotation(PostMapping.class);
    if (postMapping != null) {
      ServiceMethod methodMapping = new ServiceMethod();
      methodMapping.setName(enclosedElement.getSimpleName().toString());
      methodMapping.setMethod(RequestMethod.POST);
      methodMapping.setPath(postMapping.value(), postMapping.path());
      methodMapping.setConsumes(postMapping.consumes());
      methodMapping.setProduces(postMapping.produces());
      return methodMapping;
    }

    GetMapping getMapping = enclosedElement.getAnnotation(GetMapping.class);
    if (getMapping != null) {
      ServiceMethod methodMapping = new ServiceMethod();
      methodMapping.setName(enclosedElement.getSimpleName().toString());
      methodMapping.setMethod(RequestMethod.GET);
      methodMapping.setPath(getMapping.value(), getMapping.path());
      methodMapping.setConsumes(getMapping.consumes());
      methodMapping.setProduces(getMapping.produces());
      return methodMapping;
    }

    RequestMapping requestMapping = enclosedElement.getAnnotation(RequestMapping.class);
    if (requestMapping != null) {
      ServiceMethod methodMapping = new ServiceMethod();
      methodMapping.setName(enclosedElement.getSimpleName().toString());
      RequestMethod[] methods = requestMapping.method();
      if (methods.length == 0) {
        methodMapping.setMethod(RequestMethod.GET);
      } else {
        methodMapping.setMethod(methods[0]);
      }
      methodMapping.setPath(requestMapping.value(), requestMapping.path());
      methodMapping.setConsumes(requestMapping.consumes());
      methodMapping.setProduces(requestMapping.produces());
      return methodMapping;
    }
    return null;
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    Set<String> annotations = new HashSet<>();
    annotations.add(J2FlowEndpoint.class.getCanonicalName());
    annotations.add(J2FlowConfiguration.class.getCanonicalName());
    return annotations;
  }
}
