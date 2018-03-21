package com.coremedia.blueprint.cae.contentbeans;

import com.coremedia.blueprint.common.contentbeans.CMPicture;
import com.coremedia.cap.common.NoSuchPropertyDescriptorException;
import com.coremedia.cap.transform.TransformImageService;
import com.coremedia.cap.transform.Transformation;
import org.springframework.beans.factory.annotation.Required;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Map.Entry;

/**
 * Generated extension class for immutable beans of document type "CMPicture".
 */
public class CMPictureImpl extends CMPictureBase {
  private static final String TRANSFORMS = "transforms";

  private TransformImageService transformImageService;

  /*
   * Add additional methods here.
   * Add them to the interface {@link com.coremedia.blueprint.common.contentbeans.CMPicture} to make them public.
   */
  @Required
  public void setTransformImageService(TransformImageService transformImageService) {
    this.transformImageService = transformImageService;
  }

  /**
   * Overrides the CMTeasable feature for convenience:
   * If this CMPicture document has no delegate teaser picture, it is
   * considered to be its own teaser picture.
   *
   * @return the teasable image or "this" as fallback.
   */
  @Override
  public CMPicture getPicture() {
    CMPicture picture = super.getPicture();
    return picture==null ? this : picture;
  }

  /**
   * Override the method to handle images, which does not have a transformation already.
   * @return a map of transformations, merged from image settings and {@link TransformImageService} service
   */
  @Override
  public Map<String,String> getTransformMap() {
    Map<String,String> transformations = new HashMap<>();

    try {
      if (getLocalSettings() != null) {
        Map<String,Object> structMap = getLocalSettings().getStruct(TRANSFORMS).getProperties();

        //copy struct because it may be cached and the cache MUST NEVER be modified.
        for(Entry<String,Object> entry : structMap.entrySet()) {
          if(entry.getValue() != null) {
            transformations.put(entry.getKey(),entry.getValue().toString());
          }
        }
      }
    }
    catch (NoSuchPropertyDescriptorException e) {
      //no transforms configured for current content, empty map will be returned.
    }

    return transformImageService.getTransformationOperations(this.getContent(), DATA, transformations);
  }

  @Override
  public List<Transformation> getTransformations() {
    return transformImageService.getTransformations(getContent());
  }

  @Override
  public Transformation getTransformation(String name) {
    return transformImageService.getTransformation(this.getContent(), name);
  }
}
