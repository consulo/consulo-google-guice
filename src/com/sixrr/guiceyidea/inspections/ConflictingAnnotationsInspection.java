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
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiModifierListOwner;
import com.sixrr.guiceyidea.GuiceyIDEABundle;
import org.jetbrains.annotations.NotNull;

public class ConflictingAnnotationsInspection extends BaseInspection{

    @NotNull
    protected String buildErrorString(Object... infos){
        return GuiceyIDEABundle.message("conflicting.annotations.problem.descriptor");
    }

    public BaseInspectionVisitor buildVisitor(){
        return new Visitor();
    }

    private static class Visitor extends BaseInspectionVisitor{
        public void visitAnnotation(PsiAnnotation annotation){
            super.visitAnnotation(annotation);
            final PsiElement grandParent = annotation.getParent().getParent();
            if(!(grandParent instanceof PsiModifierListOwner)){
                return;
            }
            final PsiModifierListOwner owner = (PsiModifierListOwner) grandParent;
            final String qualifiedName = annotation.getQualifiedName();
            if("com.google.inject.ImplementedBy".equals(qualifiedName)){
                if(AnnotationUtil.isAnnotated(owner, "com.google.inject.ProvidedBy", true)){
                    registerError(annotation);
                }
                return;
            }
            if("com.google.inject.ProvidedBy".equals(qualifiedName)){
                if(AnnotationUtil.isAnnotated(owner, "com.google.inject.ImplementedBy", true)){
                    registerError(annotation);
                }
                return;
            }
            if("com.google.inject.Singleton".equals(qualifiedName)){
                if(AnnotationUtil.isAnnotated(owner, "com.google.inject.servlet.SessionScoped", true) ||
                        AnnotationUtil.isAnnotated(owner, "com.google.inject.servlet.RequestScoped", true)){
                    registerError(annotation);
                }
                return;
            }
            if("com.google.inject.servlet.SessionScoped".equals(qualifiedName)){
                if(AnnotationUtil.isAnnotated(owner, "com.google.inject.Singleton", true) ||
                        AnnotationUtil.isAnnotated(owner, "com.google.inject.servlet.RequestScoped", true)){
                    registerError(annotation);
                }
                return;
            }
            if("com.google.inject.servlet.RequestScoped".equals(qualifiedName)){
                if(AnnotationUtil.isAnnotated(owner, "com.google.inject.servlet.SessionScoped", true) ||
                        AnnotationUtil.isAnnotated(owner, "com.google.inject.Singleton", true)){
                    registerError(annotation);
                }
                return;
            }
        }
    }
}