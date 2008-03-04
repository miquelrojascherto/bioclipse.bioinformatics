package net.bioclipse.biojava.ui.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import net.bioclipse.biojava.business.BiojavaManager;
import net.bioclipse.biojava.business.IBiojavaManager;
import net.bioclipse.biojava.domain.BiojavaSequence;
import net.bioclipse.biojava.ui.views.SequenceContentProvider;
import net.bioclipse.core.domain.IAASequence;
import net.bioclipse.core.domain.IDNASequence;
import net.bioclipse.core.domain.ISequence;

import org.eclipse.core.internal.resources.File;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.junit.Before;
import org.junit.Test;

import testData.TestData;
import static org.junit.Assert.*;

public class SequenceContentProviderTest {

	
	@Before
	public void setup() {
		

		//Introduce the allowed formats
		try{
			Class.forName("org.biojavax.bio.seq.io.EMBLFormat");
			Class.forName("org.biojavax.bio.seq.io.FastaFormat");
			Class.forName("org.biojavax.bio.seq.io.GenbankFormat");
			Class.forName("org.biojavax.bio.seq.io.INSDseqFormat");
			Class.forName("org.biojavax.bio.seq.io.EMBLxmlFormat");
			Class.forName("org.biojavax.bio.seq.io.UniProtFormat");
			Class.forName("org.biojavax.bio.seq.io.UniProtXMLFormat");
			Class.forName("org.biojavax.bio.seq.io.RichSequenceFormat");
		}
		catch(ClassNotFoundException e){
			System.out.println("Class not found" + e);
		}		
	}

	@Test
	public void testGetChildren() throws CoreException, IOException {

		//Create WS with data
		Map<String, IFile> files =createWorkspaceWithData();

		//Create ContentProvider to test
		SequenceContentProvider provider=new SequenceContentProvider();

		//New file to test
		//================
		IFile gbkFile=files.get("sequence.gbk");
		assertNotNull(gbkFile);
		
		Object[] obj=provider.getChildren(gbkFile);
		assertNotNull(obj);
		assertEquals(1, obj.length);
		assertTrue(obj[0] instanceof IDNASequence);
//		IDNASequence seq=(IDNASequence)obj[0];

		//New file to test
		//================
		gbkFile=files.get("sequence_fail.gbk");
		assertNotNull(gbkFile);
		
		obj=provider.getChildren(gbkFile);
		assertNotNull(obj);
		assertEquals(0, obj.length);

		//New file to test, contains 2 sequences
		//================
		IFile fastaFile=files.get("sequence3.fasta");
		assertNotNull(fastaFile);
		
		obj=provider.getChildren(fastaFile);
		assertNotNull(obj);
		assertEquals(2, obj.length);
		assertTrue(obj[0] instanceof IAASequence);
		assertTrue(obj[1] instanceof IAASequence);

		int a=0;
		
//		//Verify children are what we expect
//		for (Object o : obj){
//			if (o instanceof ISequence) {
//				ISequence seq = (ISequence) o;
//				System.out.println("seq: " + seq.getPlainSequence());
//			}
//		}

	}



	/**
	 * Supporting method, not a Test
	 * @return
	 * @throws CoreException
	 * @throws IOException
	 */
	private Map<String, IFile> createWorkspaceWithData() throws CoreException, IOException {
		//Get WS root
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

		//Create the project
		IProject project = root.getProject("UnitTestProject");
		
		IProgressMonitor dummyMonitor=new IProgressMonitor(){

			public void beginTask(String name, int totalWork) {
			}

			public void done() {
			}

			public void internalWorked(double work) {
			}

			public boolean isCanceled() {
				return false;
			}

			public void setCanceled(boolean value) {
			}

			public void setTaskName(String name) {
			}

			public void subTask(String name) {
			}

			public void worked(int work) {
			}

		};
		
		project.create(dummyMonitor);
		
		//Open project
		project.open(dummyMonitor);
		IPath projectPath = project.getFullPath();

		//Set up return map
		Map<String, IFile> files=new HashMap<String, IFile>();

		//Create files
		IPath gbkPath= projectPath.append("sequence.gbk");
		IFile gbkFile = root.getFile(gbkPath);
		InputStream gbkIS = getClass().getResourceAsStream("/net/bioclipse/biojava/ui/test/resources/sequence.gbk");
		gbkFile.create(gbkIS,true,dummyMonitor);
		gbkIS.close();		
		files.put("sequence.gbk", gbkFile);

		IPath gbkFailPath= projectPath.append("sequence_fail.gbk");
		IFile gbkFailFile = root.getFile(gbkFailPath);
		InputStream gbkFailIS = getClass().getResourceAsStream("/net/bioclipse/biojava/ui/test/resources/sequence_fail.gbk");
		gbkFailFile.create(gbkFailIS,true,dummyMonitor);
		gbkFailIS.close();		
		files.put("sequence_fail.gbk", gbkFailFile);

		IPath fastaPath= projectPath.append("sequence3.fasta");
		IFile fastaFile = root.getFile(fastaPath);
		InputStream fastaIS = getClass().getResourceAsStream("/net/bioclipse/biojava/ui/test/resources/sequence3.fasta");
		fastaFile.create(fastaIS,true,dummyMonitor);
		fastaIS.close();		
		files.put("sequence3.fasta", fastaFile);

		//TODO: add more files
		return files;
	}

	public static IFile findFileResourceByLocation (String FileLocation)
	{
		IPath ResourcePath = new Path(FileLocation);
		if (!ResourcePath.isAbsolute())
		{
			//this methods does not support relative paths
			return null;
		}
		else
		{
			IFile[] Files =
				getWorkspaceRoot().findFilesForLocation(ResourcePath);
			return (Files.length > 0) ? Files[0] : null;
		}
	}

	private static IWorkspaceRoot getWorkspaceRoot() {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		return root;
	}

	public static IFile[] findFileResourcesByLocation (String FileLocation)
	{
		IPath ResourcePath = new Path(FileLocation);
		if (!ResourcePath.isAbsolute())
			//this methods does not support relative paths
			return new IFile[0];
		else
			return getWorkspaceRoot().findFilesForLocation(ResourcePath);
	}



}
