package consulo.google.guice.module.extension;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jdom.Element;
import consulo.annotations.RequiredReadAction;
import consulo.module.extension.MutableModuleExtension;
import consulo.roots.ModuleRootLayer;
import consulo.ui.CheckBox;
import consulo.ui.Component;
import consulo.ui.RequiredUIAccess;
import consulo.ui.layout.VerticalLayout;

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
	public Component createConfigurationComponent(@Nonnull Runnable updateOnCheck)
	{
		VerticalLayout vertical = VerticalLayout.create();
		CheckBox useJSRBox = CheckBox.create("Use 'javax.inject' annotations?", myUseJSR330);
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
