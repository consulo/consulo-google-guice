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

package com.sixrr.guiceyidea.reference;

import com.intellij.java.language.psi.PsiField;
import com.intellij.java.language.psi.PsiLiteralExpression;
import com.intellij.java.language.psi.infos.CandidateInfo;
import com.intellij.lang.properties.IProperty;
import com.intellij.lang.properties.PropertiesUtil;
import com.sixrr.guiceyidea.utils.MutationUtils;
import consulo.document.util.TextRange;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiPolyVariantReference;
import consulo.language.psi.ResolveResult;
import consulo.language.util.IncorrectOperationException;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class NamedReference implements PsiPolyVariantReference
{
    private final PsiLiteralExpression element;

    NamedReference(PsiLiteralExpression element){
        this.element = element;
    }

    public PsiElement getElement(){
        return element;
    }

    public TextRange getRangeInElement(){
        return new TextRange(0, element.getTextLength());
    }

    @Nullable
    public PsiElement resolve(){
        return null;
    }

    @Nonnull
    public ResolveResult[] multiResolve(boolean incompleteCode){
        final String text = element.getText();
        final String strippedText = text.substring(1, text.length() - 1);
      // TODO[yole] figure out what was meant here. this never works and never could have worked.
        final List<IProperty> properties =
                PropertiesUtil.findAllProperties(element.getProject(), null, strippedText);
        final Set<IProperty> propertySet = new HashSet<IProperty>(properties);
        final ResolveResult[] out = new ResolveResult[propertySet.size()];
        int i = 0;
        for(IProperty property : propertySet){
            out[i] = new CandidateInfo(property.getPsiElement(), null);
            i++;
        }

        return out;
    }

    public String getCanonicalText(){
        return element.getText();
    }

    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException
	{
        return MutationUtils.replaceExpression('\"' + newElementName + '\"', element);
    }

    public PsiElement bindToElement(@Nonnull PsiElement element) throws IncorrectOperationException{
        return null;
    }

    public boolean isReferenceTo(PsiElement element){
        if(element == null){
            return false;
        }
        if(!(element instanceof PsiField)){
            return false;
        }

        return element.equals(resolve());
    }

    public Object[] getVariants(){
        // TODO[yole] figure out what was meant here. this never works and never could have worked.
        final List<IProperty> properties = PropertiesUtil.findAllProperties(element.getProject(), null, null);
        final List<Object> out = new ArrayList<Object>();
        for(IProperty property : properties){
            out.add(property.getName());
        }

        return out.toArray(new Object[out.size()]);
    }

    public boolean isSoft(){
        return false;
    }
}
