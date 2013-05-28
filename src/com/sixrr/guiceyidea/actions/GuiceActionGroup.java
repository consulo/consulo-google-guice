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

import com.intellij.ide.IdeView;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiDirectory;
import com.sixrr.guiceyidea.GuiceyIDEABundle;
import com.sixrr.guiceyidea.GuiceyIDEAIcons;
import com.sixrr.guiceyidea.module.extension.GuiceyIDEAModuleExtension;

public class GuiceActionGroup extends DefaultActionGroup
{
	public GuiceActionGroup()
	{
		super(GuiceyIDEABundle.message("action.group.guice.title"), true);
		final Presentation presentation = getTemplatePresentation();
		presentation.setDescription(GuiceyIDEABundle.message("action.group.guice.description"));
		presentation.setIcon(GuiceyIDEAIcons.GUICE_LOGO);
	}

	@Override
	public void update(AnActionEvent e)
	{
		final Module module = e.getData(LangDataKeys.MODULE);
		if(module == null || ModuleUtilCore.getExtension(module, GuiceyIDEAModuleExtension.class) != null)
		{
			return;
		}

		final IdeView view = e.getData(LangDataKeys.IDE_VIEW);
		final Project project = e.getData(LangDataKeys.PROJECT);
		final Presentation presentation = e.getPresentation();
		if(project != null && view != null)
		{
			final ProjectFileIndex projectFileIndex = ProjectRootManager.getInstance(project).getFileIndex();
			final PsiDirectory[] dirs = view.getDirectories();
			for(PsiDirectory dir : dirs)
			{
				if(projectFileIndex.isInSourceContent(dir.getVirtualFile()) && JavaDirectoryService.getInstance().getPackage(dir) != null)
				{
					presentation.setVisible(true);
					return;
				}
			}
		}
		presentation.setVisible(false);
	}
}
