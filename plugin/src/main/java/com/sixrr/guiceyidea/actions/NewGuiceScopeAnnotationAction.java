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

import consulo.google.guice.localize.GoogleGuiceLocalize;
import consulo.language.psi.PsiDirectory;
import consulo.language.psi.PsiElement;
import consulo.localize.LocalizeValue;
import consulo.project.Project;
import consulo.ui.ex.awt.Messages;
import jakarta.annotation.Nonnull;

public class NewGuiceScopeAnnotationAction extends GeneratePluginClassAction {
    public NewGuiceScopeAnnotationAction() {
        super(GoogleGuiceLocalize.newGuiceScopeAnnotationActionName(), GoogleGuiceLocalize.newGuiceScopeAnnotationActionName(), null);
    }

    @Override
    @Nonnull
    protected PsiElement[] invokeDialogImpl(Project project, PsiDirectory directory) {
        final String annotationName = Messages.showInputDialog("Name for new scope annotation", "Create Guice Scope Annotation", Messages.getQuestionIcon());
        if (annotationName != null) {
            final MyInputValidator validator = new MyInputValidator(project, directory);
            validator.canClose(annotationName);
            return validator.getCreatedElements();
        }
        return PsiElement.EMPTY_ARRAY;
    }

    @Nonnull
    @Override
    protected String getTemplateName() {
        return "Google Guice Scope Annotation";
    }

    @Override
    protected LocalizeValue getErrorTitle() {
        return GoogleGuiceLocalize.newGuiceScopeAnnotationError();
    }

    @Override
    protected LocalizeValue getCommandName() {
        return GoogleGuiceLocalize.newGuiceScopeAnnotationCommand();
    }

    @Override
    protected LocalizeValue getActionName(PsiDirectory directory, String newName) {
        return GoogleGuiceLocalize.newGuiceScopeAnnotationName(directory, newName);
    }
}