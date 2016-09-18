package consulo.google.guice.module.extension;

import org.jetbrains.annotations.NotNull;
import consulo.module.extension.MutableModuleExtension;
import consulo.roots.ModuleRootLayer;

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
