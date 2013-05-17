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
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.sixrr.guiceyidea.GuiceyIDEABundle;
import org.jetbrains.annotations.NotNull;

public class BindingAnnotationWithoutInjectInspection extends BaseInspection{

    @NotNull
    protected String buildErrorString(Object... infos){
        return GuiceyIDEABundle.message("binding.annotation.without.inject.problem.descriptor");
    }

    public BaseInspectionVisitor buildVisitor(){
        return new Visitor();
    }

    private static class Visitor extends BaseInspectionVisitor{
        public void visitAnnotation(PsiAnnotation annotation){
            super.visitAnnotation(annotation);
            if(!isBindingAnnotation(annotation)){
                return;
            }
            final PsiVariable boundVariable = PsiTreeUtil.getParentOfType(annotation, PsiVariable.class);
            if(boundVariable == null){
                return;
            }
            if(boundVariable instanceof PsiField){
                if(!AnnotationUtil.isAnnotated(boundVariable, "com.google.inject.Inject", true)){
                    registerError(annotation);
                }
            } else if(boundVariable instanceof PsiParameter){
                final PsiMethod containingMethod = PsiTreeUtil.getParentOfType(boundVariable, PsiMethod.class);
                if(containingMethod == null){
                    return;
                }
                if(!AnnotationUtil.isAnnotated(containingMethod, "com.google.inject.Inject", true)){
                    registerError(annotation);
                }
            }
        }
    }

    public static boolean isBindingAnnotation(PsiAnnotation annotation){
        final PsiJavaCodeReferenceElement referenceElement = annotation.getNameReferenceElement();
        if(referenceElement == null){
            return false;
        }
        final PsiElement element = referenceElement.resolve();
        if(!(element instanceof PsiClass)){
            return false;
        }
        final PsiClass annotationClass = (PsiClass) element;
        return AnnotationUtil.isAnnotated(annotationClass, "com.google.inject.BindingAnnotation", true);
    }
}