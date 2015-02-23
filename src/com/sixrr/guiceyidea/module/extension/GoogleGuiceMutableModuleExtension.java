package com.sixrr.guiceyidea.module.extension;

import javax.swing.JComponent;

import org.consulo.module.extension.MutableModuleExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootLayer;

/**
 * @author VISTALL
 * @since 17:33/28.05.13
 */
public class GoogleGuiceMutableModuleExtension extends GoogleGuiceModuleExtension implements MutableModuleExtension<GoogleGuiceModuleExtension>
{
	public GoogleGuiceMutableModuleExtension(@NotNull String id, @NotNull ModuleRootLayer moduleRootLayer)
	{
		super(id, moduleRootLayer);
	}

	@Nullable
	@Override
	public JComponent createConfigurablePanel(@Nullable Runnable runnable)
	{
		return null;
	}

	@Override
	public void setEnabled(boolean b)
	{
		myIsEnabled = b;
	}

	@Override
	public boolean isModified(@NotNull GoogleGuiceModuleExtension extension)
	{
		return myIsEnabled != extension.isEnabled();
	}
}
