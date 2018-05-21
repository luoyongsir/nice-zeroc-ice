package com.nice.ice;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.ExecTask;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Environment;
import org.apache.tools.ant.types.FileSet;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ice 文件转成 Java
 *
 * @author Luo Yong
 * @date 2017-03-12
 */
public class Slice2JavaTask extends Task {

	private String outputDirString = null;
	private List<FileSet> fileSets = new LinkedList<>();
	private String iceHome;

	public void setOutputdir(File outputDir) {
		if (!outputDir.exists()) {
			outputDir.mkdirs();
		}
		this.outputDirString = outputDir.toString();
		if (this.outputDirString.indexOf(32) != -1) {
			this.outputDirString = ('"' + this.outputDirString + '"');
		}
	}

	public FileSet createFileset() {
		FileSet localFileSet = new FileSet();
		this.fileSets.add(localFileSet);
		return localFileSet;
	}

	private static final String LD_LIBRARY_PATH = "LD_LIBRARY_PATH";

	private void addLdLibraryPath(ExecTask execTask) {
		boolean bool = getProject().getProperties().containsKey("ice.src.dist");
		if (this.iceHome != null) {
			String str2 = null;
			String str3 = null;
			String str4 = new File(this.iceHome + File.separator + "lib").toString();
			String str5 = null;
			String osName = System.getProperty("os.name");
			String str7;
			if ("Mac OS X".equals(osName)) {
				str2 = "DYLD_LIBRARY_PATH";
			} else if ("AIX".equals(osName)) {
				str2 = "LIBPATH";
			} else if ("HP-UX".equals(osName)) {
				str2 = "SHLIB_PATH";
				str3 = LD_LIBRARY_PATH;
				if (bool) {
					str5 = str4;
				} else {
					str5 = new File(this.iceHome + File.separator + "lib" + File.separator + "pa20_64").toString();
				}
			} else if (!osName.startsWith("Windows")) {
				if ("SunOS".equals(osName)) {
					str2 = LD_LIBRARY_PATH;
					str3 = "LD_LIBRARY_PATH_64";
					str7 = System.getProperty("os.arch");
					if (bool) {
						str5 = str4;
					} else if ("x86".equals(str7)) {
						str5 = new File(this.iceHome + File.separator + "lib" + File.separator + "amd64").toString();
					} else {
						str5 = new File(this.iceHome + File.separator + "lib" + File.separator + "sparcv9").toString();
					}
				} else {
					str2 = LD_LIBRARY_PATH;
					str3 = LD_LIBRARY_PATH;
					if (bool) {
						str5 = str4;
					} else {
						str5 = new File(this.iceHome + File.separator + "lib64").toString();
					}
				}
			}
			Environment.Variable variable;
			if (str2 != null) {
				if (str2.equals(str3)) {
					str4 = str4 + File.pathSeparator + str5;
				}
				str7 = getEnvironment(str2);
				if (str7 != null) {
					str4 = str4 + File.pathSeparator + str7;
				}
				variable = new Environment.Variable();
				variable.setKey(str2);
				variable.setValue(str4);
				execTask.addEnv(variable);
			}
			if ((str3 != null) && (!str3.equals(str2))) {
				str7 = getEnvironment(str3);
				if (str7 != null) {
					str5 = str5 + File.pathSeparator + str7;
				}
				variable = new Environment.Variable();
				variable.setKey(str3);
				variable.setValue(str5);
				execTask.addEnv(variable);
			}
		}
	}

	/**
	 * 初始化 iceHome
	 */
	private void initIceHome() {
		if (this.iceHome == null) {
			if (getProject().getProperties().containsKey("ice.home")) {
				this.iceHome = ((String) getProject().getProperties().get("ice.home"));
			} else {
				this.iceHome = getEnvironment("ICE_HOME");
			}
		}
	}

	private String getEnvironment(String paramString) {
		Map<String, String> variables = Execute.getEnvironmentVariables();
		for (Map.Entry<String, String> entry : variables.entrySet()) {
			if (entry.getKey().equals(paramString)) {
				return entry.getValue();
			}
		}
		return null;
	}

	/**
	 * 执行slice2java命令
	 * @throws BuildException if something goes wrong with the build.
	 */
	@Override
	public void execute() throws BuildException {
		if (this.fileSets.isEmpty()) {
			throw new BuildException("No fileset specified");
		}
		initIceHome();
		Iterator<FileSet> fileSetIterator = this.fileSets.iterator();
		FileSet fileSet;
		DirectoryScanner directoryScanner;
		String[] strings;
		File file;
		// 拼接命令行
		StringBuilder line = new StringBuilder(512);
		line.append(" --output-dir ").append(this.outputDirString);
		while (fileSetIterator.hasNext()) {
			fileSet = fileSetIterator.next();
			directoryScanner = fileSet.getDirectoryScanner(getProject());
			directoryScanner.scan();
			strings = directoryScanner.getIncludedFiles();
			for (String str2 : strings) {
				file = new File(fileSet.getDir(getProject()), str2);
				line.append(" ");
				String s = file.toString();
				if (s.indexOf(' ') != -1) {
					line.append('"');
					line.append(s);
					line.append('"');
				} else {
					line.append(s);
				}
			}
		}
		ExecTask execTask = (ExecTask) getProject().createTask("exec");
		addLdLibraryPath(execTask);
		execTask.setFailonerror(true);
		Commandline.Argument argument = execTask.createArg();
		argument.setLine(line.toString());
		execTask.setExecutable(getDefaultTranslator());
		execTask.execute();
		try {
			replace();
		} catch (IOException e) {
			Logger.getGlobal().log(Level.WARNING, "replace __current error:", e);
		}
	}

	/**
	 * 查找 *Operations.java 文件<br />
	 * 替换 "__current" 为 "current"
	 */
	private void replace() throws IOException {
		// 递归查找文件，并加入fileList
		List<File> fileList = new ArrayList<>();
		findFiles(this.outputDirString, fileList);
		final StringBuilder bud = new StringBuilder(1024);
		for (File file : fileList) {
			bud.setLength(0);
			// 读取替换
			try (RandomAccessFile accessFile = new RandomAccessFile(file, "rw")) {
				String line;
				while ((line = accessFile.readLine()) != null) {
					if (line.contains("__current")) {
						line = line.replace("__current", "current");
					}
					bud.append(line);
					bud.append("\n");
				}
				accessFile.setLength(0);
				accessFile.writeBytes(bud.toString());
			}
		}
	}

	/**
	 * 递归查找文件<br/>
	 * @param baseDirName 查找的文件夹路径
	 * @param fileList 查找到的文件集合
	 */
	private static void findFiles(String baseDirName, List<File> fileList) {
		String tempPath;
		File baseDir = new File(baseDirName);
		// 判断目录是否存在
		if (baseDir.exists() && baseDir.isDirectory()) {
			String[] dirList = baseDir.list();
			if (dirList == null) {
				return;
			}
			for (String s : dirList) {
				String dirName = baseDirName + File.separatorChar + s;
				File readFile = new File(dirName);
				if (!readFile.isDirectory()) {
					tempPath = readFile.getAbsolutePath();
					if (tempPath.endsWith("Operations.java")) {
						// 匹配成功，将文件路径添加到结果集
						fileList.add(readFile);
					}
				} else if (readFile.isDirectory()) {
					findFiles(dirName, fileList);
				}
			}
		}
	}

	private String getDefaultTranslator() {
		if (this.iceHome != null) {
			return new File(this.iceHome + File.separator + "bin" + File.separator + "slice2java").toString();
		}
		return "slice2java";
	}
}
