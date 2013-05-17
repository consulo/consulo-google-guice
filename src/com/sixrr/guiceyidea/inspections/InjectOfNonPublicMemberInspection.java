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
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtil;
import com.intellij.util.IncorrectOperationException;
import com.sixrr.guiceyidea.GuiceyIDEABundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InjectOfNonPublicMemberInspection extends BaseInspection{
    private static final Logger LOGGER = Logger.getInstance("RedundantToProviderBindingInspection");

    @NotNull
    protected String buildErrorString(Object... infos){
        final Boolean isField = (Boolean) infos[0];
        if(isField){
            return GuiceyIDEABundle.message("inject.of.non.public.field.problem.descriptor");
        } else{
            return GuiceyIDEABundle.message("inject.of.non.public.method.problem.descriptor");
        }
    }

    public BaseInspectionVisitor buildVisitor(){
        return new Visitor();
    }

    @Nullable
    public LocalQuickFix buildFix(PsiElement location, Object[] infos){
        return new MakePublicFix();
    }

    private static class MakePublicFix implements LocalQuickFix{
        @NotNull
        public String getName(){
            return GuiceyIDEABundle.message("make.public");
        }

        @NotNull
        public String getFamilyName(){
            return "";
        }

        @SuppressWarnings({"HardCodedStringLiteral"})
        public void applyFix(@NotNull Project project, ProblemDescriptor descriptor){
            final PsiMember member = (PsiMember) descriptor.getPsiElement().getParent();
            try{
              PsiUtil.setModifierProperty(member, PsiModifier.PUBLIC, true);
            } catch(IncorrectOperationException e){
                LOGGER.error(e);
            }
        }
    }

    private static class Visitor extends BaseInspectionVisitor{
        public void visitMethod(PsiMethod method){
            super.visitMethod(method);
            if(method.hasModifierProperty(PsiModifier.PUBLIC)){
                return;
            }
            if(!AnnotationUtil.isAnnotated(method, "com.google.inject.Inject", true)){
                return;
            }
            registerMethodError(method, false);
        }

        public void visitField(PsiField field){
            super.visitField(field);
            if(field.hasModifierProperty(PsiModifier.PUBLIC)){
                return;
            }
            if(!AnnotationUtil.isAnnotated(field, "com.google.inject.Inject", true)){
                return;
            }
            registerFieldError(field, false);
        }
    }
}