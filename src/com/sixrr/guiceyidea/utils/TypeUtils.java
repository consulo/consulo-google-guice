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

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiClass;

class TypeUtils{

    private TypeUtils(){
        super();
    }

    public static boolean expressionHasType(
            @NonNls @NotNull String typeName,
            @Nullable PsiExpression expression){
        if(expression == null){
            return false;
        }
        final PsiType type = expression.getType();
        return typeEquals(typeName, type);
    }

    private static boolean typeEquals(@NonNls @NotNull String typeName,
                                      @Nullable PsiType targetType){
        if(targetType == null){
            return false;
        }
        return targetType.equalsToText(typeName);
    }

    public static boolean isJavaLangObject(@Nullable PsiType targetType){
        return typeEquals("java.lang.Object", targetType);
    }

    public static boolean isJavaLangString(@Nullable PsiType targetType){
        return typeEquals("java.lang.String", targetType);
    }

    public static boolean expressionHasTypeOrSubtype(
            @NonNls @NotNull String typeName,
            @Nullable PsiExpression expression){
        if(expression == null){
            return false;
        }
        final PsiType type = expression.getType();
        if(type == null){
            return false;
        }
        if(!(type instanceof PsiClassType)){
            return false;
        }
        final PsiClassType classType = (PsiClassType) type;
        final PsiClass aClass = classType.resolve();
        if(aClass == null){
            return false;
        }
        return ClassUtils.isSubclass(aClass, typeName);
    }
}