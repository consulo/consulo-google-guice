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

import com.intellij.java.language.psi.*;

class BoolUtils {
    private BoolUtils(){
        super();
    }

    public static boolean isNegated(PsiExpression exp){
        PsiExpression ancestor = exp;
        while(ancestor.getParent() instanceof PsiParenthesizedExpression){
            ancestor = (PsiExpression) ancestor.getParent();
        }
        if(ancestor.getParent() instanceof PsiPrefixExpression){
            final PsiPrefixExpression prefixAncestor =
                    (PsiPrefixExpression) ancestor.getParent();
            final PsiJavaToken sign = prefixAncestor.getOperationSign();
            if(sign.getTokenType().equals(JavaTokenType.EXCL)){
                return true;
            }
        }
        return false;
    }

    public static PsiExpression findNegation(PsiExpression exp){
        PsiExpression ancestor = exp;
        while(ancestor.getParent() instanceof PsiParenthesizedExpression){
            ancestor = (PsiExpression) ancestor.getParent();
        }
        if(ancestor.getParent() instanceof PsiPrefixExpression){
            final PsiPrefixExpression prefixAncestor =
                    (PsiPrefixExpression) ancestor.getParent();
            final PsiJavaToken sign = prefixAncestor.getOperationSign();
            if(JavaTokenType.EXCL.equals(sign.getTokenType())){
                return prefixAncestor;
            }
        }
        return null;
    }

    public static boolean isNegation(PsiExpression exp){
        if(!(exp instanceof PsiPrefixExpression)){
            return false;
        }
        final PsiPrefixExpression prefixExp = (PsiPrefixExpression) exp;
        final PsiJavaToken sign = prefixExp.getOperationSign();
        return sign.getTokenType().equals(JavaTokenType.EXCL);
    }

    public static PsiExpression getNegated(PsiExpression exp){
        final PsiPrefixExpression prefixExp = (PsiPrefixExpression) exp;
        final PsiExpression operand = prefixExp.getOperand();
        return ParenthesesUtils.stripParentheses(operand);
    }
}
