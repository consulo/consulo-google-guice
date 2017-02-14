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
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiMethodCallExpression;
import com.sixrr.guiceyidea.utils.GuiceUtils;
import consulo.google.guice.util.GoogleGuiceAnnotationUtil;

public class MoveBindingScopeToClassPredicate implements PsiElementPredicate{
    public boolean satisfiedBy(PsiElement element){
        if(!GuiceUtils.isBinding(element)){
            return false;
        }
        final PsiMethodCallExpression call = (PsiMethodCallExpression) element;
        if(GuiceUtils.findScopeForBinding(call) == null){
            return false;
        }
        final PsiMethodCallExpression scopeCall = GuiceUtils.findScopeCallForBinding(call);
        final PsiExpression arg = scopeCall.getArgumentList().getExpressions()[0];
        final String scopeAnnotation = GuiceUtils.getScopeAnnotationForScopeExpression(arg);
        if(scopeAnnotation == null){
            return false;
        }
        final PsiClass implementingClass = GuiceUtils.findImplementingClassForBinding(call);
        if(implementingClass == null){
            return false;
        }
        if(GoogleGuiceAnnotationUtil.isAnnotatedBySingleton(implementingClass, true) ||
                AnnotationUtil.isAnnotated(implementingClass, "com.google.inject.servlet.RequestScoped", false) ||
                AnnotationUtil.isAnnotated(implementingClass, "com.google.inject.servlet.SessionScoped", false)){
            return false;
        }
        return true;
    }
}
