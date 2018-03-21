package com.coremedia.blueprint.assets.contentbeans;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class AMDocumentAssetImpl extends AMDocumentAssetBase {
  @Nonnull
  @Override
  public List<AMAssetRendition> getRenditions() {
    List<AMAssetRendition> result = new ArrayList<>();

    result.addAll(super.getRenditions());
    result.add(getRendition(AMDocumentAsset.DOWNLOAD));

    return result;
  }

}