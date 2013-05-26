/*
 * Copyright 2000-2008 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sixrr.guiceyidea.actions;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import com.intellij.ide.IdeView;
import com.intellij.ide.actions.CreateElementActionBase;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;

public abstract class GeneratePluginClassAction extends CreateElementActionBase{

    // length == 1 is important to make MyInputValidator close the dialog when
    // module selection is canceled. That's some weird interface actually...
    private static final PsiElement[] CANCELED = new PsiElement[1];

    protected GeneratePluginClassAction(String text, String description, Icon icon){
        super(text, description, icon);
    }

    @NotNull
    protected PsiElement[] invokeDialog(Project project, PsiDirectory directory){
        final PsiElement[] psiElements = invokeDialogImpl(project, directory);
        if(psiElements == CANCELED){
            return PsiElement.EMPTY_ARRAY;
        }

        //   new EditorCaretMover(project).openInEditor(psiElements[0]);
        return psiElements;
    }

    protected abstract PsiElement[] invokeDialogImpl(Project project, PsiDirectory directory);

    protected void checkBeforeCreate(String newName, PsiDirectory directory) throws IncorrectOperationException{
        JavaDirectoryService.getInstance().checkCreateClass(directory, newName);
    }

    public void update(AnActionEvent e){
        super.update(e);
        final Presentation presentation = e.getPresentation();
        if(!presentation.isEnabled()){
            return;
        }
        final IdeView view = e.getData(LangDataKeys.IDE_VIEW);
        final Project project = e.getData(LangDataKeys.PROJECT);
        if(view == null || project == null){
            presentation.setEnabled(false);
            presentation.setVisible(false);
            return;
        }
        final ProjectFileIndex projectFileIndex = ProjectRootManager.getInstance(project).getFileIndex();
        final PsiDirectory[] dirs = view.getDirectories();
        for(PsiDirectory dir : dirs){
            if(projectFileIndex.isInSourceContent(dir.getVirtualFile()) && JavaDirectoryService.getInstance().getPackage(dir) != null){
                presentation.setEnabled(true);
                presentation.setVisible(true);
                return;
            }
        }

        presentation.setEnabled(false);
        presentation.setVisible(false);
    }
}
