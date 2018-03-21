package com.coremedia.blueprint.studio.template {
import com.coremedia.blueprint.studio.template.model.ProcessingData;
import com.coremedia.cap.common.SESSION;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.user.User;
import com.coremedia.cms.editor.sdk.components.StudioDialog;
import com.coremedia.cms.editor.sdk.components.folderprompt.FolderCreationResult;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.cms.editor.sdk.sites.Site;
import com.coremedia.cms.editor.sdk.util.ContentCreationUtil;
import com.coremedia.cms.editor.sdk.util.MessageBoxUtil;
import com.coremedia.cms.editor.sdk.util.StudioConfigurationUtil;
import com.coremedia.ui.components.StatefulTextField;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;
import com.coremedia.ui.mixins.IValidationStateMixin;
import com.coremedia.ui.mixins.ValidationState;

import ext.StringUtil;
import ext.form.field.Field;

import mx.resources.ResourceManager;

/**
 * The base class of the create from template dialog creates.
 */
[ResourceBundle('com.coremedia.blueprint.studio.template.CreateFromTemplateStudioPlugin')]
[ResourceBundle('com.coremedia.blueprint.studio.template.CreateFromTemplateStudioPluginSettings')]
[ResourceBundle('com.coremedia.blueprint.base.components.navigationlink.NavigationLinkField')]
public class CreateFromTemplateDialogBase extends StudioDialog {
  private var disabledExpression:ValueExpression;
  private var model:ProcessingData;

  private var errorMessages:Object = {};

  private var baseFolderEditorial:ValueExpression;
  private var baseFolderNavigation:ValueExpression;
  private var pageFolderChangeExpression:ValueExpression;

  public static const TEMPLATE_CHOOSER_FIELD_ID:String = "templateChooserField";
  public static const PAGE_FOLDER_COMBO_ID:String = 'folderCombo';
  public static const PARENT_PAGE_FIELD_ID:String = 'parentPageFieldId';
  public static const EDITORIAL_FOLDER_COMBO_ID:String = 'editorialFolderCombo';
  public static const NAME_FIELD_ID:String = 'nameField';
  public static const EDITOR_CONTAINER_ITEM_ID:String = "editorContainer";

  private var nameField:StatefulTextField;

  public function CreateFromTemplateDialogBase(config:CreateFromTemplateDialog = null) {
    super(config);

    errorMessages[NAME_FIELD_ID] = resourceManager.getString('com.coremedia.blueprint.studio.template.CreateFromTemplateStudioPlugin', 'name_not_valid_value');
    errorMessages[TEMPLATE_CHOOSER_FIELD_ID] = resourceManager.getString('com.coremedia.blueprint.studio.template.CreateFromTemplateStudioPlugin', 'template_chooser_empty_text');
    errorMessages[PAGE_FOLDER_COMBO_ID] = resourceManager.getString('com.coremedia.blueprint.studio.template.CreateFromTemplateStudioPlugin', 'page_folder_combo_validation_message');
  }

  /**
   * Init dialog
   */
  override protected function afterRender():void {
    super.afterRender();

    nameField = queryById(NAME_FIELD_ID) as StatefulTextField;
    nameField.on('blur', validateForm);

    getBaseFolderNavigation().loadValue(function(path:String):void {
      getModel().set(ProcessingData.FOLDER_PROPERTY, path);
      getBaseFolderNavigation().addChangeListener(navigationFolderListener)
    });

    getBaseFolderEditorial().loadValue(function(path:String):void {
      getModel().set(resourceManager.getString('com.coremedia.blueprint.studio.template.CreateFromTemplateStudioPluginSettings', 'editorial_folder_property'), path);
      getBaseFolderEditorial().addChangeListener(editorialFolderListener)
    });

    pageFolderChangeExpression = ValueExpressionFactory.create(ProcessingData.FOLDER_PROPERTY, getModel());
    pageFolderChangeExpression.addChangeListener(validateForm);
    validateForm();
  }

  private function navigationFolderListener():void {
    getBaseFolderNavigation().loadValue(function(path:String):void {
      getModel().set(ProcessingData.FOLDER_PROPERTY, path);
    });
  }

  private function editorialFolderListener():void {
    getBaseFolderEditorial().loadValue(function(path:String):void {
      getModel().set(resourceManager.getString('com.coremedia.blueprint.studio.template.CreateFromTemplateStudioPluginSettings', 'editorial_folder_property'), path);
    });
  }

  override protected function onDestroy():void {
    super.onDestroy();
    model.removeValueChangeListener(validateForm);
    getBaseFolderNavigation().removeChangeListener(navigationFolderListener);
    getBaseFolderEditorial().removeChangeListener(editorialFolderListener);
    pageFolderChangeExpression && pageFolderChangeExpression.removeChangeListener(validateForm);
  }

  /**
   * Creates the model that is used for this dialog.
   * @return
   */
  protected function getModel():ProcessingData {
    if (!model) {
      model = new ProcessingData();
      model.addValueChangeListener(validateForm);
    }
    return model;
  }

  private function validateForm():void {
    getDisabledExpression().setValue(false);

    //we can use the field validator since after the field becomes invalid, no event is fired to correct the value of the bound value expression
    if(nameField) {
      var result:Boolean = nameValidator();
      applyValidationResult(nameField, result);
    }

    validate(queryById(EDITORIAL_FOLDER_COMBO_ID), editorialFolderValidator);
    validateAsync(queryById(PAGE_FOLDER_COMBO_ID), folderValidator);
    validate(queryById(TEMPLATE_CHOOSER_FIELD_ID));
  }

  private function validate(editor:*, validatorFunction:Function = null):void {
    if (editor) {
      validatorFunction = validatorFunction || editor.initialConfig['validate'];
      if (validatorFunction) {
        var result:Boolean = validatorFunction(editor);
        applyValidationResult(editor, result);
      }
    }
  }

  private function validateAsync(editor:*, validator:Function):void {
    if (editor) {
      if (validator) {
        validator.call(null, function(result:Boolean):void {
          applyValidationResult(editor, result);
        });
      }
    }
  }

  private function applyValidationResult(editor:*, result:Boolean):void {
    var statefulEditor:IValidationStateMixin = editor as IValidationStateMixin;
    var errorMsg:String = errorMessages[editor.itemId];
    if (!errorMsg) {
      errorMsg = resourceManager.getString('com.coremedia.blueprint.studio.template.CreateFromTemplateStudioPlugin', 'template_create_missing_value');
    }
    if (!result) {
      if (statefulEditor) {
        statefulEditor.validationState = ValidationState.ERROR;
        statefulEditor.validationMessage = errorMsg;
      }
      getDisabledExpression().setValue(true);
    } else {
      if (statefulEditor) {
        statefulEditor.validationState = null;
        statefulEditor.validationMessage = null;
      }
    }
  }

  /**
   * Invokes the post processing and closes the dialog
   */
  protected function handleSubmit():void {
    var data:ProcessingData = getModel();
    var path:String = data.get(ProcessingData.FOLDER_PROPERTY);
    var parent:Content = data.get(resourceManager.getString('com.coremedia.blueprint.studio.template.CreateFromTemplateStudioPluginSettings', 'parent_property'));

    if (!parent) {
      MessageBoxUtil.showConfirmation(resourceManager.getString('com.coremedia.blueprint.studio.template.CreateFromTemplateStudioPlugin', 'text'),
              resourceManager.getString('com.coremedia.blueprint.studio.template.CreateFromTemplateStudioPlugin', 'no_parent_page_selected_warning'),
              resourceManager.getString('com.coremedia.blueprint.studio.template.CreateFromTemplateStudioPlugin', 'no_parent_page_selected_warning_buttonText'),
              function (buttonId:String):void {
                if (buttonId === "ok") {
                  doCreation(path);
                }
              });
    } else {
      parent.invalidate(function():void {
        if (parent.isCheckedOutByOther()) {
          parent.getEditor().load(function(user:User):void {
            var msg:String = StringUtil.format(resourceManager.getString('com.coremedia.blueprint.base.components.navigationlink.NavigationLinkField', 'layout_error_msg'), user.getName());
            MessageBoxUtil.showError(resourceManager.getString('com.coremedia.blueprint.base.components.navigationlink.NavigationLinkField', 'layout_error'), msg);
          })
        } else {
          doCreation(path);
        }
      });
    }
  }


  /**
   * Performs the creation of the content
   * @param path the navigation path
   */
  private function doCreation(path:String):void {
    //first ensure that all folders exist
    var data:ProcessingData = getModel();

    var editorialFolderName:String = data.get(resourceManager.getString('com.coremedia.blueprint.studio.template.CreateFromTemplateStudioPluginSettings', 'editorial_folder_property'));
    ContentCreationUtil.createRequiredSubfolders(path, function (result:FolderCreationResult):void {
      if (result.success) {
        var navigationFolder:Content = result.baseFolder;
        ContentCreationUtil.createRequiredSubfolders(editorialFolderName, function (editorialResult:FolderCreationResult):void {
          if (editorialResult.success) {
            destroy();

            //apply the folder instance to the processing data
            data.set(ProcessingData.FOLDER_PROPERTY, navigationFolder);
            CreateFromTemplateProcessor.process(data, function ():void {
              trace('INFO', 'Finished create from template');
              var content:Content = data.getContent();
              var initializer:Function = editorContext.lookupContentInitializer(content.getType());
              if (initializer) {
                initializer(content);
              }

              var parent:Content = data.get(resourceManager.getString('com.coremedia.blueprint.studio.template.CreateFromTemplateStudioPluginSettings', 'parent_property'));
              if (parent) {
                parent.invalidate(function ():void {
                  editorContext.getContentTabManager().openDocument(parent);
                  openNewPageInTab(data);
                });
              } else {
                openNewPageInTab(data);
              }
            });
          } else {
            MessageBoxUtil.showError(resourceManager.getString('com.coremedia.blueprint.studio.template.CreateFromTemplateStudioPlugin', 'text'),
                    resourceManager.getString('com.coremedia.blueprint.studio.template.CreateFromTemplateStudioPlugin', 'editor_folder_could_not_create_message'));
            editorialResult.remoteError.setHandled(true);
          }
        });
      } else {
        MessageBoxUtil.showError(resourceManager.getString('com.coremedia.blueprint.studio.template.CreateFromTemplateStudioPlugin', 'text'),
                resourceManager.getString('com.coremedia.blueprint.studio.template.CreateFromTemplateStudioPlugin', 'page_folder_could_not_create_message'));
        result.remoteError.setHandled(true);
      }
    });
  }

  private function openNewPageInTab(data:ProcessingData):void {
    var newPage:Content = data.getContent();
    newPage.invalidate(function ():void {
      editorContext.getContentTabManager().openDocument(newPage);
    });
  }

  /**
   * Calculates if the mandatory input is given.
   * @return
   */
  protected function getDisabledExpression():ValueExpression {
    if (!disabledExpression) {
      disabledExpression = ValueExpressionFactory.create('disabled', beanFactory.createLocalBean());
      disabledExpression.setValue(true);
    }
    return disabledExpression;
  }

  protected function nameValidator():Boolean {
    var repository:ContentRepository = SESSION.getConnection().getContentRepository();
    return getModel().get(ProcessingData.NAME_PROPERTY) && repository.isValidName(getModel().get(ProcessingData.NAME_PROPERTY));
  }

  protected function folderValidator(callback:Function):void {
    var ve:ValueExpression = ValueExpressionFactory.create(ProcessingData.FOLDER_PROPERTY, getModel());
    if (ve && ve.getValue() && (ve.getValue() as String).length > 0) {
      var folder:String = ve.getValue();
      SESSION.getConnection().getContentRepository().getChild(folder, function(c:Content):void {
        if (c) {
          callback(false);
        } else {
          callback(true);
        }
      });
    } else {
      callback(true);
    }
  }

  protected function templateChooserNonEmptyValidator():Boolean {
    var ve:ValueExpression = ValueExpressionFactory.create(
            resourceManager.getString('com.coremedia.blueprint.studio.template.CreateFromTemplateStudioPluginSettings', 'template_property'), getModel());
    return ve && ve.getValue() && (ve.getValue() as Array).length > 0;
  }

  protected static function editorialFolderValidator(editor:Field):Boolean {
    var value:* = editor.getValue();
    return !!(value && value.length > 0);
  }


  protected function getBaseFolderEditorial():ValueExpression {
    if (!baseFolderEditorial) {
      baseFolderEditorial = ValueExpressionFactory.createFromFunction(baseFolderEditorialCalculation);
    }
    return baseFolderEditorial;
  }

  protected function getBaseFolderNavigation():ValueExpression {
    if (!baseFolderNavigation) {
      baseFolderNavigation = ValueExpressionFactory.createFromFunction(baseFolderNavigationCalculation);
    }
    return baseFolderNavigation;
  }

  protected function getNavigationFolders():Array {
    var baseFolder:String = baseFolderNavigationCalculation();
    if(baseFolder) {
      return [baseFolder];
    }
    return [];
  }

  protected function getEditorialFolders():Array {
    var baseFolder:String = baseFolderEditorialCalculation();
    if(baseFolder) {
      return [baseFolder];
    }
    return [];
  }

  private function baseFolderNavigationCalculation():String {
    return baseFolderCalculation("paths.navigation", getNavigationFolderFallback);
  }

  private function baseFolderEditorialCalculation():String {
    return baseFolderCalculation("paths.editorial", getEditorialFolderFallback);
  }

  private function baseFolderCalculation(configuration:String, fallback:Function):String {
    var retPath:String = baseFolderCalculationRaw(configuration, fallback);
    if (retPath === undefined) {
      return undefined;
    }

    var diffSelectedParentPageAndNavigationPath:String = getDiffNavigationFolderParentFolder();
    if (diffSelectedParentPageAndNavigationPath === undefined) {
      return undefined;
    }

    if (diffSelectedParentPageAndNavigationPath) {
      retPath += "/" + diffSelectedParentPageAndNavigationPath;
    }

    var name:String = getModel().get(ProcessingData.NAME_PROPERTY);
    if (name && name.length > 0) {
      retPath += "/" + name;
    }
    return retPath;
  }

  private static function baseFolderCalculationRaw(configuration:String, fallback:Function):String {
    var folder:Content = StudioConfigurationUtil.getConfiguration("Content Creation", configuration);

    if (folder === undefined) {
      return undefined;
    }

    if (folder === null) {
      return fallback();
    } else {
      return folder.getPath();
    }
  }

  private function getDiffNavigationFolderParentFolder():String {
    var folderNavigation:String = baseFolderCalculationRaw("paths.navigation", getNavigationFolderFallback);
    if (folderNavigation === undefined) {
      return undefined;
    }
    var parent:Content = getModel().get(resourceManager.getString('com.coremedia.blueprint.studio.template.CreateFromTemplateStudioPluginSettings', 'parent_property'));
    if (!parent) {
      return null;
    }

    var parentFolder:Content = parent.getParent();

    if (parentFolder === undefined) {
      return undefined;
    }

    var parentFolderPath:String = parentFolder.getPath();
    if (parentFolderPath === undefined) {
      return undefined;
    }

    if (parentFolderPath.substr(0, folderNavigation.length) === folderNavigation) {
      return parentFolderPath.substr(folderNavigation.length + 1);
    } else {
      return null;
    }
  }

  protected static function getNavigationFolderFallback():String {
    return getFolderFallback(ResourceManager.getInstance().getString('com.coremedia.blueprint.studio.template.CreateFromTemplateStudioPluginSettings', 'doctype'));
  }

  protected static function getEditorialFolderFallback():String {
    return getFolderFallback("CMArticle");
  }

  protected static function getFolderFallback(docType:String):String {
    var siteId:String = editorContext.getSitesService().getPreferredSiteId();
    var site:Site = editorContext.getSitesService().getSite(siteId);
    var docTypeDefault:String = ResourceManager.getInstance().getString('com.coremedia.blueprint.studio.template.CreateFromTemplateStudioPluginSettings', docType + '_home_folder');
    var path:String = site.getSiteRootFolder().getPath();
    if (path === undefined) {
      return undefined;
    }
    if(docTypeDefault) {
      if(docTypeDefault.indexOf('/') === 0) {
        return docTypeDefault;
      }
      if(site) {
        return path + '/' + docTypeDefault;
      }
    }
    if(site) {
      return path;
    }
    return null;
  }

  public static function getDescription(name:String, content:Content):String {
    if (content && content.getProperties()) {

      var description:String = content.getProperties().get('description');
      if (!description || description.length === 0) {
        description = name;
      }
      return description;
    }
    return "";
  }

}
}
