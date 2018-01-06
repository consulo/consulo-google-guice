/*
 * Copyright 2013-2017 consulo.io
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

package consulo.google.guice;

import com.intellij.codeInsight.daemon.ImplicitUsageProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierListOwner;
import consulo.annotations.RequiredReadAction;
import consulo.google.guice.util.GoogleGuiceAnnotationUtil;

/**
 * @author VISTALL
 * @since 14-Feb-17
 */
public class GoogleGuiceImplicitUsageProvider implements ImplicitUsageProvider
{
	@Override
	public boolean isImplicitUsage(PsiElement psiElement)
	{
		return false;
	}

	@Override
	public boolean isImplicitRead(PsiElement psiElement)
	{
		return false;
	}

	@Override
	@RequiredReadAction
	public boolean isImplicitWrite(PsiElement psiElement)
	{
		if(psiElement instanceof PsiField || psiElement instanceof PsiMethod && ((PsiMethod) psiElement).isConstructor())
		{
			return GoogleGuiceAnnotationUtil.isAnnotatedByInject((PsiModifierListOwner) psiElement, true);
		}
		return false;
	}
}
