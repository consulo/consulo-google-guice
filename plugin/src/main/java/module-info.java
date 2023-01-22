/**
 * @author VISTALL
 * @since 22/01/2023
 */
module consulo.google.guice
{
	requires consulo.ide.api;

	requires consulo.java.analysis.api;

	requires com.intellij.properties;

	requires consulo.google.guice.api;

	// TODO remove in future
	requires java.desktop;

	// TODO remove this opens after actions migrated to new extensions model
	exports com.sixrr.guiceyidea.actions to consulo.component.impl;
}