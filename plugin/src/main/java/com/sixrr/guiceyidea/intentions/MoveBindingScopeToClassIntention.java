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

import com.intellij.java.language.psi.PsiClass;
import com.intellij.java.language.psi.PsiExpression;
import com.intellij.java.language.psi.PsiMethodCallExpression;
import com.sixrr.guiceyidea.GuiceyIDEABundle;
import com.sixrr.guiceyidea.utils.GuiceUtils;
import com.sixrr.guiceyidea.utils.MutationUtils;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.editor.intention.IntentionMetaData;
import consulo.language.psi.PsiElement;
import consulo.language.util.IncorrectOperationException;

import jakarta.annotation.Nonnull;

@ExtensionImpl
@IntentionMetaData(ignoreId = "google.guice.move.binding.scope", categories = {
		"Java",
		"Google Guice"
}, fileExtensions = "java")
public class MoveBindingScopeToClassIntention extends Intention{
    @Nonnull
    public String getText(){
        return GuiceyIDEABundle.message("move.binding.scope.to.class.text");
    }

    @Nonnull
    protected PsiElementPredicate getElementPredicate(){
        return new MoveBindingScopeToClassPredicate();
    }

    protected void processIntention(@Nonnull PsiElement element) throws IncorrectOperationException
	{
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
