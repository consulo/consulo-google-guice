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

import javax.annotation.Nonnull;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.sixrr.guiceyidea.GuiceyIDEABundle;

public class NewGuiceMethodInterceptorAction extends GeneratePluginClassAction
{
	public NewGuiceMethodInterceptorAction()
	{
		super(GuiceyIDEABundle.message("new.guice.method.interceptor.action.name"), GuiceyIDEABundle.message("new.guice.method.interceptor.action.name"), null);
	}

	@Override
	@Nonnull
	protected PsiElement[] invokeDialogImpl(Project project, PsiDirectory directory)
	{
		final String InterceptorName = Messages.showInputDialog("Name for new Method Interceptor", "Create Guice Method Interceptor", Messages.getQuestionIcon());
		if(InterceptorName != null)
		{
			final MyInputValidator validator = new MyInputValidator(project, directory);
			validator.canClose(InterceptorName);
			return validator.getCreatedElements();
		}
		return PsiElement.EMPTY_ARRAY;
	}

	@Nonnull
	@Override
	protected String getTemplateName()
	{
		return "Google Guice Method Interceptor";
	}

	@Override
	protected String getErrorTitle()
	{
		return GuiceyIDEABundle.message("new.guice.method.interceptor.error");
	}

	@Override
	protected String getCommandName()
	{
		return GuiceyIDEABundle.message("new.guice.method.interceptor.command");
	}

	@Override
	protected String getActionName(PsiDirectory directory, String newName)
	{
		return GuiceyIDEABundle.message("new.guice.method.interceptor.name", directory, newName);
	}
}