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

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.sixrr.guiceyidea.GuiceyIDEABundle;
import com.sixrr.guiceyidea.utils.MutationUtils;
import org.jetbrains.annotations.NotNull;

class DeleteBindingFix implements LocalQuickFix{
    private static final Logger LOGGER = Logger.getInstance("RedundantToProviderBindingInspection");

    @NotNull
    public String getName(){
        return GuiceyIDEABundle.message("delete.binding");
    }

    @NotNull
    public String getFamilyName(){
        return "";
    }

    @SuppressWarnings({"HardCodedStringLiteral"})
    public void applyFix(@NotNull Project project, ProblemDescriptor descriptor){
        final PsiMethodCallExpression call =
                PsiTreeUtil.getParentOfType(descriptor.getPsiElement(), PsiMethodCallExpression.class);
        assert call != null;
        final PsiExpression qualifier = call.getMethodExpression().getQualifierExpression();
        try{
            assert qualifier != null;
            MutationUtils.replaceExpression(qualifier.getText(), call);
        } catch(IncorrectOperationException e){
            LOGGER.error(e);
        }
    }
}
