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
import consulo.annotation.component.ExtensionImpl;
import consulo.google.guice.localize.GoogleGuiceLocalize;
import consulo.language.editor.inspection.LocalQuickFix;
import consulo.language.editor.inspection.ProblemDescriptor;
import consulo.language.psi.PsiElement;
import consulo.language.util.IncorrectOperationException;
import consulo.localize.LocalizeValue;
import consulo.logging.Logger;
import consulo.project.Project;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

@ExtensionImpl
public class PointlessBindingInspection extends BaseInspection{
    private static final Logger LOGGER = Logger.getInstance("PointlessBindingInspection");

    @Nonnull
    protected String buildErrorString(Object... infos){
        return GoogleGuiceLocalize.pointlessBindingProblemDescriptor().get();
    }

    public BaseInspectionVisitor buildVisitor(){
        return new Visitor();
    }

    @Nullable
    public LocalQuickFix buildFix(PsiElement location, Object[] infos){
        return new DeleteBindingFix();
    }

    @Nonnull
    @Override
    public LocalizeValue getDisplayName() {
        return GoogleGuiceLocalize.pointlessBindingDisplayName();
    }

    private static class DeleteBindingFix implements LocalQuickFix{
        @Nonnull
        public LocalizeValue getName(){
            return GoogleGuiceLocalize.deleteBinding();
        }

        @SuppressWarnings({"HardCodedStringLiteral"})
        public void applyFix(@Nonnull Project project, ProblemDescriptor descriptor){
            final PsiMethodCallExpression element = (PsiMethodCallExpression) descriptor.getPsiElement();
            try{
                element.getParent().delete();
            } catch(IncorrectOperationException e){
                LOGGER.error(e);
            }
        }
    }

    private static class Visitor extends BaseInspectionVisitor{
        public void visitMethodCallExpression(PsiMethodCallExpression expression){
            super.visitMethodCallExpression(expression);
            final PsiReferenceExpression methodExpression = expression.getMethodExpression();
            final String methodName = methodExpression.getReferenceName();
            if(!"bind".equals(methodName)){
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
            final PsiElement parent = expression.getParent();
            if(!(parent instanceof PsiExpressionStatement)){
                return;
            }
            registerError(expression);
        }
    }
}