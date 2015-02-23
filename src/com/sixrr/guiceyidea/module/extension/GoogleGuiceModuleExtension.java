package com.sixrr.guiceyidea.module.extension;

import org.consulo.module.extension.impl.ModuleExtensionImpl;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.roots.ModuleRootLayer;

/**
 * @author VISTALL
 * @since 17:33/28.05.13
 */
public class GoogleGuiceModuleExtension extends ModuleExtensionImpl<GoogleGuiceModuleExtension>
{
	public GoogleGuiceModuleExtension(@NotNull String id, @NotNull ModuleRootLayer moduleRootLayer)
	{
		super(id, moduleRootLayer);
	}
}
