package com.coremedia.livecontext.p13n.studio {
import com.coremedia.cms.editor.sdk.context.ComponentContextManager;
import com.coremedia.cms.editor.sdk.premular.Premular;
import com.coremedia.cms.editor.sdk.premular.PropertyFieldGroup;
import com.coremedia.ui.data.ValueExpression;

public class CommerceGroupContainerBase extends PropertyFieldGroup{

  private var contentExpression:ValueExpression;

  public function CommerceGroupContainerBase(config:CommerceGroupContainer = null) {
    super(config);
  }

  internal function getContentExpression():ValueExpression {
    if (!contentExpression) {
      contentExpression = ComponentContextManager.getInstance().getContextExpression(this, Premular.CONTENT_VARIABLE_NAME);
    }
    return contentExpression;
  }
}
}