package com.intellij.database.access;

import com.intellij.database.psi.DbSchemaElement;
import com.intellij.database.view.DatabaseView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.util.containers.ContainerUtil;

import java.util.Set;

/**
 * Created by jt on 2017-4-24.
 */
public class GeneratorCodeAction extends AnAction {

    public void actionPerformed(AnActionEvent e) {
        Set<DbSchemaElement> selectedElements = DatabaseView.getSelectedElements(e.getDataContext(), DbSchemaElement.class);
        DbSchemaElement schemaElement = ContainerUtil.getFirstItem(selectedElements);
        ExternalProcessHandler.start(schemaElement, e.getProject());
    }

    @Override
    public void update(AnActionEvent e) {
        Set<DbSchemaElement> selectedElements = DatabaseView.getSelectedElements(e.getDataContext(), DbSchemaElement.class);
        if(selectedElements != null && selectedElements.size() == 1){
            e.getPresentation().setEnabledAndVisible(true);
            return;
        }
        e.getPresentation().setEnabledAndVisible(false);
    }
}
