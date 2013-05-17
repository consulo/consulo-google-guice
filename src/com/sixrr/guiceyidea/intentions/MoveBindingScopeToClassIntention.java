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

package com.sixrr.guiceyidea.intentions;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.util.IncorrectOperationException;
import com.sixrr.guiceyidea.GuiceyIDEABundle;
import com.sixrr.guiceyidea.utils.GuiceUtils;
import com.sixrr.guiceyidea.utils.MutationUtils;
import org.jetbrains.annotations.NotNull;

public class MoveBindingScopeToClassIntention extends Intention{
    @NotNull
    public String getText(){
        return GuiceyIDEABundle.message("move.binding.scope.to.class.text");
    }

    @NotNull
    public String getFamilyName(){
        return GuiceyIDEABundle.message("move.binding.scope.to.class.family.name");
    }

    @NotNull
    protected PsiElementPredicate getElementPredicate(){
        return new MoveBindingScopeToClassPredicate();
    }

    protected void processIntention(@NotNull PsiElement element) throws IncorrectOperationException{
        final PsiMethodCallExpression originalCall = (PsiMethodCallExpression) element;
        final PsiClass bindingClass = GuiceUtils.findImplementingClassForBinding(originalCall);
        final PsiMethodCallExpression scopeCall = GuiceUtils.findScopeCallForBinding(originalCall);
        final PsiExpression arg = scopeCall.getArgumentList().getExpressions()[0];
        final String scopeAnnotation = GuiceUtils.getScopeAnnotationForScopeExpression(arg);
        MutationUtils.addAnnotation(bindingClass, "@" + scopeAnnotation);
        final PsiExpression qualifier = scopeCall.getMethodExpression().getQualifierExpression();

        assert qualifier != null;
        MutationUtils.replaceExpression(qualifier.getText(), scopeCall);
    }
}
