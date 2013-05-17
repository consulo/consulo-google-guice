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

import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.InheritanceUtil;
import com.sixrr.guiceyidea.GuiceyIDEABundle;
import com.sixrr.guiceyidea.utils.AnnotationUtils;
import org.jetbrains.annotations.NotNull;

public class InvalidImplementedByInspection extends BaseInspection{

    @NotNull
    protected String buildErrorString(Object... infos){
        return GuiceyIDEABundle.message("invalid.implemented.by.problem.descriptor");
    }

    public BaseInspectionVisitor buildVisitor(){
        return new Visitor();
    }

    private static class Visitor extends BaseInspectionVisitor{
        public void visitAnnotation(PsiAnnotation annotation){
            super.visitAnnotation(annotation);
            final String qualifiedName = annotation.getQualifiedName();
            if(!"com.google.inject.ImplementedBy".equals(qualifiedName)){
                return;
            }
            final PsiClass containingClass = PsiTreeUtil.getParentOfType(annotation, PsiClass.class);
            if(containingClass == null){
                return;
            }
            final PsiElement defaultValue = AnnotationUtils.findDefaultValue(annotation);
            if(defaultValue == null){
                return;
            }
            if(!(defaultValue instanceof PsiClassObjectAccessExpression)){
                return;
            }
            final PsiTypeElement classTypeElement = ((PsiClassObjectAccessExpression) defaultValue).getOperand();
            final PsiType classType = classTypeElement.getType();
            if(!(classType instanceof PsiClassType)){
                return;
            }
            final PsiClass referentClass = ((PsiClassType) classType).resolve();
            if(referentClass == null){
                return;
            }
            if(InheritanceUtil.isInheritorOrSelf(referentClass, containingClass, true))
            {
                return;
            }
            registerError(classTypeElement);
        }
    }

}
