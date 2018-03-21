package com.coremedia.livecontext.fragment.links.transformers.resolvers;

import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.contentbeans.CMObject;
import com.coremedia.livecontext.commercebeans.CategoryInSite;
import com.coremedia.livecontext.commercebeans.ProductInSite;
import com.coremedia.livecontext.contentbeans.CMExternalPage;
import com.coremedia.livecontext.contentbeans.CMProductTeaser;
import com.coremedia.livecontext.contentbeans.LiveContextExternalProduct;
import com.coremedia.livecontext.context.LiveContextNavigation;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.fragment.links.transformers.resolvers.seo.SeoSegmentBuilder;
import com.coremedia.livecontext.fragment.resolver.ExternalReferenceResolver;
import com.coremedia.livecontext.handler.ExternalNavigationHandler;
import com.coremedia.livecontext.logictypes.CommerceLedLinkBuilderHelper;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Required;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Default LiveContext link resolver for all objects of type {@link com.coremedia.blueprint.common.contentbeans.CMObject}.
 * This fill the json object representing the link (which will be returned by the fragment call).
 */
public class CMObjectLiveContextLinkResolver extends AbstractLiveContextLinkResolver {
  //common constants to determine the template name
  private static final String KEY_OBJECT_TYPE = "objectType";
  //these are the supported object types that can be rendered as fragments on commerce site
  private static final String OBJECT_TYPE_PRODUCT = "product";
  private static final String OBJECT_TYPE_CONTENT = "content";
  private static final String OBJECT_TYPE_CATEGORY = "category";
  private static final String OBJECT_TYPE_LC_PAGE = "page";

  private static final String KEY_RENDER_TYPE = "renderType";
  private static final String RENDER_TYPE_URL = "url";

  //additional parameter values to be read from the corresponding JSP
  private static final String EXTERNAL_SEOSEGMENT_PARAMETER_NAME = "externalSeoSegment";
  private static final String EXTERNAL_URI_PATH_PARAMETER_NAME = "externalUriPath";

  //the id values of the objects
  private static final String PRODUCT_ID = "productId";
  private static final String CATEGORY_ID = "categoryId";
  private static final String CONTENT_ID = "contentId";
  private static final String LEVEL = "level";
  private static final String TOP_CATEGORY_ID = "topCategoryId";
  private static final String PARENT_CATEGORY_ID = "parentCategoryId";

  private SeoSegmentBuilder seoSegmentBuilder;

  private CommerceLedLinkBuilderHelper commerceLedLinkBuilderHelper;
  private ExternalNavigationHandler externalNavigationHandler;

  @Required
  public void setCommerceLedLinkBuilderHelper(CommerceLedLinkBuilderHelper commerceLedLinkBuilderHelper) {
    this.commerceLedLinkBuilderHelper = commerceLedLinkBuilderHelper;
  }

  @Required
  public void setSeoSegmentBuilder(SeoSegmentBuilder seoSegmentBuilder) {
    this.seoSegmentBuilder = seoSegmentBuilder;
  }

  @Required
  public void setExternalNavigationHandler(ExternalNavigationHandler externalNavigationHandler) {
    this.externalNavigationHandler = externalNavigationHandler;
  }

  @Override
  public boolean isApplicable(Object bean) {
    return bean instanceof CMObject ||
            bean instanceof ProductInSite ||
            bean instanceof Product ||
            bean instanceof CategoryInSite ||
            bean instanceof Category ||
            bean instanceof LiveContextNavigation;
  }

  /**
   * Evaluates the given bean type. Depending on the type, values are put
   * into a JSON map. The JSON is passed to the CoreMedia Content Widget and is
   * used to evaluate the corresponding JSP that is used to generate the link on commerce site.
   * Additional values, like SEO segments are already evaluated here so that the JSP can process them.
   *
   * The JSP on the commerce site will be determined by the KEY_OBJECT_TYPE and KEY_RENDER_TYPE, e.g.
   * for a ProductTeaser or a ProductInSite object the KEY_OBJECT_TYPE is "product" and the KEY_RENDER_TYPE value
   * is "url", result in the template "Product.url.jsp".
   *
   * @param bean       Bean for which URL is to be rendered
   * @param variant    Link variant
   * @param navigation Current navigation of bean for which URL is to be rendered
   * @param request    the request
   * @return The JSON object send to commerce.
   */
  @Override
  protected JSONObject resolveUrlInternal(Object bean, String variant, CMNavigation navigation, HttpServletRequest request) throws JSONException {
    JSONObject out = new JSONObject();
    out.put(KEY_RENDER_TYPE, RENDER_TYPE_URL);

    try {
      // Product
      if (bean instanceof CMProductTeaser || bean instanceof LiveContextExternalProduct || bean instanceof ProductInSite || bean instanceof Product) {
        Product product;
        // Todo: mbi better logging
        if (bean instanceof CMProductTeaser) {
          product = ((CMProductTeaser) bean).getProduct();
          if (product == null) {
            throw new IllegalArgumentException("Product cannot be retrieved (product teaser: " + ((CMProductTeaser) bean).getContent().getPath() + ")");
          }
        } else if (bean instanceof LiveContextExternalProduct) {
          product = ((LiveContextExternalProduct) bean).getProduct();
          if (product == null) {
            throw new IllegalArgumentException("Product cannot be retrieved (augmented Product: " + ((LiveContextExternalProduct) bean).getContent().getPath()  + ")");
          }
        } else if (bean instanceof ProductInSite) {
          product = ((ProductInSite) bean).getProduct();
          if (product == null) {
            throw new IllegalArgumentException("Product cannot be retrieved (in site: " + ((ProductInSite) bean).getSite() + ")");
          }
        } else {
          product = (Product) bean;
        }

        //set type and id
        out.put(KEY_OBJECT_TYPE, OBJECT_TYPE_PRODUCT);
        out.put(PRODUCT_ID, product.getExternalTechId());

        //determine the category id too, since a product can be inside different categories
        Category category = product.getCategory();
        if (category != null) {
          List<Category> breadcrumb = product.getCategory().getBreadcrumb();
          Category parentCategory = (breadcrumb.size() > 0) ? Lists.reverse(breadcrumb).get(0) : null;
          if (parentCategory != null) {
            String categoryId = parentCategory.getExternalTechId();
            out.put(CATEGORY_ID, categoryId);
          }
        }
      }
      // Category in commerce are mapped by "LiveContextNavigation" objects
      else if (bean instanceof LiveContextNavigation || bean instanceof CategoryInSite || bean instanceof Category) {
        Category category;
        if (bean instanceof LiveContextNavigation) {
          category = ((LiveContextNavigation) bean).getCategory();
        } else if (bean instanceof CategoryInSite) {
          category = ((CategoryInSite) bean).getCategory();
          if (category == null) {
            throw new IllegalArgumentException("Category cannot be retrieved (in site: " + ((CategoryInSite) bean).getSite() + ")");
          }
        } else {
          category = (Category) bean;
        }

        if (category != null) {
          int level = category.getBreadcrumb().size();
          if (level > 3) {
            level = 3;
          }

          //determine type and id
          out.put(KEY_OBJECT_TYPE, OBJECT_TYPE_CATEGORY);
          out.put(CATEGORY_ID, category.getExternalTechId());
          out.put(LEVEL, level);
          if (level >= 2) {
            out.put(TOP_CATEGORY_ID, category.getBreadcrumb().get(0).getExternalTechId());
          }
          if (level >= 3) {
            Category parent = category.getParent();
            out.put(PARENT_CATEGORY_ID, parent != null ? parent.getExternalTechId() : category.getExternalTechId());
          }
        }
      }
      else if (bean instanceof CMExternalPage) {
        CMExternalPage externalChannel = (CMExternalPage) bean;
        out.put(KEY_OBJECT_TYPE, OBJECT_TYPE_LC_PAGE);
        if (isNotEmpty(externalChannel.getExternalUriPath())) {
          //pass the fully qualified url to shop system
          out.put(EXTERNAL_URI_PATH_PARAMETER_NAME,
                  externalNavigationHandler.buildLinkForExternalPage(externalChannel, Collections.emptyMap(), request));
        } else {
          out.put(EXTERNAL_SEOSEGMENT_PARAMETER_NAME, externalChannel.getExternalId());
        }
      }
      // Content
      else if (bean instanceof CMObject) {
        CMObject contentBean = (CMObject) bean;
        String contentId = ExternalReferenceResolver.CONTENT_ID_FRAGMENT_PREFIX + navigation.getContentId() + "-" + contentBean.getContentId();

        //set type and id
        out.put(KEY_OBJECT_TYPE, OBJECT_TYPE_CONTENT);
        out.put(CONTENT_ID, contentId);

        // read url prefix for coremedia content
        if (commerceLedLinkBuilderHelper.isCommerceLedChannel(contentBean)) {
          String contentURLKeyword = commerceLedLinkBuilderHelper.getContentURLKeyword();
          if (StringUtils.isNotBlank(contentURLKeyword)) {
            out.put("staticContentURLKeyword", contentURLKeyword);
          }
        }

        // SEO Segments
        String externalSeoSegment = seoSegmentBuilder.asSeoSegment(navigation, contentBean);
        if (StringUtils.isNotBlank(externalSeoSegment)) {
          out.put(EXTERNAL_SEOSEGMENT_PARAMETER_NAME, externalSeoSegment);
        }
      }
    } catch (Exception e) {
      LOG.error("Error creating placeholder JSON representation", e);
    }

    return out;
  }
}
