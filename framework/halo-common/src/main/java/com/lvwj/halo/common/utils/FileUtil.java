package com.lvwj.halo.common.utils;

import lombok.SneakyThrows;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.PatternMatchUtils;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * 文件工具类
 */
public class FileUtil extends FileCopyUtils {

  public static class TrueFilter implements FileFilter, Serializable {

    private static final long serialVersionUID = -6420452043795072619L;

    public final static TrueFilter TRUE = new TrueFilter();

    @Override
    public boolean accept(File pathname) {
      return true;
    }
  }

  /**
   * 扫描目录下的文件
   *
   * @param path 路径
   * @return 文件集合
   */
  public static List<File> list(String path) {
    File file = new File(path);
    return list(file, TrueFilter.TRUE);
  }

  /**
   * 扫描目录下的文件
   *
   * @param path            路径
   * @param fileNamePattern 文件名 * 号
   * @return 文件集合
   */
  public static List<File> list(String path, final String fileNamePattern) {
    File file = new File(path);
    return list(file, pathname -> {
      String fileName = pathname.getName();
      return PatternMatchUtils.simpleMatch(fileNamePattern, fileName);
    });
  }

  /**
   * 扫描目录下的文件
   *
   * @param path   路径
   * @param filter 文件过滤
   * @return 文件集合
   */
  public static List<File> list(String path, FileFilter filter) {
    File file = new File(path);
    return list(file, filter);
  }

  /**
   * 扫描目录下的文件
   *
   * @param file 文件
   * @return 文件集合
   */
  public static List<File> list(File file) {
    List<File> fileList = new ArrayList<>();
    return list(file, fileList, TrueFilter.TRUE);
  }

  /**
   * 扫描目录下的文件
   *
   * @param file            文件
   * @param fileNamePattern Spring AntPathMatcher 规则
   * @return 文件集合
   */
  public static List<File> list(File file, final String fileNamePattern) {
    List<File> fileList = new ArrayList<>();
    return list(file, fileList, pathname -> {
      String fileName = pathname.getName();
      return PatternMatchUtils.simpleMatch(fileNamePattern, fileName);
    });
  }

  /**
   * 扫描目录下的文件
   *
   * @param file   文件
   * @param filter 文件过滤
   * @return 文件集合
   */
  public static List<File> list(File file, FileFilter filter) {
    List<File> fileList = new ArrayList<>();
    return list(file, fileList, filter);
  }

  /**
   * 扫描目录下的文件
   *
   * @param file   文件
   * @param filter 文件过滤
   * @return 文件集合
   */
  private static List<File> list(File file, List<File> fileList, FileFilter filter) {
    if (file.isDirectory()) {
      File[] files = file.listFiles();
      if (files != null) {
        for (File f : files) {
          list(f, fileList, filter);
        }
      }
    } else {
      // 过滤文件
      boolean accept = filter.accept(file);
      if (file.exists() && accept) {
        fileList.add(file);
      }
    }
    return fileList;
  }

  /**
   * 获取文件后缀名
   *
   * @param fullName 文件全名
   * @return {String}
   */
  public static String getFileExtension(String fullName) {
		if (StringUtil.isBlank(fullName)) {
			return StringPool.EMPTY;
		}
    String fileName = new File(fullName).getName();
    int dotIndex = fileName.lastIndexOf(CharPool.DOT);
    return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
  }

  /**
   * 获取文件名，去除后缀名
   *
   * @param fullName 文件全名
   * @return {String}
   */
  public static String getNameWithoutExtension(String fullName) {
		if (StringUtil.isBlank(fullName)) {
			return StringPool.EMPTY;
		}
    String fileName = new File(fullName).getName();
    int dotIndex = fileName.lastIndexOf(CharPool.DOT);
    return (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
  }

  /**
   * Returns the path to the system temporary directory.
   *
   * @return the path to the system temporary directory.
   */
  public static String getTempDirPath() {
    return System.getProperty("java.io.tmpdir");
  }

  /**
   * Returns a {@link File} representing the system temporary directory.
   *
   * @return the system temporary directory.
   */
  public static File getTempDir() {
    return new File(getTempDirPath());
  }

  /**
   * Reads the contents of a file into a String. The file is always closed.
   *
   * @param file the file to read, must not be {@code null}
   * @return the file contents, never {@code null}
   */
  public static String readToString(final File file) {
    return readToString(file, StandardCharsets.UTF_8);
  }

  /**
   * Reads the contents of a file into a String. The file is always closed.
   *
   * @param file     the file to read, must not be {@code null}
   * @param encoding the encoding to use, {@code null} means platform default
   * @return the file contents, never {@code null}
   */
  public static String readToString(final File file, final Charset encoding) {
    try (InputStream in = Files.newInputStream(file.toPath())) {
      return IOUtil.readToString(in, encoding);
    } catch (IOException e) {
      throw Exceptions.unchecked(e);
    }
  }

  /**
   * Reads the contents of a file into a String. The file is always closed.
   *
   * @param file the file to read, must not be {@code null}
   * @return the file contents, never {@code null}
   */
  public static byte[] readToByteArray(final File file) {
    try (InputStream in = Files.newInputStream(file.toPath())) {
      return IOUtil.readToByteArray(in);
    } catch (IOException e) {
      throw Exceptions.unchecked(e);
    }
  }

  /**
   * Writes a String to a file creating the file if it does not exist.
   *
   * @param file the file to write
   * @param data the content to write to the file
   */
  public static void writeToFile(final File file, final String data) {
    writeToFile(file, data, StandardCharsets.UTF_8, false);
  }

  /**
   * Writes a String to a file creating the file if it does not exist.
   *
   * @param file   the file to write
   * @param data   the content to write to the file
   * @param append if {@code true}, then the String will be added to the end of the file rather than
   *               overwriting
   */
  public static void writeToFile(final File file, final String data, final boolean append) {
    writeToFile(file, data, StandardCharsets.UTF_8, append);
  }

  /**
   * Writes a String to a file creating the file if it does not exist.
   *
   * @param file     the file to write
   * @param data     the content to write to the file
   * @param encoding the encoding to use, {@code null} means platform default
   */
  public static void writeToFile(final File file, final String data, final Charset encoding) {
    writeToFile(file, data, encoding, false);
  }

  /**
   * Writes a String to a file creating the file if it does not exist.
   *
   * @param file     the file to write
   * @param data     the content to write to the file
   * @param encoding the encoding to use, {@code null} means platform default
   * @param append   if {@code true}, then the String will be added to the end of the file rather
   *                 than overwriting
   */
  public static void writeToFile(final File file, final String data, final Charset encoding,
      final boolean append) {
    try (OutputStream out = new FileOutputStream(file, append)) {
      IOUtil.write(data, out, encoding);
    } catch (IOException e) {
      throw Exceptions.unchecked(e);
    }
  }

  /**
   * 转成file
   *
   * @param in   InputStream
   * @param file File
   */
  public static void toFile(InputStream in, final File file) {
    try (OutputStream out = new FileOutputStream(file)) {
      FileUtil.copy(in, out);
    } catch (IOException e) {
      throw Exceptions.unchecked(e);
    }
  }

  /**
   * Moves a file.
   * <p>
   * When the destination file is on another file system, do a "copy and delete".
   *
   * @param srcFile  the file to be moved
   * @param destFile the destination file
   * @throws NullPointerException if source or destination is {@code null}
   * @throws IOException          if source or destination is invalid
   * @throws IOException          if an IO error occurs moving the file
   */
  public static void moveFile(final File srcFile, final File destFile) throws IOException {
    Assert.notNull(srcFile, "Source must not be null");
    Assert.notNull(destFile, "Destination must not be null");
    if (!srcFile.exists()) {
      throw new FileNotFoundException("Source '" + srcFile + "' does not exist");
    }
    if (srcFile.isDirectory()) {
      throw new IOException("Source '" + srcFile + "' is a directory");
    }
    if (destFile.exists()) {
      throw new IOException("Destination '" + destFile + "' already exists");
    }
    if (destFile.isDirectory()) {
      throw new IOException("Destination '" + destFile + "' is a directory");
    }
    final boolean rename = srcFile.renameTo(destFile);
    if (!rename) {
      FileUtil.copy(srcFile, destFile);
      if (!srcFile.delete()) {
        FileUtil.deleteQuietly(destFile);
        throw new IOException(
            "Failed to delete original file '" + srcFile + "' after copy to '" + destFile + "'");
      }
    }
  }

  /**
   * Deletes a file, never throwing an exception. If file is a directory, delete it and all
   * sub-directories.
   * <p>
   * The difference between File.delete() and this method are:
   * <ul>
   * <li>A directory to be deleted does not have to be empty.</li>
   * <li>No exceptions are thrown when a file or directory cannot be deleted.</li>
   * </ul>
   *
   * @param file file or directory to delete, can be {@code null}
   * @return {@code true} if the file or directory was deleted, otherwise {@code false}
   */
  public static boolean deleteQuietly(@Nullable final File file) {
    if (file == null) {
      return false;
    }
    try {
      if (file.isDirectory()) {
        FileSystemUtils.deleteRecursively(file);
      }
    } catch (final Exception ignored) {
    }

    try {
      return file.delete();
    } catch (final Exception ignored) {
      return false;
    }
  }

  /**
   * 根据地址删除
   *
   * @param path 地址
   * @return boolean
   */
  public static boolean delete(String path) {
    return deleteQuietly(new File(path));
  }

  public static File file(String path) {
    return null == path ? null : new File(getAbsolutePath(path));
  }

  public static File file(File parent, String path) {
    return checkSlip(parent, buildFile(parent, path));
  }

  public static File checkSlip(File parentFile, File file) {
    if (null != parentFile && null != file) {
      String parentCanonicalPath;
      String canonicalPath;
      try {
        parentCanonicalPath = parentFile.getCanonicalPath();
        canonicalPath = file.getCanonicalPath();
      } catch (IOException var5) {
        parentCanonicalPath = parentFile.getAbsolutePath();
        canonicalPath = file.getAbsolutePath();
      }

      if (!canonicalPath.startsWith(parentCanonicalPath)) {
        throw new IllegalArgumentException(
            "New file is outside of the parent dir: " + file.getName());
      }
    }

    return file;
  }

  public static String normalize(String path) {
    if (path == null) {
      return null;
    } else {
      String pathToUse = StringUtil.removePrefixIgnoreCase(path, "classpath:");
      pathToUse = StringUtil.removePrefixIgnoreCase(pathToUse, "file:");
      if (pathToUse.startsWith("~")) {
        pathToUse = getUserHomePath() + pathToUse.substring(1);
      }

      pathToUse = pathToUse.replaceAll("[/\\\\]+", "/");
      pathToUse = StringUtil.trimStart(pathToUse);
      if (path.startsWith("\\\\")) {
        pathToUse = "\\" + pathToUse;
      }

      String prefix = "";
      int prefixIndex = pathToUse.indexOf(":");
      if (prefixIndex > -1) {
        prefix = pathToUse.substring(0, prefixIndex + 1);
        if (prefix.startsWith("/")) {
          prefix = prefix.substring(1);
        }

        if (!prefix.contains("/")) {
          pathToUse = pathToUse.substring(prefixIndex + 1);
        } else {
          prefix = "";
        }
      }

      if (pathToUse.startsWith("/")) {
        prefix = prefix + "/";
        pathToUse = pathToUse.substring(1);
      }

      List<String> pathList = StringUtil.split(pathToUse, '/');
      List<String> pathElements = new LinkedList<>();
      int tops = 0;

      for (int i = pathList.size() - 1; i >= 0; --i) {
        String element = (String) pathList.get(i);
        if (!".".equals(element)) {
          if ("..".equals(element)) {
            ++tops;
          } else if (tops > 0) {
            --tops;
          } else {
            pathElements.add(0, element);
          }
        }
      }

      if (tops > 0 && StringUtil.isEmpty(prefix)) {
        while (tops-- > 0) {
          pathElements.add(0, "..");
        }
      }

      return prefix + String.join("/", pathElements);
    }
  }

  public static String getAbsolutePath(String path, Class<?> baseClass) {
    String normalPath;
    if (path == null) {
      normalPath = "";
    } else {
      normalPath = normalize(path);
      if (isAbsolutePath(normalPath)) {
        return normalPath;
      }
    }

    URL url = ResourceUtil.getResourceURL(normalPath, baseClass);
    if (null != url) {
      return normalize(url.getPath());
    } else {
      String classPath = ClassUtil.getClassPath();
      return null == classPath ? path : normalize(classPath.concat(Objects.requireNonNull(path)));
    }
  }

  public static String getAbsolutePath(String path) {
    return getAbsolutePath(path, null);
  }

  public static boolean isAbsolutePath(String path) {
    return '/' == path.charAt(0) || path.matches("^[a-zA-Z]:([/\\\\].*)?");
  }

  private static File buildFile(File outFile, String fileName) {
    fileName = fileName.replace('\\', '/');
    if (!isWindows() && fileName.lastIndexOf(47, fileName.length() - 2) > 0) {
      List<String> pathParts = StringUtil.split(fileName, '/', false, true);
      int lastPartIndex = pathParts.size() - 1;

      for (int i = 0; i < lastPartIndex; ++i) {
        outFile = new File(outFile, pathParts.get(i));
      }

      outFile.mkdirs();
      fileName = pathParts.get(lastPartIndex);
    }

    return new File(outFile, fileName);
  }

  private static boolean isWindows() {
    return '\\' == File.separatorChar;
  }

  private static String getUserHomePath() {
    return System.getProperty("user.home");
  }

  public static File mkdir(String dirPath) {
    if (dirPath == null) {
      return null;
    } else {
      File dir = file(dirPath);
      return mkdir(dir);
    }
  }

  public static File mkdir(File dir) {
    if (dir == null) {
      return null;
    } else {
      if (!dir.exists()) {
        mkdirsSafely(dir, 5, 1L);
      }

      return dir;
    }
  }

  public static boolean mkdirsSafely(File dir, int tryCount, long sleepMillis) {
    if (dir == null) {
      return false;
    } else if (dir.isDirectory()) {
      return true;
    } else {
      for (int i = 1; i <= tryCount; ++i) {
        dir.mkdirs();
        if (dir.exists()) {
          return true;
        }
        ThreadUtil.sleep(sleepMillis);
      }

      return dir.exists();
    }
  }

  public static File mkParentDirs(File file) {
    return null == file ? null : mkdir(file.getParentFile());
  }

  public static File mkParentDirs(String path) {
    return path == null ? null : mkParentDirs(file(path));
  }

  public static File touch(String path) {
    return path == null ? null : touch(file(path));
  }

  @SneakyThrows
  public static File touch(File file) {
    if (null == file) {
      return null;
    } else {
      if (!file.exists()) {
        mkParentDirs(file);
        file.createNewFile();
      }

      return file;
    }
  }

  /**
   * Returns {@code true} if {@link File} exists.
   */
  public static boolean isExistingFile(final File file) {
    return file != null && file.exists() && file.isFile();
  }

  /**
   * Returns {@code true} if directory exists.
   */
  public static boolean isExistingFolder(final File folder) {
    return folder != null && folder.exists() && folder.isDirectory();
  }
}
