package consulo.google.guice.maven.importer;

import java.util.List;
import java.util.Map;

import org.jetbrains.idea.maven.importing.MavenModifiableModelsProvider;
import org.jetbrains.idea.maven.importing.MavenRootModelAdapter;
import org.jetbrains.idea.maven.model.MavenArtifact;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectChanges;
import org.jetbrains.idea.maven.project.MavenProjectsProcessorTask;
import org.jetbrains.idea.maven.project.MavenProjectsTree;
import com.intellij.openapi.module.Module;
import com.intellij.util.text.VersionComparatorUtil;
import consulo.google.guice.module.extension.GoogleGuiceModuleExtension;
import consulo.google.guice.module.extension.GoogleGuiceMutableModuleExtension;
import consulo.maven.importing.MavenImporterFromDependency;

/**
 * @author VISTALL
 * @since 14-Feb-17
 */
public class GoogleGuiceMavenImporter extends MavenImporterFromDependency
{
	public GoogleGuiceMavenImporter()
	{
		super("com.google.inject", "guice");
	}

	@Override
	public void preProcess(Module module, MavenProject mavenProject, MavenProjectChanges mavenProjectChanges, MavenModifiableModelsProvider mavenModifiableModelsProvider)
	{
	}

	@Override
	public void process(MavenModifiableModelsProvider mavenModifiableModelsProvider,
			Module module,
			MavenRootModelAdapter mavenRootModelAdapter,
			MavenProjectsTree mavenProjectsTree,
			MavenProject mavenProject,
			MavenProjectChanges mavenProjectChanges,
			Map<MavenProject, String> map,
			List<MavenProjectsProcessorTask> list)
	{
		GoogleGuiceMutableModuleExtension extension = (GoogleGuiceMutableModuleExtension) enableModuleExtension(module, mavenModifiableModelsProvider, GoogleGuiceModuleExtension.class);

		List<MavenArtifact> artifactList = mavenProject.findDependencies("com.google.inject", "guice");
		for(MavenArtifact mavenArtifact : artifactList)
		{
			String version = mavenArtifact.getVersion();
			if(VersionComparatorUtil.compare(version, "3.0") >= 0)
			{
				extension.setUseJSR330(true);
			}
		}
	}
}
