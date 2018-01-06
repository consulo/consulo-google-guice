/*
 * Copyright 2013-2017 consulo.io
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

package consulo.google.guice.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.psi.PsiModifierListOwner;
import consulo.annotations.RequiredReadAction;
import consulo.google.guice.module.extension.GoogleGuiceModuleExtension;

/**
 * @author VISTALL
 * @since 14-Feb-17
 */
public class GoogleGuiceAnnotationUtil
{
	public static final String GUICE_INJECT = "com.google.inject.Inject";
	public static final String JSR330_INJECT = "javax.inject.Inject";

	public static final String GUICE_SINGLETON = "com.google.inject.Singleton";
	public static final String JSR330_SINGLETON = "javax.inject.Singleton";

	@RequiredReadAction
	public static boolean isAnnotatedByInject(@NotNull PsiModifierListOwner element, boolean checkHierarchy)
	{
		Collection<String> injectAnnotations = getInjectAnnotations(element);
		return !injectAnnotations.isEmpty() && AnnotationUtil.isAnnotated(element, injectAnnotations, checkHierarchy);
	}

	@RequiredReadAction
	public static Collection<String> getInjectAnnotations(@NotNull PsiModifierListOwner element)
	{
		GoogleGuiceModuleExtension extension = ModuleUtilCore.getExtension(element, GoogleGuiceModuleExtension.class);
		if(extension == null)
		{
			return Collections.emptyList();
		}

		List<String> anno = new ArrayList<>(2);
		if(extension.isUseJSR330())
		{
			anno.add(JSR330_INJECT);
		}
		anno.add(GUICE_INJECT);
		return anno;
	}

	@RequiredReadAction
	public static boolean isAnnotatedBySingleton(@NotNull PsiModifierListOwner element, boolean checkHierarchy)
	{
		Collection<String> injectAnnotations = getInjectAnnotations(element);
		return !injectAnnotations.isEmpty() && AnnotationUtil.isAnnotated(element, injectAnnotations, checkHierarchy);
	}

	@RequiredReadAction
	public static Collection<String> getSingletonAnnotations(@NotNull PsiModifierListOwner element)
	{
		GoogleGuiceModuleExtension extension = ModuleUtilCore.getExtension(element, GoogleGuiceModuleExtension.class);
		if(extension == null)
		{
			return Collections.emptyList();
		}

		List<String> anno = new ArrayList<>(2);
		if(extension.isUseJSR330())
		{
			anno.add(JSR330_SINGLETON);
		}
		anno.add(GUICE_SINGLETON);
		return anno;
	}
}
