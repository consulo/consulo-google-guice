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

package com.sixrr.guiceyidea.utils;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EditorCaretMover{
    @NotNull
    private final Project project;

    public EditorCaretMover(@NotNull Project project){
        super();
        this.project = project;
    }

    @Nullable
    public Editor openInEditor(PsiElement element){
        final PsiFile psiFile;
        final int i;
        if(element instanceof PsiFile){
            psiFile = (PsiFile) element;
            i = -1;
        } else{
            psiFile = element.getContainingFile();
            i = element.getTextOffset();
        }
        if(psiFile == null){
            return null;
        }
        final VirtualFile virtualFile = psiFile.getVirtualFile();
        if(virtualFile != null){
            final OpenFileDescriptor fileDesc =
                    new OpenFileDescriptor(project, virtualFile, i);
            final FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
            return fileEditorManager.openTextEditor(fileDesc, true);
        } else{
            Logger.getInstance("Foo").error("Virtual file doesn't exist");
            return null;
        }
    }
}
