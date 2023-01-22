package consulo.google.guice.module.extension;

import consulo.annotation.component.ExtensionImpl;
import consulo.localize.LocalizeValue;
import consulo.module.content.layer.ModuleExtensionProvider;
import consulo.module.content.layer.ModuleRootLayer;
import consulo.module.extension.ModuleExtension;
import consulo.module.extension.MutableModuleExtension;
import consulo.platform.base.icon.PlatformIconGroup;
import consulo.ui.image.Image;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author VISTALL
 * @since 22/01/2023
 */
@ExtensionImpl
public class GoogleGuiceModuleExtensionProvider implements ModuleExtensionProvider<GoogleGuiceModuleExtension>
{
	@Nonnull
	@Override
	public String getId()
	{
		return "google-guice";
	}

	@Nullable
	@Override
	public String getParentId()
	{
		return "java";
	}

	@Nonnull
	@Override
	public LocalizeValue getName()
	{
		return LocalizeValue.localizeTODO("Google Guice");
	}

	@Nonnull
	@Override
	public Image getIcon()
	{
		return PlatformIconGroup.providersGoogle();
	}

	@Nonnull
	@Override
	public ModuleExtension<GoogleGuiceModuleExtension> createImmutableExtension(@Nonnull ModuleRootLayer moduleRootLayer)
	{
		return new GoogleGuiceModuleExtension(getId(), moduleRootLayer);
	}

	@Nonnull
	@Override
	public MutableModuleExtension<GoogleGuiceModuleExtension> createMutableExtension(@Nonnull ModuleRootLayer moduleRootLayer)
	{
		return new GoogleGuiceMutableModuleExtension(getId(), moduleRootLayer);
	}
}
