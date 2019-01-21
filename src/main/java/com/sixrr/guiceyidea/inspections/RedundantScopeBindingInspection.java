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

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.psi.*;
import com.sixrr.guiceyidea.GuiceyIDEABundle;
import com.sixrr.guiceyidea.utils.GuiceUtils;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RedundantScopeBindingInspection extends BaseInspection{

    @Nonnull
    protected String buildErrorString(Object... infos){
        return GuiceyIDEABundle.message("redundant.scope.binding.problem.descriptor");
    }

    public BaseInspectionVisitor buildVisitor(){
        return new Visitor();
    }

    @Nullable
    public LocalQuickFix buildFix(PsiElement location, Object[] infos){
        return new DeleteBindingFix();
    }

    private static class Visitor extends BaseInspectionVisitor{
        public void visitMethodCallExpression(PsiMethodCallExpression expression){
            super.visitMethodCallExpression(expression);
            final PsiReferenceExpression methodExpression = expression.getMethodExpression();
            final String methodName = methodExpression.getReferenceName();
            if(!"in".equals(methodName)){
                return;
            }
            final PsiExpression[] args = expression.getArgumentList().getExpressions();
            if(args.length != 1){
                return;
            }
            final PsiExpression arg = args[0];
            if(!(arg instanceof PsiReferenceExpression)){
                return;
            }
            final String annotation = GuiceUtils.getScopeAnnotationForScopeExpression(arg);
            if(annotation == null){
                return;
            }
            final PsiClass boundClass = GuiceUtils.findImplementedClassForBinding(expression);
            if(boundClass == null){
                return;
            }
            if(!AnnotationUtil.isAnnotated(boundClass, annotation, true)){
                return;
            }
            registerError(arg);
        }
    }
}