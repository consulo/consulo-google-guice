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

import java.util.Map;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.ide.IdeView;
import com.intellij.ide.actions.CreateElementActionBase;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import consulo.annotations.RequiredDispatchThread;

public abstract class GeneratePluginClassAction extends CreateElementActionBase
{
	protected GeneratePluginClassAction(String text, String description, Icon icon)
	{
		super(text, description, icon);
	}

	@Override
	@NotNull
	protected PsiElement[] invokeDialog(Project project, PsiDirectory directory)
	{
		return invokeDialogImpl(project, directory);
	}

	@NotNull
	protected abstract PsiElement[] invokeDialogImpl(Project project, PsiDirectory directory);

	@NotNull
	protected abstract String getTemplateName();

	@Override
	@NotNull
	protected final PsiElement[] create(String newName, PsiDirectory directory)
	{
		FileTemplate template = FileTemplateManager.getInstance(directory.getProject()).getInternalTemplate(getTemplateName());
		try
		{
			Map<String, Object> properties = getProperties();

			PsiElement element = FileTemplateUtil.createFromTemplate(template, newName, properties, directory);
			return new PsiElement[]{element};
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	@Nullable
	protected Map<String, Object> getProperties()
	{
		return null;
	}

	@RequiredDispatchThread
	@Override
	public void update(@NotNull AnActionEvent e)
	{
		super.update(e);
		final Presentation presentation = e.getPresentation();
		if(!presentation.isEnabled())
		{
			return;
		}
		final IdeView view = e.getData(LangDataKeys.IDE_VIEW);
		final Project project = e.getData(LangDataKeys.PROJECT);
		if(view == null || project == null)
		{
			presentation.setEnabled(false);
			presentation.setVisible(false);
			return;
		}
		final ProjectFileIndex projectFileIndex = ProjectRootManager.getInstance(project).getFileIndex();
		final PsiDirectory[] dirs = view.getDirectories();
		for(PsiDirectory dir : dirs)
		{
			if(projectFileIndex.isInSourceContent(dir.getVirtualFile()) && JavaDirectoryService.getInstance().getPackage(dir) != null)
			{
				presentation.setEnabled(true);
				presentation.setVisible(true);
				return;
			}
		}

		presentation.setEnabled(false);
		presentation.setVisible(false);
	}
}
