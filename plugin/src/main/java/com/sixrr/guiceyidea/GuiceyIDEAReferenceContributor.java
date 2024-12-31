package com.sixrr.guiceyidea;

import com.intellij.java.language.JavaLanguage;
import com.intellij.java.language.patterns.PsiJavaPatterns;
import com.intellij.java.language.psi.PsiLiteralExpression;
import com.sixrr.guiceyidea.reference.NamedReferenceProvider;
import consulo.language.Language;
import consulo.language.pattern.PlatformPatterns;
import consulo.language.psi.PsiReferenceContributor;
import consulo.language.psi.PsiReferenceRegistrar;

import jakarta.annotation.Nonnull;

/**
 * @author yole
 */
public class GuiceyIDEAReferenceContributor extends PsiReferenceContributor
{
	public void registerReferenceProviders(final PsiReferenceRegistrar registrar)
	{
		registrar.registerReferenceProvider(
				PlatformPatterns.psiElement(PsiLiteralExpression.class).
						withSuperParent(3, PsiJavaPatterns.psiAnnotation().qName("com.google.inject.name.Named")), new NamedReferenceProvider());
	}

	@Nonnull
	@Override
	public Language getLanguage()
	{
		return JavaLanguage.INSTANCE;
	}
}
