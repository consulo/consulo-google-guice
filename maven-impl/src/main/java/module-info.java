/**
 * @author VISTALL
 * @since 22/01/2023
 */
module consulo.google.guice.maven.impl
{
	requires consulo.module.api;
	requires consulo.module.content.api;
	requires consulo.util.lang;

	requires consulo.google.guice.api;
	requires org.jetbrains.idea.maven;
	requires org.jetbrains.idea.maven.server.common;
}
