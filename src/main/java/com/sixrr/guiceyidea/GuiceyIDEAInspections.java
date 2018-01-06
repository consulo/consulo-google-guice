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

package com.sixrr.guiceyidea;

import com.intellij.codeInspection.InspectionToolProvider;
import com.sixrr.guiceyidea.inspections.*;

public class GuiceyIDEAInspections implements InspectionToolProvider {
    public Class[] getInspectionClasses(){
        return new Class[]{
                SessionScopedInjectsRequestScopedInspection.class,
                SingletonInjectsScopedInspection.class,
                InvalidImplementedByInspection.class,
                InvalidProvidedByInspection.class,
                UninstantiableImplementedByClassInspection.class,
                UninstantiableBindingInspection.class,
                UninstantiableProvidedByClassInspection.class,
                PointlessBindingInspection.class,
                ConflictingAnnotationsInspection.class,
                RedundantToBindingInspection.class,
                RedundantToProviderBindingInspection.class,
                RedundantScopeBindingInspection.class,
                MultipleInjectedConstructorsForClassInspection.class,
                MultipleBindingAnnotationsInspection.class,
                InvalidRequestParametersInspection.class,
                BindingAnnotationWithoutInjectInspection.class,
                InterceptionAnnotationWithoutRuntimeRetentionInspection.class,
                UnnecessaryStaticInjectionInspection.class,
        };
    }
}
