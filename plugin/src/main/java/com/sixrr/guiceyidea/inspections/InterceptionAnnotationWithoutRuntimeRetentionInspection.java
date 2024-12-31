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

package com.sixrr.guiceyidea.inspections;

import com.intellij.java.language.codeInsight.AnnotationUtil;
import com.intellij.java.language.psi.*;
import com.sixrr.guiceyidea.GuiceyIDEABundle;
import consulo.annotation.component.ExtensionImpl;

import jakarta.annotation.Nonnull;
import java.util.Collections;

@ExtensionImpl
public class InterceptionAnnotationWithoutRuntimeRetentionInspection extends BaseInspection {

    @Nonnull
    protected String buildErrorString(Object... infos){
        return GuiceyIDEABundle.message("interception.annotation.without.runtime.retention.problem.descriptor");
    }

    public BaseInspectionVisitor buildVisitor(){
        return new Visitor();
    }

    private static class Visitor extends BaseInspectionVisitor{
        public void visitMethodCallExpression(PsiMethodCallExpression expression){
            super.visitMethodCallExpression(expression);
            final PsiReferenceExpression methodExpression = expression.getMethodExpression();
            final String name = methodExpression.getReferenceName();
            if(!"annotatedWith".equals(name)){
                return;
            }
            final PsiExpression[] args = expression.getArgumentList().getExpressions();
            if(args.length != 1){
                return;
            }
            if(!(args[0] instanceof PsiClassObjectAccessExpression)){
                return;
            }
            final PsiMethod method = expression.resolveMethod();
            if(method == null){
                return;
            }
            final PsiClass containingClass = method.getContainingClass();
            if(!"com.google.inject.matcher.Matchers".equals(containingClass.getQualifiedName())){
                return;
            }
            final PsiClassObjectAccessExpression arg = (PsiClassObjectAccessExpression) args[0];
            final PsiTypeElement operandType = arg.getOperand();
            final PsiClassType operandAnnotationType = (PsiClassType) operandType.getType();
            final PsiClass operantAnnotation = operandAnnotationType.resolve();
            final PsiAnnotation retentionAnnotation =
                    AnnotationUtil.findAnnotation(operantAnnotation, Collections.singleton("java.lang.annotation.Retention"));
            if(retentionAnnotation == null || !retentionAnnotation.getText().contains("RUNTIME")){
                registerError(operandType);
            }
        }
    }
}