package com.lvwj.halo.common.utils;

import lombok.SneakyThrows;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipOutputStream;

/**
 * ZIP builder class for building both files or in-memory zips.
 */
public class ZipBuilder {

	private final ZipOutputStream zos;
	private final File targetZipFile;
	private final ByteArrayOutputStream targetBaos;

	public static ZipBuilder createZipFile(final File zipFile) {
		return new ZipBuilder(zipFile);
	}
	public static ZipBuilder createZipFile(final String zipFile) {
		return new ZipBuilder(new File(zipFile));
	}

	public static ZipBuilder createZipInMemory() {
		return new ZipBuilder();
	}

	@SneakyThrows
	protected ZipBuilder(final File zipFile) {
		if (!FileUtil.isExistingFile(zipFile)) {
			FileUtil.touch(zipFile);
		}
		zos = new ZipOutputStream(new FileOutputStream(zipFile));
		targetZipFile = zipFile;
		targetBaos = null;
	}

	protected ZipBuilder() {
		targetZipFile = null;
		targetBaos = new ByteArrayOutputStream();
		zos = new ZipOutputStream(targetBaos);
	}

	public File toZipFile() {
		IOUtil.closeQuietly(zos);
		return targetZipFile;
	}

	public byte[] toBytes() {
		IOUtil.closeQuietly(zos);
		if (targetZipFile != null) {
			try {
				return IOUtil.readToByteArray(new FileInputStream(targetZipFile));
			}
			catch (IOException ignore) {
				return null;
			}
		}
		return targetBaos.toByteArray();
	}

	public AddFileToZip add(final File source) {
		return new AddFileToZip(source);
	}

	public class AddFileToZip {
		private final File file;
		private String path;
		private String comment;
		private boolean recursive = true;

		private AddFileToZip(final File file) {
			this.file = file;
		}

		/**
		 * Defines optional entry path.
		 */
		public AddFileToZip path(final String path) {
			this.path = path;
			return this;
		}

		/**
		 * Defines optional comment.
		 */
		public AddFileToZip comment(final String comment) {
			this.comment = comment;
			return this;
		}
		/**
		 * Defines if folders content should be added.
		 * Ignored when used for files.
		 */
		public AddFileToZip recursive() {
			this.recursive = true;
			return this;
		}

		/**
		 * Stores the content into the ZIP.
		 */
		public ZipBuilder save() {
			ZipUtil.addToZip(zos, file, path, comment, recursive);
			return ZipBuilder.this;
		}
	}

	public AddContentToZip add(final String content) {
		return new AddContentToZip(content.getBytes(StandardCharsets.UTF_8));
	}

	public AddContentToZip add(final byte[] content) {
		return new AddContentToZip(content);
	}

	public class AddContentToZip {
		private final byte[] bytes;
		private String path;
		private String comment;

		private AddContentToZip(final byte[] content) {
			this.bytes = content;
		}

		/**
		 * Defines optional entry path.
		 */
		public AddContentToZip path(final String path) {
			this.path = path;
			return this;
		}

		/**
		 * Defines optional comment.
		 */
		public AddContentToZip comment(final String comment) {
			this.comment = comment;
			return this;
		}

		/**
		 * Stores the content into the ZIP.
		 */
		public ZipBuilder save() throws IOException {
			ZipUtil.addToZip(zos, bytes, path, comment);
			return ZipBuilder.this;
		}
	}

	public ZipBuilder addFolder(final String folderName) throws IOException {
		ZipUtil.addFolderToZip(zos, folderName, null);
		return this;
	}
}