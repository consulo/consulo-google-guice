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
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.IncorrectOperationException;

public class MutationUtils{
    private MutationUtils(){
        super();
    }

    public static void negateExpression(PsiExpression exp)
            throws IncorrectOperationException{
        final PsiManager mgr = exp.getManager();
        final PsiJavaParserFacade facade = JavaPsiFacade.getInstance(exp.getProject()).getParserFacade();

        PsiExpression expressionToReplace = exp;
        @NonNls final String expString;
        @NonNls final String newExpressionText = exp.getText();
        if("true".equals(newExpressionText)){
            expressionToReplace = exp;
            expString = "false";
        } else if("false".equals(newExpressionText)){
            expressionToReplace = exp;
            expString = "true";
        } else if(BoolUtils.isNegated(exp)){
            expressionToReplace = BoolUtils.findNegation(exp);
            expString = newExpressionText;
        } else if(BoolUtils.isNegation(exp)){
            expressionToReplace = exp;
            expString = BoolUtils.getNegated(exp).getText();
        } else if(ComparisonUtils.isComparison(exp)){
            final PsiBinaryExpression binaryExpression =
                    (PsiBinaryExpression) exp;
            final PsiJavaToken sign = binaryExpression.getOperationSign();
            final String operator = sign.getText();
            final String negatedComparison =
                    ComparisonUtils.getNegatedComparison(operator);
            final PsiExpression lhs = binaryExpression.getLOperand();
            final PsiExpression rhs = binaryExpression.getROperand();
            assert rhs != null;
            expString = lhs.getText() + negatedComparison + rhs.getText();
        } else{
            if(ParenthesesUtils.getPrecendence(exp) >
                    ParenthesesUtils.PREFIX_PRECEDENCE){
                expString = "!(" + newExpressionText + ')';
            } else{
                expString = '!' + newExpressionText;
            }
        }
        final PsiExpression newCall =
                facade.createExpressionFromText(expString, null);
        final PsiElement insertedElement = expressionToReplace.replace(newCall);
        final CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(mgr.getProject());
        codeStyleManager.reformat(insertedElement);
    }

    public static void replaceType(String newExpression,
                                   PsiTypeElement typeElement)
            throws IncorrectOperationException{
        final PsiManager mgr = typeElement.getManager();
        final Project project = mgr.getProject();
        final PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
        final PsiType newType =
                factory.createTypeFromText(newExpression, null);
        final PsiTypeElement newTypeElement = factory.createTypeElement(newType);
        final PsiElement insertedElement = typeElement.replace(newTypeElement);
        final CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(mgr.getProject());
        final PsiElement shortenedElement = JavaCodeStyleManager.getInstance(project).shortenClassReferences(insertedElement);
        codeStyleManager.reformat(shortenedElement);
    }

    public static PsiExpression replaceExpression(String newExpression,
                                                  PsiExpression exp)
            throws IncorrectOperationException{
        final PsiManager mgr = exp.getManager();
        final Project project = mgr.getProject();
        final PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
        final PsiExpression newCall =
                factory.createExpressionFromText(newExpression, null);
        final PsiElement insertedElement = exp.replace(newCall);
        final CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(mgr.getProject());
        final PsiElement shortenedElement = JavaCodeStyleManager.getInstance(project).shortenClassReferences(insertedElement);
        return (PsiExpression) codeStyleManager.reformat(shortenedElement);
    }

    public static void replaceExpressionIfValid(String newExpression,
                                                PsiExpression exp) throws IncorrectOperationException{
        final PsiManager mgr = exp.getManager();
        final Project project = mgr.getProject();
        final PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
        final PsiExpression newCall;
        try{
            newCall = factory.createExpressionFromText(newExpression, null);
        } catch(IncorrectOperationException e){
            return;
        }
        final PsiElement insertedElement = exp.replace(newCall);
        final CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(mgr.getProject());
        final PsiElement shortenedElement = JavaCodeStyleManager.getInstance(project).shortenClassReferences(insertedElement);
        codeStyleManager.reformat(shortenedElement);
    }

    public static void replaceReference(String className,
                                        PsiJavaCodeReferenceElement reference)
            throws IncorrectOperationException{
        final PsiManager mgr = reference.getManager();
        final Project project = mgr.getProject();
        final PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
        final GlobalSearchScope scope = GlobalSearchScope.allScope(project);

        final PsiJavaCodeReferenceElement newReference =
                factory.createReferenceElementByFQClassName(className, scope);
        final PsiElement insertedElement = reference.replace(newReference);
        JavaCodeStyleManager codeStyleManager = JavaCodeStyleManager.getInstance(project);
        final PsiElement shortenedElement = codeStyleManager.shortenClassReferences(insertedElement);
		CodeStyleManager.getInstance(mgr.getProject()).reformat(shortenedElement);
    }

    public static void replaceStatement(String newStatement,
                                        PsiStatement statement)
            throws IncorrectOperationException{
        final Project project = statement.getProject();
        final PsiManager mgr = PsiManager.getInstance(project);
        final PsiJavaParserFacade facade = JavaPsiFacade.getInstance(project).getParserFacade();
        final PsiStatement newCall =
                facade.createStatementFromText(newStatement, null);
        final PsiElement insertedElement = statement.replace(newCall);
        JavaCodeStyleManager codeStyleManager = JavaCodeStyleManager.getInstance(project);
        final PsiElement shortenedElement = codeStyleManager.shortenClassReferences(insertedElement);
		CodeStyleManager.getInstance(mgr.getProject()).reformat(shortenedElement);
    }

    public static void addAnnotation(PsiModifierListOwner owner, String annotation) throws IncorrectOperationException{
        final PsiModifierList modifiers = owner.getModifierList();
        final Project project = owner.getProject();
        final PsiElementFactory elementFactory = JavaPsiFacade.getInstance(project).getElementFactory();
        final PsiAnnotation newAnnotation = elementFactory.createAnnotationFromText(annotation, owner);
        assert modifiers != null;
        final PsiElement replacedAnnotation = modifiers.add(newAnnotation);
        JavaCodeStyleManager codeStyleManager = JavaCodeStyleManager.getInstance(project);
        codeStyleManager.shortenClassReferences(replacedAnnotation);
    }

    public static void replaceAnnotation(PsiAnnotation originalAnnotation, String replacementString) throws IncorrectOperationException{
        final Project project = originalAnnotation.getProject();
        final PsiManager manager = originalAnnotation.getManager();
        final PsiElementFactory elementFactory = JavaPsiFacade.getInstance(project).getElementFactory();
        final PsiAnnotation newAnnotation = elementFactory.createAnnotationFromText(replacementString, originalAnnotation);
        final PsiElement replacedAnnotation = originalAnnotation.replace(newAnnotation);
        JavaCodeStyleManager.getInstance(project).shortenClassReferences(replacedAnnotation);
    }
}
