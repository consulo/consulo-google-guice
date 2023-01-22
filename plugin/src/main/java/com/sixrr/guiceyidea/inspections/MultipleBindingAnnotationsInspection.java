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
import consulo.annotation.component.ExtensionImpl;
import consulo.language.psi.PsiElement;

import javax.annotation.Nonnull;

@ExtensionImpl
public class MultipleBindingAnnotationsInspection extends BaseInspection{

    @Nonnull
    protected String buildErrorString(Object... infos){
        return GuiceyIDEABundle.message("multiple.binding.annotations.problem.descriptor");
    }

    public BaseInspectionVisitor buildVisitor(){
        return new Visitor();
    }

    private static class Visitor extends BaseInspectionVisitor{
        public void visitVariable(PsiVariable variable){
            super.visitVariable(variable);
            final PsiModifierList modifiers = variable.getModifierList();
            if(modifiers == null){
                return;
            }
            final PsiAnnotation[] annotations = modifiers.getAnnotations();
            int numBindingAnnotations = 0;
            for(PsiAnnotation annotation : annotations){
                if(isBindingAnnotation(annotation)){
                    numBindingAnnotations++;
                }
            }
            if(numBindingAnnotations > 1){
                registerVariableError(variable);
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