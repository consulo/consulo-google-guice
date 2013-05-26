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

import java.io.IOException;

import org.jetbrains.annotations.NotNull;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.util.IncorrectOperationException;
import com.sixrr.guiceyidea.GuiceyIDEABundle;

public class NewGuiceBindingAnnotationAction extends GeneratePluginClassAction{

    private static final Logger LOGGER = Logger.getInstance("NewGuiceBindingAnnotationAction");

    public NewGuiceBindingAnnotationAction(){
        super(GuiceyIDEABundle.message("new.guice.binding.annotation.action.name"),
                GuiceyIDEABundle.message("new.guice.binding.annotation.action.name"),
                null);
    }

    protected PsiElement[] invokeDialogImpl(Project project, PsiDirectory directory){
        final String annotationName =
                Messages.showInputDialog("Name for new binding annotation", "Create Guice Binding Annotation", Messages.getQuestionIcon());
        if(annotationName != null){
            final MyInputValidator validator = new MyInputValidator(project, directory);
            validator.canClose(annotationName);
            return validator.getCreatedElements();
        }
        return PsiElement.EMPTY_ARRAY;
    }

    @NotNull
    protected PsiElement[] create(String newName, PsiDirectory directory){
        final Project project = directory.getProject();
        final PsiManager manager = PsiManager.getInstance(project);
        final PsiFileFactory elementFactory = PsiFileFactory.getInstance(project);
        final GuiceBindingAnnotationBuilder builder = new GuiceBindingAnnotationBuilder();
        builder.setClassName(newName);
        final String beanClassString;
        try{
            beanClassString = builder.buildAnnotationClass();
        } catch(IOException e){
            LOGGER.error(e);
            return PsiElement.EMPTY_ARRAY;
        }
        try{
            final PsiFile newFile = elementFactory.createFileFromText(newName + ".java", JavaFileType.INSTANCE, beanClassString);
            final CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(project);
            final PsiElement shortenedFile = JavaCodeStyleManager.getInstance(project).shortenClassReferences(newFile);
            final PsiElement reformattedFile = codeStyleManager.reformat(shortenedFile);
            directory.add(reformattedFile);
            return new PsiElement[]{reformattedFile};
        } catch(IncorrectOperationException e){
            LOGGER.error(e);
            return PsiElement.EMPTY_ARRAY;
        }
    }

    protected String getErrorTitle(){
        return GuiceyIDEABundle.message("new.guice.binding.annotation.error");
    }

    protected String getCommandName(){
        return GuiceyIDEABundle.message("new.guice.binding.annotation.command");
    }

    protected String getActionName(PsiDirectory directory, String newName){
        return GuiceyIDEABundle.message("new.guice.binding.annotation.name", directory, newName);
    }
}