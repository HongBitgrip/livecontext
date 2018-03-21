package com.coremedia.blueprint.personalization.editorplugin {
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.configuration.StudioPlugin;
import com.coremedia.cms.editor.sdk.IEditorContext;

public class P13NStudioPluginBase extends StudioPlugin {
  public function P13NStudioPluginBase(config:P13NStudioPlugin= null) {
    super(config);
  }

  override public function init(editorContext:IEditorContext):void {
    super.init(editorContext);

    editorContext.registerContentInitializer("CMP13NSearch", initP13NSearch);
  }

  private function initP13NSearch(content:Content):void {
    content.getProperties().set('documentType', 'Document_');
  }
}
}