package com.coremedia.blueprint.studio {

import com.coremedia.cap.common.SESSION;
import com.coremedia.cap.workflow.Process;
import com.coremedia.cap.workflow.Task;
import com.coremedia.cap.workflow.WorkflowRepository;
import com.coremedia.cap.workflow.WorklistService;
import com.coremedia.cap.workflow.impl.ProcessImpl;
import com.coremedia.cms.editor.configuration.StudioPlugin;
import com.coremedia.cms.editor.controlroom.ControlRoomContextImpl;
import com.coremedia.cms.editor.controlroom.controlRoomContext;
import com.coremedia.cms.editor.controlroom.workflow.TabbedWorkflowPanel;
import com.coremedia.cms.editor.sdk.IEditorContext;
import com.coremedia.collaboration.controlroom.rest.CapListRepositoryImpl;
import com.coremedia.ui.data.BeanState;

public class MemoryControlRoomPluginBase extends StudioPlugin {

  public function MemoryControlRoomPluginBase(config:MemoryControlRoomPlugin = null) {
    if (!CapListRepositoryImpl.getInstance()) {
      trace("[WARN] cap list repository is not available, disabling control room");
      return;
    }

    super(config);
  }

  override public function init(editorContext:IEditorContext):void {
    if (!controlRoomContext) {
      ControlRoomContextImpl.initControlRoomContext();
    }

    if (!CapListRepositoryImpl.getInstance()) {
      trace("[WARN] control room is disabled, skipping initialization");
      return;
    }

    controlRoomContext.addCallback(initializeMemoryCapLists);
    super.init(editorContext);
  }

  protected function initializeMemoryCapLists():void {
    //Publication Workflow Panel
    //if inbox is already loaded: update pending processes
    if (controlRoomContext.getPublicationWfPanel().getInboxTasksValueExpression().isLoaded()) {
      updatePendingProcessesForPublication();
    } else {
      //wait until inbox is loaded to update pending processes
      controlRoomContext.getPublicationWfPanel().getInboxTasksValueExpression().addChangeListener(updatePendingProcessesForPublication);
    }

    //Translation Workflow Panel
    if (controlRoomContext.getTranslationWfPanel().getInboxTasksValueExpression().isLoaded()) {
      updatePendingProcessesForTranslation();
    } else {
      controlRoomContext.getTranslationWfPanel().getInboxTasksValueExpression().addChangeListener(updatePendingProcessesForTranslation);
    }
  }

  private function updatePendingProcessesForPublication():void {
    getInboxTasksAndUpdatePendingProcesses(controlRoomContext.getPublicationWfPanel());
  }

  private function updatePendingProcessesForTranslation():void {
    getInboxTasksAndUpdatePendingProcesses(controlRoomContext.getTranslationWfPanel());
  }

  private function getInboxTasksAndUpdatePendingProcesses(workflowPanel:TabbedWorkflowPanel):void {
    //get all tasks from inbox to filter them from pending processes
    var tasksInbox:Array = workflowPanel.getInboxTasksValueExpression().getValue();
    // Index all processes in the inbox for faster lookup.
    var inboxProcessUris:Object = {};
    for (var i:int = 0; i < tasksInbox.length; i++) {
      if (tasksInbox[i]) {
        var inboxProcess:Process = (tasksInbox[i] as Task).getContainingProcess();
        if (inboxProcess) {
          inboxProcessUris[inboxProcess.getUriPath()] = true;
        }
      }
    }
    updatePendingProcesses(inboxProcessUris, workflowPanel);
  }

  private function updatePendingProcesses(filterUris:Object, workflowPanel:TabbedWorkflowPanel):void {
    //get running processes from workflow server
    var workflowRepository:WorkflowRepository = SESSION.getConnection().getWorkflowRepository();
    if (null === workflowRepository) {
      return;
    }
    var worklistService:WorklistService = workflowRepository.getWorklistService();
    var processesRunning:Array = worklistService.getProcessesRunning() || [];

    processesRunning.forEach(function (process:ProcessImpl):void {
      var state:BeanState = process.getState();
      if (state === BeanState.NON_EXISTENT) {
        return;
      }
      if (state !== BeanState.NON_EXISTENT && state !== BeanState.UNREADABLE) {
        process.load(function ():void {
          if ((!filterUris || !filterUris[process.getUriPath()]) && workflowPanel.isProcessOfConfiguredType(process)) {
            CapListRepositoryImpl.getInstance().getPendingProcesses().addItems([process]);
          }
        });
      }
      //remove change listener after first call
      controlRoomContext.getPublicationWfPanel().getInboxTasksValueExpression().removeChangeListener(getInboxTasksAndUpdatePendingProcesses);
    });
  }
}
}
