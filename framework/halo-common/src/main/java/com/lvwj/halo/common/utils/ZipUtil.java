package com.lvwj.halo.common.utils;

import lombok.SneakyThrows;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.*;

/**
 * Performs zip/gzip/zlib operations on files and directories. These are just tools over existing
 * <code>java.util.zip</code> classes, meaning that existing behavior and bugs are persisted. Most
 * common issue is not being able to use UTF8 in file names, because implementation uses old ZIP format that supports
 * only IBM Code Page 437. This bug was resolved in JDK7: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4244499
 */
public class ZipUtil {

  public static final String ZIP_EXT = ".zip";
  public static final String GZIP_EXT = ".gz";
  public static final String ZLIB_EXT = ".zlib";

  // ---------------------------------------------------------------- deflate

  /**
   * Compresses a file into zlib archive.
   */
  public static File zlib(final String file) {
    return zlib(new File(file));
  }

  /**
   * Compresses a file into zlib archive.
   */
  @SneakyThrows
  public static File zlib(final File file) {
    if (file.isDirectory()) {
      throw new RuntimeException("Can't zlib folder");
    }
    Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
    String zlibFileName = file.getAbsolutePath() + ZLIB_EXT;
    try (FileInputStream fis = new FileInputStream(file); DeflaterOutputStream dos = new DeflaterOutputStream(
        new FileOutputStream(zlibFileName), deflater)) {
      IOUtil.copy(fis, dos);
    }
    return new File(zlibFileName);
  }

  // ---------------------------------------------------------------- gzip

  /**
   * Compresses a file into gzip archive.
   */
  public static File gzip(final String fileName) {
    return gzip(new File(fileName));
  }

  /**
   * Compresses a file into gzip archive.
   */
  @SneakyThrows
  public static File gzip(final File file) {
    if (file.isDirectory()) {
      throw new RuntimeException("Can't gzip folder");
    }
    String gzipName = file.getAbsolutePath() + GZIP_EXT;
    try (FileInputStream fis = new FileInputStream(file); GZIPOutputStream gzipOs = new GZIPOutputStream(
        new FileOutputStream(gzipName));) {
      IOUtil.copy(fis, gzipOs);
    }
    return new File(gzipName);
  }

  /**
   * Decompress gzip archive.
   */
  public static File unGzip(final String file) {
    return unGzip(new File(file));
  }

  /**
   * Decompress gzip archive.
   */
  @SneakyThrows
  public static File unGzip(final File file) {
    String outFileName = FileNameUtil.mainName(file.getAbsolutePath());
    File out = new File(outFileName);
    try {
      if (!out.createNewFile()) {
        throw new RuntimeException("Decompress gzip failed: file[" + outFileName + "] already exists");
      }
    } catch (IOException ignored) {
    }
    try (FileOutputStream fos = new FileOutputStream(out); GZIPInputStream gzipOs = new GZIPInputStream(
        new FileInputStream(file))) {
      IOUtil.copy(gzipOs, fos);
    }
    return out;
  }

  // ---------------------------------------------------------------- zip

  /**
   * Zips a file or a folder.
   *
   * @see #zip(File)
   */
  public static File zip(final String file) {
    return zip(new File(file));
  }

  /**
   * Zips a file or a folder. If adding a folder, all its content will be added.
   */
  public static File zip(final File file) {
    String zipFile = file.getAbsolutePath() + ZIP_EXT;
    return ZipBuilder.createZipFile(zipFile).add(file).recursive().save().toZipFile();
  }

  // ---------------------------------------------------------------- unzip

  /**
   * Lists zip content.
   */
  @SneakyThrows
  public static List<String> listZip(final File zipFile) throws IOException {
    List<String> entries = new ArrayList<>();
    try (ZipFile zip = new ZipFile(zipFile)) {
      Enumeration<? extends ZipEntry> zipEntries = zip.entries();
      while (zipEntries.hasMoreElements()) {
        ZipEntry entry = zipEntries.nextElement();
        entries.add(entry.getName());
      }
    }
    return Collections.unmodifiableList(entries);
  }

  /**
   * Extracts zip file content to the target directory.
   *
   * @see #unzip(File, File)
   */
  public static void unzip(final String zipFile, final String destDir) throws IOException {
    unzip(new File(zipFile), new File(destDir));
  }

  /**
   * Extracts zip file to the target directory. If patterns are provided only matched paths are extracted.
   *
   * @param zipFile zip file
   * @param destDir destination directory
   */
  @SneakyThrows
  public static void unzip(final File zipFile, final File destDir) throws IOException {
    try (ZipFile zip = new ZipFile(zipFile)) {
      Enumeration<? extends ZipEntry> zipEntries = zip.entries();
      while (zipEntries.hasMoreElements()) {
        ZipEntry entry = zipEntries.nextElement();
        String entryName = entry.getName();
        File file = (destDir != null) ? new File(destDir, entryName) : new File(entryName);
        // check for Zip slip FLAW
//			final File rootDir = destDir != null ? destDir : new File(".");
//			if (!FileUtil.isAncestor(rootDir, file, true)) {
//				throw new IOException("Unzipping");
//			}
        if (entry.isDirectory()) {
          if (!file.mkdirs()) {
            if (!file.isDirectory()) {
              throw new IOException("Failed to create directory: " + file);
            }
          }
        } else {
          File parent = file.getParentFile();
          if (parent != null && !parent.exists()) {
            if (!parent.mkdirs()) {
              if (!file.isDirectory()) {
                throw new IOException("Failed to create directory: " + parent);
              }
            }
          }
          try (InputStream in = zip.getInputStream(entry); OutputStream out = new FileOutputStream(file)) {
            IOUtil.copy(in, out);
          }
        }
      }
    }
  }

  // ---------------------------------------------------------------- zip stream

  /**
   * Adds single entry to ZIP output stream.
   *
   * @param zos       zip output stream
   * @param file      file or folder to add
   * @param path      relative path of file entry; if <code>null</code> files name will be used instead
   * @param comment   optional comment
   * @param recursive when set to <code>true</code> content of added folders will be added, too
   */
  @SneakyThrows
  public static void addToZip(ZipOutputStream zos, File file, String path, String comment, boolean recursive) {
    if (!file.exists()) {
      throw new FileNotFoundException(file.toString());
    }
    if (path == null) {
      path = file.getName();
    }
    while (path.length() != 0 && path.charAt(0) == '/') {
      path = path.substring(1);
    }
    boolean isDir = file.isDirectory();
    if (isDir) {
      // add folder record
      if (!path.endsWith("/")) {
        path += '/';
      }
    }

    ZipEntry zipEntry = new ZipEntry(path);
    zipEntry.setTime(file.lastModified());
    if (comment != null) {
      zipEntry.setComment(comment);
    }
    if (isDir) {
      zipEntry.setSize(0);
      zipEntry.setCrc(0);
    }
    zos.putNextEntry(zipEntry);
    if (!isDir) {
      try (InputStream is = new FileInputStream(file);) {
        IOUtil.copy(is, zos);
      }
    }
    zos.closeEntry();
    // continue adding
    if (recursive && file.isDirectory()) {
      boolean noRelativePath = StringUtil.isBlank(path);
      final File[] children = file.listFiles();
      if (children != null && children.length != 0) {
        for (File child : children) {
          String childRelativePath = (noRelativePath ? StringPool.EMPTY : path) + child.getName();
          addToZip(zos, child, childRelativePath, comment, recursive);
        }
      }
    }
  }

  /**
   * Adds byte content into the zip as a file.
   */
  public static void addToZip(final ZipOutputStream zos, final byte[] content, String path,
      final String comment) throws IOException {
    while (path.length() != 0 && path.charAt(0) == '/') {
      path = path.substring(1);
    }

    if (path.endsWith("/")) {
      path = path.substring(0, path.length() - 1);
    }

    ZipEntry zipEntry = new ZipEntry(path);
    zipEntry.setTime(System.currentTimeMillis());

    if (comment != null) {
      zipEntry.setComment(comment);
    }

    zos.putNextEntry(zipEntry);

    try (InputStream is = new ByteArrayInputStream(content);) {
      IOUtil.copy(is, zos);
    }

    zos.closeEntry();
  }

  public static void addFolderToZip(final ZipOutputStream zos, String path, final String comment) throws IOException {
    while (path.length() != 0 && path.charAt(0) == '/') {
      path = path.substring(1);
    }
    // add folder record
    if (!path.endsWith("/")) {
      path += '/';
    }

    ZipEntry zipEntry = new ZipEntry(path);
    zipEntry.setTime(System.currentTimeMillis());

    if (comment != null) {
      zipEntry.setComment(comment);
    }

    zipEntry.setSize(0);
    zipEntry.setCrc(0);

    zos.putNextEntry(zipEntry);
    zos.closeEntry();
  }

  // ---------------------------------------------------------------- close

  /**
   * Closes zip file safely.
   */
  public static void close(final ZipFile zipFile) {
    if (zipFile != null) {
      try {
        zipFile.close();
      } catch (IOException ioex) {
        // ignore
      }
    }
  }
}
