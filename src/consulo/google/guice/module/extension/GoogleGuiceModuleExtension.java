package consulo.google.guice.module.extension;

import org.jetbrains.annotations.NotNull;
import consulo.extension.impl.ModuleExtensionImpl;
import consulo.roots.ModuleRootLayer;

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
