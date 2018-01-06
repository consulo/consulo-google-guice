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

package com.sixrr.guiceyidea.utils;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationParameterList;
import com.intellij.psi.PsiNameValuePair;

public class AnnotationUtils{
    public static PsiElement findDefaultValue(PsiAnnotation annotation){
        final PsiAnnotationParameterList parameters = annotation.getParameterList();
        final PsiNameValuePair[] pairs = parameters.getAttributes();
        for(PsiNameValuePair pair : pairs){
            final String name = pair.getName();
            if("value".equals(name) || name == null){
                return pair.getValue();
            }
        }
        return null;
    }
}
