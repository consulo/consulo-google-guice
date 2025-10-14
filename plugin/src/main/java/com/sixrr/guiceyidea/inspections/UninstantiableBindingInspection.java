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
import com.sixrr.guiceyidea.utils.GuiceUtils;
import consulo.annotation.component.ExtensionImpl;
import consulo.google.guice.localize.GoogleGuiceLocalize;
import consulo.localize.LocalizeValue;
import jakarta.annotation.Nonnull;

@ExtensionImpl
public class UninstantiableBindingInspection extends BaseInspection{

    @Nonnull
    protected String buildErrorString(Object... infos){
        return GoogleGuiceLocalize.uninstantiableBindingProblemDescriptor().get();
    }

    @Nonnull
    @Override
    public LocalizeValue getDisplayName() {
        return GoogleGuiceLocalize.uninstantiableBindingDisplayName();
    }

    public BaseInspectionVisitor buildVisitor(){
        return new Visitor();
    }

    private static class Visitor extends BaseInspectionVisitor{
        public void visitMethodCallExpression(PsiMethodCallExpression expression){
            super.visitMethodCallExpression(expression);
            final PsiReferenceExpression methodExpression = expression.getMethodExpression();
            final String methodName = methodExpression.getReferenceName();
            if(!"to".equals(methodName)){
                return;
            }
            final PsiExpression[] args = expression.getArgumentList().getExpressions();
            if(args.length != 1){
                return;
            }
            final PsiExpression arg = args[0];
            if(!(arg instanceof PsiClassObjectAccessExpression)){
                return;
            }
            final PsiTypeElement classTypeElement = ((PsiClassObjectAccessExpression) arg).getOperand();
            final PsiType classType = classTypeElement.getType();
            if(!(classType instanceof PsiClassType)){
                return;
            }
            final PsiClass referentClass = ((PsiClassType) classType).resolve();
            if(referentClass == null){
                return;
            }
            if(GuiceUtils.isInstantiable(referentClass)){
                return;
            }
            final PsiMethod method = expression.resolveMethod();
            if(method == null){
                return;
            }
            final PsiClass containingClass = method.getContainingClass();
            if(!"com.google.inject.binder.LinkedBindingBuilder".equals(containingClass.getQualifiedName())){
                return;
            }
            registerError(classTypeElement);
        }
    }
}