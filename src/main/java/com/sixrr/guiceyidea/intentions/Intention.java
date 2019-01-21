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

package com.sixrr.guiceyidea.intentions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;

public abstract class Intention implements IntentionAction
{
	private final PsiElementPredicate predicate;

	protected Intention()
	{
		super();
		predicate = getElementPredicate();
	}

	@Override
	public void invoke(@Nonnull Project project, Editor editor, PsiFile file) throws IncorrectOperationException
	{
		if(!file.isWritable())
		{
			return;
		}
		final PsiElement element = findMatchingElement(file, editor);
		if(element == null)
		{
			return;
		}

		processIntention(element);
	}

	protected abstract void processIntention(@Nonnull PsiElement element) throws IncorrectOperationException;

	@Nonnull
	protected abstract PsiElementPredicate getElementPredicate();

	@Nullable
	public PsiElement findMatchingElement(PsiFile file, Editor editor)
	{
		final CaretModel caretModel = editor.getCaretModel();
		final int position = caretModel.getOffset();
		PsiElement element = file.findElementAt(position);
		while(element != null)
		{
			if(predicate.satisfiedBy(element))
			{
				return element;
			}
			else
			{
				element = element.getParent();
			}
		}
		return null;
	}

	@Override
	public boolean isAvailable(@Nonnull Project project, Editor editor, PsiFile file)
	{
		return findMatchingElement(file, editor) != null;
	}

	@Override
	public boolean startInWriteAction()
	{
		return true;
	}
}
