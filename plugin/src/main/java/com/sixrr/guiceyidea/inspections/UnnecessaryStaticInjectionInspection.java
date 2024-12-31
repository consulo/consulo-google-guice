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

package com.sixrr.guiceyidea.inspections;

import com.intellij.java.language.psi.*;
import com.sixrr.guiceyidea.GuiceyIDEABundle;
import consulo.annotation.component.ExtensionImpl;
import consulo.google.guice.util.GoogleGuiceAnnotationUtil;
import consulo.language.editor.inspection.LocalQuickFix;
import consulo.language.psi.PsiElement;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

@ExtensionImpl
public class UnnecessaryStaticInjectionInspection extends BaseInspection
{
	@Override
	@Nonnull
	protected String buildErrorString(Object... infos)
	{
		return GuiceyIDEABundle.message("unnecessary.static.injection.problem.descriptor");
	}

	@Override
	public BaseInspectionVisitor buildVisitor()
	{
		return new Visitor();
	}

	@Override
	@Nullable
	public LocalQuickFix buildFix(PsiElement location, Object[] infos)
	{
		return new DeleteBindingFix();
	}

	private static class Visitor extends BaseInspectionVisitor
	{
		@Override
		public void visitMethodCallExpression(PsiMethodCallExpression expression)
		{
			super.visitMethodCallExpression(expression);
			final PsiReferenceExpression methodExpression = expression.getMethodExpression();
			final String methodName = methodExpression.getReferenceName();
			if(!"requestStaticInjection".equals(methodName))
			{
				return;
			}
			final PsiExpression[] args = expression.getArgumentList().getExpressions();
			for(PsiExpression arg : args)
			{
				if(!(arg instanceof PsiClassObjectAccessExpression))
				{
					continue;
				}
				final PsiTypeElement classTypeElement = ((PsiClassObjectAccessExpression) arg).getOperand();
				final PsiType classType = classTypeElement.getType();
				if(!(classType instanceof PsiClassType))
				{
					continue;
				}
				final PsiClass classToBindStatically = ((PsiClassType) classType).resolve();
				if(classToBindStatically == null)
				{
					continue;
				}
				if(!classHasStaticInjects(classToBindStatically))
				{
					registerError(classTypeElement);
				}
			}
		}

		private static boolean classHasStaticInjects(PsiClass aClass)
		{
			final PsiMethod[] methods = aClass.getMethods();
			for(PsiMethod method : methods)
			{
				if(method.hasModifierProperty(PsiModifier.STATIC) && GoogleGuiceAnnotationUtil.isAnnotatedByInject(method, true))
				{
					return true;
				}
			}
			final PsiField[] fields = aClass.getFields();
			for(PsiField field : fields)
			{
				if(field.hasModifierProperty(PsiModifier.STATIC) && GoogleGuiceAnnotationUtil.isAnnotatedByInject(field, true))
				{
					return true;
				}
			}
			return false;
		}
	}
}