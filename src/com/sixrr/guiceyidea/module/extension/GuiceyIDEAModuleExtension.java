package com.sixrr.guiceyidea.module.extension;

import org.consulo.module.extension.impl.ModuleExtensionImpl;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.module.Module;

/**
 * @author VISTALL
 * @since 17:33/28.05.13
 */
public class GuiceyIDEAModuleExtension extends ModuleExtensionImpl<GuiceyIDEAModuleExtension>
{
	public GuiceyIDEAModuleExtension(@NotNull String id, @NotNull Module module)
	{
		super(id, module);
	}
}
