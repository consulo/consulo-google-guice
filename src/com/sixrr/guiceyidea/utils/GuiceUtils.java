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

import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import consulo.google.guice.util.GoogleGuiceAnnotationUtil;

public class GuiceUtils {
  private GuiceUtils() {
  }

  public static boolean isInstantiable(PsiClass referentClass) {
    if (referentClass.isInterface() ||
        referentClass.isEnum() ||
        referentClass.isAnnotationType() ||
        referentClass.hasModifierProperty(PsiModifier.ABSTRACT)) {
      return false;
    }
    final PsiMethod[] constructors = referentClass.getConstructors();
    if (constructors.length == 0) {
      return true;
    }
    for (PsiMethod constructor : constructors) {
      if (GoogleGuiceAnnotationUtil.isAnnotatedByInject(constructor, true)) {
        return true;
      }

      if (constructor.getParameterList().getParametersCount() == 0) {
        return true;
      }
    }
    return false;
  }

  public static boolean provides(PsiClass providerClass, PsiClass providedClass) {
    if (!ClassUtils.isSubclass(providerClass, "com.google.inject.Provider")) {
      return false;
    }
    final PsiMethod[] methods = providerClass.findMethodsByName("get", true);
    for (PsiMethod method : methods) {
      if (method.getParameterList().getParametersCount() != 0) {
        continue;
      }
      final PsiType returnType = method.getReturnType();
      if (!(returnType instanceof PsiClassType)) {
        return false;
      }
      final PsiClass classProvided = ((PsiClassType)returnType).resolve();
      return providedClass.equals(classProvided);
    }
    return false;
  }

  public static boolean isBinding(PsiElement element) {
    if (!(element instanceof PsiMethodCallExpression)) {
      return false;
    }
    final PsiMethodCallExpression containingCall = PsiTreeUtil.getParentOfType(element, PsiMethodCallExpression.class, true);
    if (containingCall != null) {
      return false;
    }

    final PsiMethodCallExpression callExpression = (PsiMethodCallExpression)element;
    final PsiMethod method = callExpression.resolveMethod();
    if (method == null) {
      return false;
    }
    final PsiClass containingClass = method.getContainingClass();
    return containingClass != null && ClassUtils.isSubclass(containingClass, "com.google.inject.binder.ScopedBindingBuilder");
  }

  public static PsiClass findImplementedClassForBinding(PsiMethodCallExpression call) {
    final PsiReferenceExpression methodExpression = call.getMethodExpression();
    final String methodName = methodExpression.getReferenceName();
    if ("bind".equals(methodName)) {
      final PsiExpression[] args = call.getArgumentList().getExpressions();
      if (!(args.length == 1 && args[0] instanceof PsiClassObjectAccessExpression)) {
        return null;
      }
      final PsiType classType = ((PsiClassObjectAccessExpression)args[0]).getOperand().getType();
      if (classType instanceof PsiClassType) {
        return ((PsiClassType)classType).resolve();
      }
      return null;
    }
    final PsiElement qualifier = methodExpression.getQualifier();
    if (qualifier == null || !(qualifier instanceof PsiMethodCallExpression)) {
      return null;
    }
    return findImplementedClassForBinding((PsiMethodCallExpression)qualifier);
  }

  public static PsiClass findImplementingClassForBinding(PsiMethodCallExpression call) {
    final PsiReferenceExpression methodExpression = call.getMethodExpression();
    final String methodName = methodExpression.getReferenceName();
    if ("to".equals(methodName)) {
      final PsiExpression[] args = call.getArgumentList().getExpressions();
      if (!(args.length == 1 && args[0] instanceof PsiClassObjectAccessExpression)) {
        return null;
      }
      final PsiType classType = ((PsiClassObjectAccessExpression)args[0]).getOperand().getType();
      if (classType instanceof PsiClassType) {
        return ((PsiClassType)classType).resolve();
      }
      return null;
    }
    final PsiElement qualifier = methodExpression.getQualifier();
    if (qualifier == null || !(qualifier instanceof PsiMethodCallExpression)) {
      return null;
    }
    return findImplementingClassForBinding((PsiMethodCallExpression)qualifier);
  }

  public static PsiClass findProvidingClassForBinding(PsiMethodCallExpression call) {
    final PsiReferenceExpression methodExpression = call.getMethodExpression();
    final String methodName = methodExpression.getReferenceName();
    if ("toProvider".equals(methodName)) {
      final PsiExpression[] args = call.getArgumentList().getExpressions();
      if (!(args.length == 1 && args[0] instanceof PsiClassObjectAccessExpression)) {
        return null;
      }
      final PsiType classType = ((PsiClassObjectAccessExpression)args[0]).getOperand().getType();
      if (classType instanceof PsiClassType) {
        return ((PsiClassType)classType).resolve();
      }
      return null;
    }
    final PsiElement qualifier = methodExpression.getQualifier();
    if (qualifier == null || !(qualifier instanceof PsiMethodCallExpression)) {
      return null;
    }
    return findProvidingClassForBinding((PsiMethodCallExpression)qualifier);
  }

  public static PsiExpression findScopeForBinding(PsiMethodCallExpression call) {
    final PsiReferenceExpression methodExpression = call.getMethodExpression();
    final String methodName = methodExpression.getReferenceName();
    if ("in".equals(methodName)) {
      final PsiExpression[] args = call.getArgumentList().getExpressions();
      if (args.length == 1) {
        return args[0];
      }
      return null;
    }
    final PsiElement qualifier = methodExpression.getQualifier();
    if (qualifier == null || !(qualifier instanceof PsiMethodCallExpression)) {
      return null;
    }
    return findScopeForBinding((PsiMethodCallExpression)qualifier);
  }

  public static PsiMethodCallExpression findScopeCallForBinding(PsiMethodCallExpression call) {
    final PsiReferenceExpression methodExpression = call.getMethodExpression();
    final String methodName = methodExpression.getReferenceName();
    if ("in".equals(methodName)) {
      final PsiExpression[] args = call.getArgumentList().getExpressions();
      if (args.length == 1) {
        return call;
      }
      return null;
    }
    final PsiElement qualifier = methodExpression.getQualifier();
    if (qualifier == null || !(qualifier instanceof PsiMethodCallExpression)) {
      return null;
    }
    return findScopeCallForBinding((PsiMethodCallExpression)qualifier);
  }

  public static PsiMethodCallExpression findBaseCallForBinding(PsiMethodCallExpression call) {
    final PsiReferenceExpression methodExpression = call.getMethodExpression();
    final String methodName = methodExpression.getReferenceName();
    if ("bind".equals(methodName)) {
      final PsiExpression[] args = call.getArgumentList().getExpressions();
      if (args.length == 1) {
        return call;
      }
      return null;
    }
    final PsiElement qualifier = methodExpression.getQualifier();
    if (qualifier == null || !(qualifier instanceof PsiMethodCallExpression)) {
      return null;
    }
    return findScopeCallForBinding((PsiMethodCallExpression)qualifier);
  }

  public static PsiMethodCallExpression findProvidingCallForBinding(PsiMethodCallExpression call) {
    final PsiReferenceExpression methodExpression = call.getMethodExpression();
    final String methodName = methodExpression.getReferenceName();
    if ("toProvider".equals(methodName)) {
      final PsiExpression[] args = call.getArgumentList().getExpressions();
      if (args.length == 1) {
        return call;
      }
      return null;
    }
    final PsiElement qualifier = methodExpression.getQualifier();
    if (qualifier == null || !(qualifier instanceof PsiMethodCallExpression)) {
      return null;
    }
    return findProvidingCallForBinding((PsiMethodCallExpression)qualifier);
  }

  public static PsiMethodCallExpression findBindingCallForBinding(PsiMethodCallExpression call) {
    final PsiReferenceExpression methodExpression = call.getMethodExpression();
    final String methodName = methodExpression.getReferenceName();
    if ("to".equals(methodName)) {
      final PsiExpression[] args = call.getArgumentList().getExpressions();
      if (args.length == 1) {
        return call;
      }
      return null;
    }
    final PsiElement qualifier = methodExpression.getQualifier();
    if (qualifier == null || !(qualifier instanceof PsiMethodCallExpression)) {
      return null;
    }
    return findBindingCallForBinding((PsiMethodCallExpression)qualifier);
  }

  public static String getScopeAnnotationForScopeExpression(PsiExpression arg) {
    if (!(arg instanceof PsiReferenceExpression)) {
      return null;
    }
    final PsiReferenceExpression referenceExpression = (PsiReferenceExpression)arg;
    final PsiElement referent = referenceExpression.resolve();
    if (referent == null || !(referent instanceof PsiField)) {
      return null;
    }
    final PsiField field = (PsiField)referent;
    final String className = field.getContainingClass().getQualifiedName();
    final String fieldName = field.getName();
    if ("SINGLETON".equals(fieldName) && "com.google.inject.Scopes".equals(className)) {
      return "com.google.inject.Singleton";
    }
    if ("REQUEST".equals(fieldName) && "com.google.inject.servlet.ServletScopes".equals(className)) {
      return "com.google.inject.servlet.RequestScoped";
    }
    if ("SESSION".equals(fieldName) && "com.google.inject.servlet.ServletScopes".equals(className)) {
      return "com.google.inject.servlet.SessionScoped";
    }
    return null;
  }
}
