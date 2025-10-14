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

import com.intellij.java.language.JavaLanguage;
import consulo.annotation.access.RequiredReadAction;
import consulo.google.guice.localize.GoogleGuiceLocalize;
import consulo.google.guice.module.extension.GoogleGuiceModuleExtension;
import consulo.language.Language;
import consulo.language.editor.inspection.LocalInspectionTool;
import consulo.language.editor.inspection.LocalQuickFix;
import consulo.language.editor.inspection.ProblemsHolder;
import consulo.language.editor.rawHighlight.HighlightDisplayLevel;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiElementVisitor;
import consulo.language.util.ModuleUtilCore;
import consulo.localize.LocalizeValue;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

public abstract class BaseInspection extends LocalInspectionTool {
    private String m_shortName = null;

    private static final String INSPECTION = "Inspection";

    @Nullable
    @Override
    public Language getLanguage() {
        return JavaLanguage.INSTANCE;
    }

    @Nonnull
    @Override
    public HighlightDisplayLevel getDefaultLevel() {
        return HighlightDisplayLevel.WARNING;
    }

    @Override
    @Nonnull
    public LocalizeValue getGroupDisplayName() {
        return GoogleGuiceLocalize.guiceInspectionGroupName();
    }

    @Override
    @Nonnull
    public String getShortName() {
        if (m_shortName != null) {
            return m_shortName;
        }
        final Class<? extends BaseInspection> aClass = getClass();
        final String name = aClass.getName();
        m_shortName = name.substring(name.lastIndexOf('.') + 1, name.length() - INSPECTION.length());
        return m_shortName;
    }

    @Nonnull
    protected abstract String buildErrorString(Object... infos);

    protected boolean buildQuickFixesOnlyForOnTheFlyErrors() {
        return false;
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }

    public abstract BaseInspectionVisitor buildVisitor();

    @Override
    @Nonnull
    @RequiredReadAction
    public PsiElementVisitor buildVisitor(@Nonnull ProblemsHolder holder, boolean isOnTheFly) {
        GoogleGuiceModuleExtension extension = ModuleUtilCore.getExtension(holder.getFile(), GoogleGuiceModuleExtension.class);
        if (extension == null) {
            return new PsiElementVisitor() {
            };
        }

        final BaseInspectionVisitor visitor = buildVisitor();

        visitor.setProblemsHolder(holder);
        visitor.setOnTheFly(isOnTheFly);
        visitor.setInspection(this);
        return visitor;
    }

    public LocalQuickFix buildFix(PsiElement location, Object[] infos) {
        return null;
    }
}