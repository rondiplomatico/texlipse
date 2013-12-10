package net.sourceforge.texlipse.handlers;

import net.sourceforge.texlipse.TexlipsePlugin;
import net.sourceforge.texlipse.editor.TexEditor;
import net.sourceforge.texlipse.properties.TexlipseProperties;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

public class PartialBuildCommandDefaultHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		// Toggle button in each case
		boolean active = !HandlerUtil.toggleCommandState(event.getCommand());

		// Retrieve current project of 
		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		if (window != null && window.getActivePage() != null) {
			IEditorPart editor = window.getActivePage().getActiveEditor();
			if (editor != null && editor instanceof TexEditor) {

				IProject project = ((TexEditor) editor).getProject();

				TexlipseProperties.setProjectProperty(project,
						TexlipseProperties.PARTIAL_BUILD_PROPERTY,
						active ? "true" : null);

				if (!active)
					cleanPartialBuildFiles(project);

//				try {
//					project.build(IncrementalProjectBuilder.FULL_BUILD , null);
//				} catch (CoreException e) {
//					e.printStackTrace();
//				}
			}
		}
		return null;
	}

	private void cleanPartialBuildFiles(IProject project) {
		// delete all tempPartial0000 files
		try {
			IResource[] res;
			IFolder projectOutputDir = TexlipseProperties
					.getProjectOutputDir(project);
			if (projectOutputDir != null)
				res = projectOutputDir.members();
			else
				res = project.members();
			for (int i = 0; i < res.length; i++) {
				if (res[i].getName().startsWith("tempPartial0000"))
					res[i].delete(true, null);
			}

			IFolder projectTempDir = TexlipseProperties
					.getProjectTempDir(project);
			if (projectTempDir != null && projectTempDir.exists())
				res = projectTempDir.members();
			else
				res = project.members();

			for (int i = 0; i < res.length; i++) {
				if (res[i].getName().startsWith("tempPartial0000"))
					res[i].delete(true, null);
			}
			IContainer sourceDir = TexlipseProperties
					.getProjectSourceDir(project);
			res = sourceDir.members();
			for (int i = 0; i < res.length; i++) {
				if (res[i].getName().startsWith("tempPartial0000"))
					res[i].delete(true, null);
			}

		} catch (CoreException e) {
			TexlipsePlugin.log("Error while deleting temp files", e);
		}
	}

}
