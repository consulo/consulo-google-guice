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

import javax.annotation.Nonnull;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.sixrr.guiceyidea.GuiceyIDEABundle;
import consulo.google.guice.util.GoogleGuiceAnnotationUtil;

public class MultipleInjectedConstructorsForClassInspection extends BaseInspection{

    @Nonnull
    protected String buildErrorString(Object... infos){
        return GuiceyIDEABundle.message("multiple.injected.constructors.for.class.problem.descriptor");
    }

    public BaseInspectionVisitor buildVisitor(){
        return new Visitor();
    }

    private static class Visitor extends BaseInspectionVisitor{
        public void visitMethod(PsiMethod method){
            super.visitMethod(method);
            if(!method.isConstructor()){
                return;
            }
            final PsiClass containingClass = method.getContainingClass();
            final PsiMethod[] constructors = containingClass.getConstructors();
            if(constructors.length <= 1){
                return;
            }
            if(!GoogleGuiceAnnotationUtil.isAnnotatedByInject(method, true)){
                return;
            }

            int annotatedConstructorCount = 0;
            for(PsiMethod constructor : constructors){
                if(GoogleGuiceAnnotationUtil.isAnnotatedByInject(constructor, true)){
                    annotatedConstructorCount++;
                }
            }
            if(annotatedConstructorCount > 1){
                registerMethodError(method);
            }
        }
    }
}