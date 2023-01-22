package consulo.google.guice;

import consulo.annotation.component.ExtensionImpl;
import consulo.fileTemplate.FileTemplateContributor;
import consulo.fileTemplate.FileTemplateRegistrator;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 22/01/2023
 */
@ExtensionImpl
public class GoogleGuiceFileTemplateContributor implements FileTemplateContributor
{
	@Override
	public void register(@Nonnull FileTemplateRegistrator registrator)
	{
		registrator.registerInternalTemplate("Google Guice Module");
		registrator.registerInternalTemplate("Google Guice Method Interceptor");
		registrator.registerInternalTemplate("Google Guice Provider");
		registrator.registerInternalTemplate("Google Guice Binding Annotation");
		registrator.registerInternalTemplate("Google Guice Scope Annotation");
	}
}
