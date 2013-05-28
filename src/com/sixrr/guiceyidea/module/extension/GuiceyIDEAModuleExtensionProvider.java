package com.sixrr.guiceyidea.module.extension;

import javax.swing.Icon;

import org.consulo.module.extension.ModuleExtensionProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.module.Module;
import com.sixrr.guiceyidea.GuiceyIDEAIcons;

/**
 * @author VISTALL
 * @since 17:35/28.05.13
 */
public class GuiceyIDEAModuleExtensionProvider implements ModuleExtensionProvider<GuiceyIDEAModuleExtension, GuiceyIDEAMutableModuleExtension>
{
	@Nullable
	@Override
	public Icon getIcon()
	{
		return GuiceyIDEAIcons.GUICE_LOGO;
	}

	@NotNull
	@Override
	public String getName()
	{
		return "Google Guice";
	}

	@NotNull
	@Override
	public Class<GuiceyIDEAModuleExtension> getImmutableClass()
	{
		return GuiceyIDEAModuleExtension.class;
	}

	@NotNull
	@Override
	public GuiceyIDEAModuleExtension createImmutable(@NotNull String s, @NotNull Module module)
	{
		return new GuiceyIDEAModuleExtension(s, module);
	}

	@NotNull
	@Override
	public GuiceyIDEAMutableModuleExtension createMutable(@NotNull String s, @NotNull Module module, @NotNull GuiceyIDEAModuleExtension moduleExtension)
	{
		return new GuiceyIDEAMutableModuleExtension(s, module, moduleExtension);
	}
}