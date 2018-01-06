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

import com.intellij.psi.*;
import com.intellij.util.IncorrectOperationException;
import com.sixrr.guiceyidea.GuiceyIDEABundle;
import com.sixrr.guiceyidea.utils.MutationUtils;
import org.jetbrains.annotations.NotNull;

public class ToggleInjectionRequiredIntention extends MutablyNamedIntention{
    protected String getTextForElement(PsiElement element){
        final PsiAnnotation annotation = (PsiAnnotation) element;
        final PsiAnnotationMemberValue value = annotation.findAttributeValue("optional");
        if(value == null){
            return GuiceyIDEABundle.message("make.injection.optional");
        }
        if(value instanceof PsiLiteralExpression){
            if(value.getText().equals("false")){
                return GuiceyIDEABundle.message("make.injection.optional");
            } else{
                return GuiceyIDEABundle.message("make.injection.mandatory");
            }
        }
        return GuiceyIDEABundle.message("toggle.required");
    }

    @NotNull
    public String getFamilyName(){
        return GuiceyIDEABundle.message("toggle.injection.required.family.name");
    }

    @NotNull
    protected PsiElementPredicate getElementPredicate(){
        return new ToggleInjectionRequiredPredicate();
    }

    protected void processIntention(@NotNull PsiElement element) throws IncorrectOperationException{
        final PsiAnnotation annotation = (PsiAnnotation) element;
        final PsiAnnotationMemberValue value = annotation.findAttributeValue("optional");
        if(value == null){
            MutationUtils.replaceAnnotation(annotation, "@com.google.inject.Inject(optional = true)");
        } else if(value instanceof PsiLiteralExpression){
            if(value.getText().equals("false")){
                MutationUtils.replaceAnnotation(annotation, "@com.google.inject.Inject(optional = true)");
            } else{
                MutationUtils.replaceAnnotation(annotation, "@com.google.inject.Inject");
            }
        } else{
            MutationUtils.negateExpression((PsiExpression) value);
        }
    }
}