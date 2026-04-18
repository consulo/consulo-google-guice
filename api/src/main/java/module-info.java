/**
 * @author VISTALL
 * @since 22/01/2023
 */
module consulo.google.guice.api
{
	requires consulo.disposer.api;
	requires consulo.localize.api;
	requires consulo.module.api;
	requires consulo.module.content.api;
	requires consulo.ui.api;

	exports consulo.google.guice.icon;
	exports consulo.google.guice.module.extension;
}
