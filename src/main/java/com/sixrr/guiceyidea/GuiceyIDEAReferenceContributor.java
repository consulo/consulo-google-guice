package com.sixrr.guiceyidea;

import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiJavaPatterns;
import com.sixrr.guiceyidea.reference.NamedReferenceProvider;

/**
 * @author yole
 */
public class GuiceyIDEAReferenceContributor extends PsiReferenceContributor {
  public void registerReferenceProviders(final PsiReferenceRegistrar registrar) {
    registrar.registerReferenceProvider(
        PlatformPatterns.psiElement(PsiLiteralExpression.class).
            withSuperParent(3, PsiJavaPatterns.psiAnnotation().qName("com.google.inject.name.Named")), new NamedReferenceProvider());
  }
}
