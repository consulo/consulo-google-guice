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

import javax.annotation.Nonnull;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.sixrr.guiceyidea.GuiceyIDEABundle;
import com.sixrr.guiceyidea.utils.AnnotationUtils;
import consulo.google.guice.util.GoogleGuiceAnnotationUtil;

public class SingletonInjectsScopedInspection extends BaseInspection{

    @Nonnull
    protected String buildErrorString(Object... infos){
        return GuiceyIDEABundle.message("singleton.injects.scoped.problem.descriptor");
    }

    public BaseInspectionVisitor buildVisitor(){
        return new Visitor();
    }

    private static class Visitor extends BaseInspectionVisitor{
        public void visitAnnotation(PsiAnnotation annotation){
            super.visitAnnotation(annotation);
            final String qualifiedName = annotation.getQualifiedName();
            if(!"com.google.inject.Inject".equals(qualifiedName)){
                return;
            }
            final PsiClass containingClass = PsiTreeUtil.getParentOfType(annotation, PsiClass.class);
            if(containingClass == null){
                return;
            }
            if(!GoogleGuiceAnnotationUtil.isAnnotatedBySingleton(containingClass, true)){
                return;
            }
            final PsiElement owner = annotation.getParent().getParent();
            if(owner instanceof PsiField){
                final PsiField field = (PsiField) owner;
                checkForScopedInjection(field.getTypeElement());
            } else if(owner instanceof PsiMethod){
                final PsiMethod method = (PsiMethod) owner;
                final PsiParameter[] parameters = method.getParameterList().getParameters();
                for(PsiParameter parameter : parameters){
                    checkForScopedInjection(parameter.getTypeElement());
                }
            }
        }

        private void checkForScopedInjection(PsiTypeElement typeElement){
            final PsiType type = typeElement.getType();
            if(!(type instanceof PsiClassType)){
                return;
            }
            final PsiClassType classType = (PsiClassType) type;
            final PsiClass referencedClass = classType.resolve();
            if(referencedClass == null){
                return;
            }
            if(AnnotationUtil.isAnnotated(referencedClass, "com.google.inject.servlet.SessionScoped", true) ||
                    AnnotationUtil.isAnnotated(referencedClass, "com.google.inject.servlet.RequestScoped", true)){
                registerError(typeElement);
                return;
            }
            final PsiAnnotation implementedByAnnotation = referencedClass.getModifierList().findAnnotation("com.google.inject.ImplementedBy");
            if(implementedByAnnotation == null){
                return;
            }
            final PsiElement defaultValue = AnnotationUtils.findDefaultValue(implementedByAnnotation);
            if(defaultValue == null){
                return;
            }
            if(!(defaultValue instanceof PsiClassObjectAccessExpression)){
                return;
            }
            final PsiTypeElement classTypeElement = ((PsiClassObjectAccessExpression) defaultValue).getOperand();
            final PsiType implementedByClassType = classTypeElement.getType();
            if(!(implementedByClassType instanceof PsiClassType)){
                return;
            }
            final PsiClass implementedByClass = ((PsiClassType) implementedByClassType).resolve();
            if(implementedByClass == null){
                return;
            }
            if(AnnotationUtil.isAnnotated(implementedByClass, "com.google.inject.servlet.SessionScoped", true) ||
                    AnnotationUtil.isAnnotated(implementedByClass, "com.google.inject.servlet.RequestScoped", true)){
                registerError(typeElement);
            }
        }
    }
}