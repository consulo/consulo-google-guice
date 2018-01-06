package consulo.google.guice.module.extension;

import org.jetbrains.annotations.NotNull;
import consulo.annotations.RequiredReadAction;
import consulo.module.extension.impl.ModuleExtensionImpl;
import consulo.roots.ModuleRootLayer;

/**
 * @author VISTALL
 * @since 17:33/28.05.13
 */
public class GoogleGuiceModuleExtension extends ModuleExtensionImpl<GoogleGuiceModuleExtension>
{
	protected boolean myUseJSR330;

	public GoogleGuiceModuleExtension(@NotNull String id, @NotNull ModuleRootLayer moduleRootLayer)
	{
		super(id, moduleRootLayer);
	}

	@RequiredReadAction
	@Override
	public void commit(@NotNull GoogleGuiceModuleExtension mutableModuleExtension)
	{
		super.commit(mutableModuleExtension);
		myUseJSR330 = mutableModuleExtension.isUseJSR330();
	}

	public boolean isUseJSR330()
	{
		return myUseJSR330;
	}
}
