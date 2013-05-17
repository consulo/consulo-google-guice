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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UnnecessaryStaticInjectionInspection extends BaseInspection{

    @NotNull
    protected String buildErrorString(Object... infos){
        return GuiceyIDEABundle.message("unnecessary.static.injection.problem.descriptor");
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
            if(!"requestStaticInjection".equals(methodName)){
                return;
            }
            final PsiExpression[] args = expression.getArgumentList().getExpressions();
            for(PsiExpression arg : args){
                if(!(arg instanceof PsiClassObjectAccessExpression)){
                    continue;
                }
                final PsiTypeElement classTypeElement = ((PsiClassObjectAccessExpression) arg).getOperand();
                final PsiType classType = classTypeElement.getType();
                if(!(classType instanceof PsiClassType)){
                    continue;
                }
                final PsiClass classToBindStatically = ((PsiClassType) classType).resolve();
                if(classToBindStatically == null){
                    continue;
                }
                if(!classHasStaticInjects(classToBindStatically)){
                    registerError(classTypeElement);
                }
            }
        }

        private static boolean classHasStaticInjects(PsiClass aClass){
            final PsiMethod[] methods = aClass.getMethods();
            for(PsiMethod method : methods){
                if(method.hasModifierProperty(PsiModifier.STATIC) &&
                        AnnotationUtil.isAnnotated(method, "com.google.inject.Inject", true))
                {
                    return true;
                }
            }
            final PsiField[] fields = aClass.getFields();
            for(PsiField field : fields){
                if(field.hasModifierProperty(PsiModifier.STATIC) &&
                        AnnotationUtil.isAnnotated(field, "com.google.inject.Inject", true))
                {
                    return true;
                }
            }
            return false;
        }
    }
}