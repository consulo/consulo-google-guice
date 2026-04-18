/**
 * @author VISTALL
 * @since 22/01/2023
 */
module consulo.google.guice
{
	requires consulo.code.editor.api;
	requires consulo.document.api;
	requires consulo.file.template.api;
	requires consulo.ide.api;
	requires consulo.language.api;
	requires consulo.language.code.style.api;
	requires consulo.language.editor.api;
	requires consulo.localize.api;
	requires consulo.logging.api;
	requires consulo.module.api;
	requires consulo.module.content.api;
	requires consulo.project.api;
	requires consulo.ui.api;
	requires consulo.ui.ex.api;
	requires consulo.ui.ex.awt.api;

	requires consulo.java.analysis.api;

	requires com.intellij.properties;

	requires consulo.google.guice.api;

	// TODO remove in future
	requires java.desktop;

	// TODO remove this opens after actions migrated to new extensions model
	exports com.sixrr.guiceyidea.actions to consulo.component.impl;
}
