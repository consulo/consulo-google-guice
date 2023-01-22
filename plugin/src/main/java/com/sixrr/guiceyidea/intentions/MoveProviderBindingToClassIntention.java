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

import javax.annotation.Nonnull;

@ExtensionImpl
@IntentionMetaData(ignoreId = "google.guice.move.provider.binding.to.class", categories = {
		"Java",
		"Google Guice"
}, fileExtensions = "java")
public class MoveProviderBindingToClassIntention extends Intention{
    @Nonnull
    public String getText(){
        return GuiceyIDEABundle.message("move.provider.binding.to.class.text");
    }

    @Nonnull
    public String getFamilyName(){
        return GuiceyIDEABundle.message("move.provider.binding.to.class.family.name");
    }

    @Nonnull
    protected PsiElementPredicate getElementPredicate(){
        return new MoveProviderBindingToClassPredicate();
    }

    protected void processIntention(@Nonnull PsiElement element) throws IncorrectOperationException
	{
        final PsiMethodCallExpression originalCall = (PsiMethodCallExpression) element;
        final PsiClass providerClass = GuiceUtils.findProvidingClassForBinding(originalCall);
        final PsiClass implementedClass = GuiceUtils.findImplementedClassForBinding(originalCall);
        MutationUtils.addAnnotation(implementedClass, "@com.google.inject.ProvidedBy(" + providerClass.getQualifiedName() + ".class)");
        final PsiMethodCallExpression bindingCall = GuiceUtils.findProvidingCallForBinding(originalCall);
        final PsiExpression qualifier = bindingCall.getMethodExpression().getQualifierExpression();

        assert qualifier != null;
        MutationUtils.replaceExpression(qualifier.getText(), bindingCall);
    }
}