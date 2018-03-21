package com.coremedia.blueprint.elastic.social.demodata;

import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.cap.test.xmlrepo.XmlUapiConfig;
import com.coremedia.springframework.component.ComponentLoaderInitializer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.servlet.DispatcherServlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {
        EsDemodataGeneratorWebappIT.DemoDataGeneratorConfiguration.class,
        XmlRepoConfiguration.class
}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, value = {
        "components.disabled=es-p13n-cae", // this extension doesn't go with core-memory
        "tenant.default=media",
        "models.createIndexes=false",
        "elastic.solr.lazyIndexCreation=true"
})
@ContextConfiguration(initializers = ComponentLoaderInitializer.class)
@ActiveProfiles("disableDataViews")
public class EsDemodataGeneratorWebappIT {

  @Autowired
  private DemoDataGenerator demoDataGenerator;

  @Value("http://localhost:${local.server.port}/generate?{param}")
  String url;

  @Before
  public void testDemoDataGeneratorSetUp() throws Exception {
    assertEquals(DemoDataGenerator.STATE_STOPPED, demoDataGenerator.getStatus());
    assertEquals("invalid number of users", 0, demoDataGenerator.getUserCount());
  }

  @After
  public void testDemoDataGeneratorTearDown() throws Exception {
    assertEquals(DemoDataGenerator.STATE_STOPPED, demoDataGenerator.getStatus());
    assertTrue("invalid number of users", demoDataGenerator.getUserCount() > 0);
  }

  @Test
  public void doIt() throws InterruptedException {
    HttpStatus httpStatus = new TestRestTemplate().getForEntity(url, String.class, "start").getStatusCode();
    assertEquals(url + " start", 200, httpStatus.value());

    assertEquals(DemoDataGenerator.STATE_RUNNING, demoDataGenerator.getStatus());

    httpStatus = new TestRestTemplate().getForEntity(url, String.class, "status").getStatusCode();
    assertEquals(url + "status", 200, httpStatus.value());

    httpStatus = new TestRestTemplate().getForEntity(url, String.class, "stop").getStatusCode();
    assertEquals(url + "stop", 200, httpStatus.value());
  }

  @Configuration
  static class DemoDataGeneratorConfiguration {

    @Bean
    EmbeddedServletContainerFactory tomcat() {
      return new TomcatEmbeddedServletContainerFactory();
    }

    @Bean
    DispatcherServlet dispatcherServlet() {
      return new DispatcherServlet();
    }

    @Bean
    XmlUapiConfig xmlUapiConfig() {
      return new XmlUapiConfig("classpath:/com/coremedia/testing/contenttest.xml");
    }

  }
}
