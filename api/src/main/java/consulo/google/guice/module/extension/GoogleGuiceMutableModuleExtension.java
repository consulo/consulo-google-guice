package consulo.google.guice.module.extension;

import consulo.annotation.access.RequiredReadAction;
import consulo.disposer.Disposable;
import consulo.module.content.layer.ModuleRootLayer;
import consulo.module.extension.MutableModuleExtension;
import consulo.ui.CheckBox;
import consulo.ui.Component;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.layout.VerticalLayout;
import org.jdom.Element;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 17:33/28.05.13
 */
public class GoogleGuiceMutableModuleExtension extends GoogleGuiceModuleExtension implements MutableModuleExtension<GoogleGuiceModuleExtension>
{
	public GoogleGuiceMutableModuleExtension(@Nonnull String id, @Nonnull ModuleRootLayer moduleRootLayer)
	{
		super(id, moduleRootLayer);
	}

	public void setUseJSR330(boolean useJSR330)
	{
		myUseJSR330 = useJSR330;
	}

	@RequiredUIAccess
	@Nullable
	@Override
	public Component createConfigurationComponent(@Nonnull Disposable disposable, @Nonnull Runnable runnable)
	{
		VerticalLayout vertical = VerticalLayout.create();
		CheckBox useJSRBox = CheckBox.create("Prefer 'javax.inject' annotations?", myUseJSR330);
		useJSRBox.addValueListener(valueEvent -> setUseJSR330(valueEvent.getValue()));
		vertical.add(useJSRBox);
		return vertical;
	}

	@RequiredReadAction
	@Override
	protected void loadStateImpl(@Nonnull Element element)
	{
		super.loadStateImpl(element);
		myUseJSR330 = Boolean.parseBoolean(element.getAttributeValue("useJSR330", "false"));
	}

	@Override
	protected void getStateImpl(@Nonnull Element element)
	{
		super.getStateImpl(element);
		if(myUseJSR330)
		{
			element.setAttribute("useJSR330", "true");
		}
	}

	@Override
	public void setEnabled(boolean b)
	{
		myIsEnabled = b;
	}

	@Override
	public boolean isModified(@Nonnull GoogleGuiceModuleExtension extension)
	{
		return myIsEnabled != extension.isEnabled() || myUseJSR330 != extension.isUseJSR330();
	}
}
