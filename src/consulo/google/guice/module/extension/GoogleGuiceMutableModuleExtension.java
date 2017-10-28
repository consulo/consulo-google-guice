package consulo.google.guice.module.extension;

import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import consulo.annotations.RequiredReadAction;
import consulo.module.extension.MutableModuleExtension;
import consulo.roots.ModuleRootLayer;
import consulo.ui.CheckBox;
import consulo.ui.Component;
import consulo.ui.RequiredUIAccess;
import consulo.ui.VerticalLayout;

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

	public void setUseJSR330(boolean useJSR330)
	{
		myUseJSR330 = useJSR330;
	}

	@RequiredUIAccess
	@Nullable
	@Override
	public Component createConfigurationComponent(@NotNull Runnable updateOnCheck)
	{
		VerticalLayout vertical = VerticalLayout.create();
		CheckBox useJSRBox = CheckBox.create("Use 'javax.inject' annotations?", myUseJSR330);
		useJSRBox.addValueListener(valueEvent -> setUseJSR330(valueEvent.getValue()));
		vertical.add(useJSRBox);
		return vertical;
	}

	@RequiredReadAction
	@Override
	protected void loadStateImpl(@NotNull Element element)
	{
		super.loadStateImpl(element);
		myUseJSR330 = Boolean.parseBoolean(element.getAttributeValue("useJSR330", "false"));
	}

	@Override
	protected void getStateImpl(@NotNull Element element)
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
	public boolean isModified(@NotNull GoogleGuiceModuleExtension extension)
	{
		return myIsEnabled != extension.isEnabled() || myUseJSR330 != extension.isUseJSR330();
	}
}
