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

package com.sixrr.guiceyidea.intentions;

import consulo.codeEditor.Editor;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.localize.LocalizeValue;
import consulo.project.Project;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

public abstract class MutablyNamedIntention extends Intention {
    private LocalizeValue text = LocalizeValue.of();

    protected abstract LocalizeValue getTextForElement(@Nullable PsiElement element);

    @Nonnull
    public LocalizeValue getText() {
        if (text == null) {
            text = getTextForElement(null);
        }
        return text;
    }

    public boolean isAvailable(@Nonnull Project project, Editor editor, PsiFile file) {
        final PsiElement element = findMatchingElement(file, editor);
        if (element != null) {
            text = getTextForElement(element);
        }
        return element != null;
    }
}
