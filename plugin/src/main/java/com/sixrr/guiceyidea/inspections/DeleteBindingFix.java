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

import com.intellij.java.language.psi.PsiExpression;
import com.intellij.java.language.psi.PsiMethodCallExpression;
import com.sixrr.guiceyidea.utils.MutationUtils;
import consulo.google.guice.localize.GoogleGuiceLocalize;
import consulo.language.editor.inspection.LocalQuickFix;
import consulo.language.editor.inspection.ProblemDescriptor;
import consulo.language.psi.util.PsiTreeUtil;
import consulo.language.util.IncorrectOperationException;
import consulo.localize.LocalizeValue;
import consulo.logging.Logger;
import consulo.project.Project;
import jakarta.annotation.Nonnull;

class DeleteBindingFix implements LocalQuickFix {
    private static final Logger LOGGER = Logger.getInstance("RedundantToProviderBindingInspection");

    @Nonnull
    public LocalizeValue getName() {
        return GoogleGuiceLocalize.deleteBinding();
    }

    @SuppressWarnings({"HardCodedStringLiteral"})
    public void applyFix(@Nonnull Project project, ProblemDescriptor descriptor) {
        final PsiMethodCallExpression call =
            PsiTreeUtil.getParentOfType(descriptor.getPsiElement(), PsiMethodCallExpression.class);
        assert call != null;
        final PsiExpression qualifier = call.getMethodExpression().getQualifierExpression();
        try {
            assert qualifier != null;
            MutationUtils.replaceExpression(qualifier.getText(), call);
        }
        catch (IncorrectOperationException e) {
            LOGGER.error(e);
        }
    }
}
