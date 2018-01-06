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

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethodCallExpression;
import com.sixrr.guiceyidea.utils.GuiceUtils;

public class MoveProviderBindingToClassPredicate implements PsiElementPredicate{
    public boolean satisfiedBy(PsiElement element){
        if(!GuiceUtils.isBinding(element)){
            return false;
        }
        final PsiMethodCallExpression call = (PsiMethodCallExpression) element;

        if(GuiceUtils.findProvidingClassForBinding(call) == null){
            return false;
        }
        final PsiClass implementedClass = GuiceUtils.findImplementedClassForBinding(call);
        if(implementedClass == null){
            return false;
        }
        if(AnnotationUtil.isAnnotated(implementedClass, "com.google.inject.ImplementedBy", true) ||
                AnnotationUtil.isAnnotated(implementedClass, "com.google.inject.ProvidedBy", true)){
            return false;
        }
        return true;
    }
}