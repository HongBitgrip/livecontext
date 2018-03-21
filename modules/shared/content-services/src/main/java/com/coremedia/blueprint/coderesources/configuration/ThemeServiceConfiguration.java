package com.coremedia.blueprint.coderesources.configuration;

import com.coremedia.blueprint.base.tree.TreeRelation;
import com.coremedia.blueprint.coderesources.ThemeService;
import com.coremedia.cap.content.Content;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import javax.annotation.Resource;

@Configuration
@ImportResource(
        value = {
                "classpath:/com/coremedia/blueprint/base/tree/bpbase-treerelation-services.xml"
        },
        reader = ResourceAwareXmlBeanDefinitionReader.class)
public class ThemeServiceConfiguration {
  private TreeRelation<Content> treeRelation;

  @Resource(name="navigationTreeRelation")
  public void setTreeRelation(TreeRelation<Content> treeRelation) {
    this.treeRelation = treeRelation;
  }

  @Bean(name="themeService")
  public ThemeService themeService() {
    return new ThemeService(treeRelation);
  }

}
