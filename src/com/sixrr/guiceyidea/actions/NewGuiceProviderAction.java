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
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.util.IncorrectOperationException;
import com.sixrr.guiceyidea.GuiceyIDEABundle;

public class NewGuiceProviderAction extends GeneratePluginClassAction{

    private static final Logger LOGGER = Logger.getInstance("NewGuiceProviderAction");
    private String providedClassName = null;

    public NewGuiceProviderAction(){
        super(GuiceyIDEABundle.message("new.guice.provider.action.name"),
                GuiceyIDEABundle.message("new.guice.provider.action.name"),
                null);
    }

    protected PsiElement[] invokeDialogImpl(Project project, PsiDirectory directory){
        final ProviderDialog dialog = new ProviderDialog(project);
        dialog.show();
        if(dialog.isOK()){
            final String providerName = dialog.getProviderName();
            providedClassName = dialog.getProvidedClass();
            final MyInputValidator validator = new MyInputValidator(project, directory);
            validator.canClose(providerName);
            return validator.getCreatedElements();
        }
        return PsiElement.EMPTY_ARRAY;
    }

    @NotNull
    protected PsiElement[] create(String newName, PsiDirectory directory){
        final Project project = directory.getProject();
        final PsiFileFactory elementFactory = PsiFileFactory.getInstance(project);
        final GuiceProviderBuilder builder = new GuiceProviderBuilder();
        builder.setClassName(newName);
        builder.setProvidedClassName(providedClassName);

        final String beanClassString;
        try{
            beanClassString = builder.buildProviderClass();
        } catch(IOException e){
            LOGGER.error(e);
            return PsiElement.EMPTY_ARRAY;
        }
        try{
            final PsiFile newFile = elementFactory.createFileFromText(newName + ".java", JavaFileType.INSTANCE, beanClassString);
            final JavaCodeStyleManager codeStyleManager = JavaCodeStyleManager.getInstance(project);
            final PsiElement shortenedFile = codeStyleManager.shortenClassReferences(newFile);
            final PsiElement reformattedFile = CodeStyleManager.getInstance(project).reformat(shortenedFile);
            directory.add(reformattedFile);
            return new PsiElement[]{reformattedFile};
        } catch(IncorrectOperationException e){
            LOGGER.error(e);
            return PsiElement.EMPTY_ARRAY;
        }
    }

    protected String getErrorTitle(){
        return GuiceyIDEABundle.message("new.guice.provider.error");
    }

    protected String getCommandName(){
        return GuiceyIDEABundle.message("new.guice.provider.command");
    }

    protected String getActionName(PsiDirectory directory, String newName){
        return GuiceyIDEABundle.message("new.guice.provider.name", directory, newName);
    }
}