package com.coremedia.blueprint.cae.contentbeans;

import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.blueprint.common.contentbeans.CMPicture;
import com.coremedia.blueprint.common.contentbeans.CMTeasable;
import com.coremedia.cap.struct.Struct;
import com.coremedia.cap.struct.StructBuilder;
import com.coremedia.cap.struct.StructBuilderMode;

import java.util.List;

/**
 * Generated extension class for immutable beans of document type "CMTeaser".
 */
public class CMTeaserImpl extends CMTeaserBase {

  /*
   * Add additional methods here.
   * Add them to the interface {@link com.coremedia.blueprint.common.contentbeans.CMTeaser} to make them public.
   */

  @Override
  public Struct getLocalSettings() {
    Struct returnValue;

    Struct localSettings = super.getLocalSettings();
    CMLinkable target = getTarget();

    if (target == null || this.equals(target) /* avoid recursion and check for equality because identity does not work for data views*/) {
      //no target found, use local settings
      returnValue = localSettings;
    }
    else {
      //target found

      if(localSettings == null) {
        //no local settings, target should have settings
        returnValue = target.getLocalSettings();
      }
      else {
        //local settings found, merge with target settings if possible
        Struct targetSettings = target.getLocalSettings();

        if(targetSettings != null) {
          StructBuilder structBuilder = localSettings.builder();
          //tell structbuilder to allow merging of structs
          structBuilder = structBuilder.mode(StructBuilderMode.LOOSE);
          returnValue = structBuilder.defaultTo(targetSettings).build();
        }
        else {
          returnValue = localSettings;
        }
      }
    }

    return returnValue;
  }

  @Override
  public List<CMPicture> getPictures() {
    List<CMPicture> pictures = super.getPictures();
    if (pictures.isEmpty() && this.getTarget() instanceof CMTeasable && !this.getTarget().equals(this)) {
      pictures = ((CMTeasable) this.getTarget()).getPictures();
    }
    return pictures;
  }
}
  
