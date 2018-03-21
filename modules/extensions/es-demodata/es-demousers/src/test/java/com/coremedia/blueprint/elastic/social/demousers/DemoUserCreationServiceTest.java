package com.coremedia.blueprint.elastic.social.demousers;

import com.coremedia.elastic.core.api.blobs.Blob;
import com.coremedia.elastic.core.api.blobs.BlobService;
import com.coremedia.elastic.core.api.tenant.TenantService;
import com.coremedia.elastic.social.api.users.CommunityUser;
import com.coremedia.elastic.social.api.users.CommunityUserService;
import com.coremedia.mimetype.MimeTypeService;
import com.coremedia.mimetype.TikaMimeTypeService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.io.InputStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/com/coremedia/blueprint/elastic/social/demousers/DemoUserCreationServiceTest-context.xml"})
@Configuration
@PropertySource(name = "test", value = {
        "classpath:/com/coremedia/blueprint/elastic/social/demousers/es-demousers-test.properties"
})
public class DemoUserCreationServiceTest {

  private static final CommunityUserService communityUserService = mock(CommunityUserService.class);
  private static final BlobService blobService = mock(BlobService.class);

  @Inject
  private TenantService tenantService;

  @After
  public void teardown() {
    tenantService.clearCurrent();
    Mockito.reset(communityUserService, blobService);
  }

  @Before
  public void setup(){
    when(communityUserService.createUser(anyString(), anyString(), anyString())).thenReturn(mock(CommunityUser.class));
    when(blobService.put(any(InputStream.class), anyString(), anyString())).thenReturn(mock(Blob.class));
  }

  @Test
  public void testTenantSpecific() throws Exception {
    tenantService.register("test");
    tenantService.setCurrent("test");

    verify(communityUserService, times(3)).createUser(anyString(), anyString(), anyString());
    verify(blobService, times(2)).put(any(InputStream.class), anyString(), anyString());
  }

  @Test
  public void testArbitraryTenant() throws Exception {
    tenantService.register("anothertest");
    tenantService.setCurrent("anothertest");

    verify(communityUserService, times(2)).createUser(anyString(), anyString(), anyString());
    verify(blobService, times(2)).put(any(InputStream.class), anyString(), anyString());
  }

  @Bean
  public CommunityUserService communityUserService(){
    return communityUserService;
  }

  @Bean
  public BlobService blobService(){
    return blobService;
  }

  @Bean
  public MimeTypeService mimeTypeService() {
    TikaMimeTypeService mimeTypeService = new TikaMimeTypeService();
    mimeTypeService.init();
    return mimeTypeService;
  }

}
