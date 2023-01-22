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

import com.intellij.java.language.psi.PsiAnnotation;
import com.intellij.java.language.psi.PsiType;
import com.intellij.java.language.psi.PsiVariable;
import com.sixrr.guiceyidea.GuiceyIDEABundle;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.psi.util.PsiTreeUtil;

import javax.annotation.Nonnull;

@ExtensionImpl
public class InvalidRequestParametersInspection extends BaseInspection{

    @Nonnull
    protected String buildErrorString(Object... infos){
        return GuiceyIDEABundle.message("invalid.request.parameters.problem.descriptor");
    }

    public BaseInspectionVisitor buildVisitor(){
        return new Visitor();
    }

    private static class Visitor extends BaseInspectionVisitor{
        public void visitAnnotation(PsiAnnotation annotation){
            super.visitAnnotation(annotation);
            if(!"com.google.inject.servlet.RequestParameters".equals(annotation.getQualifiedName())){
                return;
            }
            final PsiVariable variable = PsiTreeUtil.getParentOfType(annotation, PsiVariable.class);
            if(variable == null){
                return;
            }
            final PsiType type = variable.getType();
            String typeText = type.getCanonicalText();
            typeText = typeText.replaceAll(" ", "");
            if(typeText.equals("java.util.Map<java.lang.String,java.lang.String[]>")){
                return;
            }
            registerError(annotation);
        }
    }
}