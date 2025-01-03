package consulo.google.guice.module.extension;

import consulo.annotation.access.RequiredReadAction;
import consulo.module.content.layer.ModuleRootLayer;
import consulo.module.content.layer.extension.ModuleExtensionBase;

import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 17:33/28.05.13
 */
public class GoogleGuiceModuleExtension extends ModuleExtensionBase<GoogleGuiceModuleExtension>
{
	protected boolean myUseJSR330;

	public GoogleGuiceModuleExtension(@Nonnull String id, @Nonnull ModuleRootLayer moduleRootLayer)
	{
		super(id, moduleRootLayer);
	}

	@RequiredReadAction
	@Override
	public void commit(@Nonnull GoogleGuiceModuleExtension mutableModuleExtension)
	{
		super.commit(mutableModuleExtension);
		myUseJSR330 = mutableModuleExtension.isUseJSR330();
	}

	public boolean isUseJSR330()
	{
		return myUseJSR330;
	}
}
