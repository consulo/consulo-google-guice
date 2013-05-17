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

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.sixrr.guiceyidea.GuiceyIDEABundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public abstract class BaseInspection extends LocalInspectionTool{

    private String m_shortName = null;
    @NonNls
    private static final String INSPECTION = "Inspection";

    @Nls
    @NotNull
    public String getGroupDisplayName(){
        return "Guice Inspections";
    }

    @NotNull
    public String getShortName(){
        if(m_shortName != null){
            return m_shortName;
        }
        final Class<? extends BaseInspection> aClass = getClass();
        final String name = aClass.getName();
        m_shortName = name.substring(name.lastIndexOf('.') + 1,
                name.length() - INSPECTION.length());
        return m_shortName;
    }

    @NotNull
    protected abstract String buildErrorString(Object... infos);

    protected boolean buildQuickFixesOnlyForOnTheFlyErrors(){
        return false;
    }

    private String getPropertyPrefixForInspection(){
        final String shortName = getShortName();
        return getPrefix(shortName);
    }

    public static String getPrefix(String shortName){
        final int length = shortName.length();
        final StringBuffer buffer = new StringBuffer(length + 10);
        buffer.append(Character.toLowerCase(shortName.charAt(0)));
        for(int i = 1; i < length; i++){
            final char c = shortName.charAt(i);
            if(Character.isUpperCase(c)){
                buffer.append('.');
                buffer.append(Character.toLowerCase(c));
            } else{
                buffer.append(c);
            }
        }
        return buffer.toString();
    }

    public String getDisplayName(){
        @NonNls final String displayNameSuffix = ".display.name";
        return GuiceyIDEABundle.message(
                getPropertyPrefixForInspection() + displayNameSuffix);
    }

    public boolean isEnabledByDefault(){
        return true;
    }

    public abstract BaseInspectionVisitor buildVisitor();

    @NotNull
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder,
                                          boolean isOnTheFly){
        final BaseInspectionVisitor visitor = buildVisitor();

        visitor.setProblemsHolder(holder);
        visitor.setOnTheFly(isOnTheFly);
        visitor.setInspection(this);
        return visitor;
    }

    public LocalQuickFix buildFix(PsiElement location, Object[] infos){
        return null;
    }
}