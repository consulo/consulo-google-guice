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

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.sixrr.guiceyidea.GuiceyIDEABundle;

public class NewGuiceProviderAction extends GeneratePluginClassAction
{
	private String myProvidedClassName;

	public NewGuiceProviderAction()
	{
		super(GuiceyIDEABundle.message("new.guice.provider.action.name"), GuiceyIDEABundle.message("new.guice.provider.action.name"), null);
	}

	@Override
	@NotNull
	protected PsiElement[] invokeDialogImpl(Project project, PsiDirectory directory)
	{
		final ProviderDialog dialog = new ProviderDialog(project);
		dialog.show();
		if(dialog.isOK())
		{
			final String providerName = dialog.getProviderName();
			myProvidedClassName = dialog.getProvidedClass();
			final MyInputValidator validator = new MyInputValidator(project, directory);
			validator.canClose(providerName);
			return validator.getCreatedElements();
		}
		return PsiElement.EMPTY_ARRAY;
	}

	@Nullable
	@Override
	protected Map<String, Object> getProperties()
	{
		Map<String, Object> properties = new HashMap<>();
		properties.put("PROVIDER_CLASS_NAME", myProvidedClassName);
		return properties;
	}

	@NotNull
	@Override
	protected String getTemplateName()
	{
		return "Google Guice Provider";
	}

	@Override
	protected String getErrorTitle()
	{
		return GuiceyIDEABundle.message("new.guice.provider.error");
	}

	@Override
	protected String getCommandName()
	{
		return GuiceyIDEABundle.message("new.guice.provider.command");
	}

	@Override
	protected String getActionName(PsiDirectory directory, String newName)
	{
		return GuiceyIDEABundle.message("new.guice.provider.name", directory, newName);
	}
}