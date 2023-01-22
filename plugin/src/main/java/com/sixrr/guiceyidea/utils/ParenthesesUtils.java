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

import java.util.HashMap;
import java.util.Map;

public class ParenthesesUtils{
    private static final int PARENTHESIZED_EXPRESSION_PRECEDENCE = 0;
    private static final int LITERAL_PRECEDENCE = 0;
    private static final int METHOD_CALL_PRECEDENCE = 1;

    private static final int POSTFIX_PRECEDENCE = 2;
    public static final int PREFIX_PRECEDENCE = 3;
    private static final int TYPE_CAST_PRECEDENCE = 4;
    private static final int MULTIPLICATIVE_PRECEDENCE = 5;
    private static final int ADDITIVE_PRECEDENCE = 6;
    public static final int SHIFT_PRECEDENCE = 7;
    private static final int RELATIONAL_PRECEDENCE = 8;
    public static final int EQUALITY_PRECEDENCE = 9;

    private static final int BINARY_AND_PRECEDENCE = 10;
    private static final int BINARY_XOR_PRECEDENCE = 11;
    private static final int BINARY_OR_PRECEDENCE = 12;
    private static final int AND_PRECEDENCE = 13;
    public static final int OR_PRECEDENCE = 14;
    public static final int CONDITIONAL_EXPRESSION_EXPRESSION = 15;
    private static final int ASSIGNMENT_EXPRESSION_PRECEDENCE = 16;

    private static final Map<String, Integer> s_binaryOperatorPrecedence = new HashMap<String, Integer>(16);

    static{
        s_binaryOperatorPrecedence.put("+", ADDITIVE_PRECEDENCE);
        s_binaryOperatorPrecedence.put("-", ADDITIVE_PRECEDENCE);
        s_binaryOperatorPrecedence.put("*",
                MULTIPLICATIVE_PRECEDENCE);
        s_binaryOperatorPrecedence.put("/",
                MULTIPLICATIVE_PRECEDENCE);
        s_binaryOperatorPrecedence.put("%",
                MULTIPLICATIVE_PRECEDENCE);
        s_binaryOperatorPrecedence.put("&&", AND_PRECEDENCE);
        s_binaryOperatorPrecedence.put("||", OR_PRECEDENCE);
        s_binaryOperatorPrecedence.put("&", BINARY_AND_PRECEDENCE);
        s_binaryOperatorPrecedence.put("|", BINARY_OR_PRECEDENCE);
        s_binaryOperatorPrecedence.put("^", BINARY_XOR_PRECEDENCE);
        s_binaryOperatorPrecedence.put("<<", SHIFT_PRECEDENCE);
        s_binaryOperatorPrecedence.put(">>", SHIFT_PRECEDENCE);
        s_binaryOperatorPrecedence.put(">>>", SHIFT_PRECEDENCE);
        s_binaryOperatorPrecedence.put(">", RELATIONAL_PRECEDENCE);
        s_binaryOperatorPrecedence.put(">=",
                RELATIONAL_PRECEDENCE);
        s_binaryOperatorPrecedence.put("<", RELATIONAL_PRECEDENCE);
        s_binaryOperatorPrecedence.put("<=",
                RELATIONAL_PRECEDENCE);
        s_binaryOperatorPrecedence.put("==", EQUALITY_PRECEDENCE);
        s_binaryOperatorPrecedence.put("!=", EQUALITY_PRECEDENCE);
    }

    private ParenthesesUtils(){
        super();
    }

    public static PsiExpression stripParentheses(PsiExpression exp){
        PsiExpression parenthesized = exp;
        while(parenthesized instanceof PsiParenthesizedExpression){
            parenthesized = ((PsiParenthesizedExpression) parenthesized).getExpression();
        }
        return parenthesized;
    }

    @SuppressWarnings({"OverlyComplexMethod"})
    public static int getPrecendence(PsiExpression exp){

        if(exp instanceof PsiThisExpression ||
                exp instanceof PsiLiteralExpression ||
                exp instanceof PsiSuperExpression ||
                exp instanceof PsiReferenceExpression ||
                exp instanceof PsiClassObjectAccessExpression ||
                exp instanceof PsiArrayAccessExpression ||
                exp instanceof PsiArrayInitializerExpression){
            return LITERAL_PRECEDENCE;
        }
        if(exp instanceof PsiMethodCallExpression){
            return METHOD_CALL_PRECEDENCE;
        }
        if(exp instanceof PsiTypeCastExpression ||
                exp instanceof PsiNewExpression){
            return TYPE_CAST_PRECEDENCE;
        }
        if(exp instanceof PsiPrefixExpression){
            return PREFIX_PRECEDENCE;
        }
        if(exp instanceof PsiPostfixExpression){
            return POSTFIX_PRECEDENCE;
        }
        if(exp instanceof PsiBinaryExpression){
            final PsiBinaryExpression binaryExpression =
                    (PsiBinaryExpression) exp;
            final PsiJavaToken sign = binaryExpression.getOperationSign();
            return precedenceForBinaryOperator(sign);
        }
        if(exp instanceof PsiInstanceOfExpression){
            return RELATIONAL_PRECEDENCE;
        }
        if(exp instanceof PsiConditionalExpression){
            return CONDITIONAL_EXPRESSION_EXPRESSION;
        }
        if(exp instanceof PsiAssignmentExpression){
            return ASSIGNMENT_EXPRESSION_PRECEDENCE;
        }
        if(exp instanceof PsiParenthesizedExpression){
            return PARENTHESIZED_EXPRESSION_PRECEDENCE;
        }
        return -1;
    }

    private static int precedenceForBinaryOperator(PsiJavaToken sign){
        final String operator = sign.getText();
        return s_binaryOperatorPrecedence.get(operator);
    }
}
