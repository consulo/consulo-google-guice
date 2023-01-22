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

import com.intellij.java.language.codeInsight.AnnotationUtil;
import com.intellij.java.language.psi.*;
import com.sixrr.guiceyidea.GuiceyIDEABundle;
import com.sixrr.guiceyidea.utils.AnnotationUtils;
import com.sixrr.guiceyidea.utils.GuiceUtils;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.editor.inspection.LocalQuickFix;
import consulo.language.psi.PsiElement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@ExtensionImpl
public class RedundantToBindingInspection extends BaseInspection{

    @Nonnull
    protected String buildErrorString(Object... infos){
        return GuiceyIDEABundle.message("redundant.to.binding.problem.descriptor");
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
            final PsiClass boundClass = GuiceUtils.findImplementedClassForBinding(expression);
            if(boundClass == null){
                return;
            }
            if(AnnotationUtil.isAnnotated(boundClass, "com.google.inject.ProvidedBy", true)){
                return;
            }
            if(AnnotationUtil.isAnnotated(boundClass, "com.google.inject.ImplementedBy", true)){

                final PsiAnnotation implementedByAnnotation =
                        boundClass.getModifierList().findAnnotation("com.google.inject.ImplementedBy");
                final PsiElement defaultValue = AnnotationUtils.findDefaultValue(implementedByAnnotation);
                if(defaultValue == null){
                    return;
                }
                if(!(defaultValue instanceof PsiClassObjectAccessExpression)){
                    return;
                }
                final PsiTypeElement implementByClass = ((PsiClassObjectAccessExpression) defaultValue).getOperand();
                final PsiType implmenetedByClass = implementByClass.getType();
                if(!(implmenetedByClass instanceof PsiClassType)){
                    return;
                }
                final PsiClass implementedByClass = ((PsiClassType) implmenetedByClass).resolve();
                if(referentClass.equals(implementedByClass)){
                    registerError(classTypeElement);
                }
            } else{
                if(boundClass.equals(referentClass)){
                    registerError(classTypeElement);
                }
            }
        }
    }
}