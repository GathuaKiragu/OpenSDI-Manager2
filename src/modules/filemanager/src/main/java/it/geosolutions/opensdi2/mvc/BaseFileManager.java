/*
 *  OpenSDI Manager 2
 *  Copyright (C) 2014 GeoSolutions S.A.S.
 *  http://www.geo-solutions.it
 *
 *  GPLv3 + Classpath exception
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.geosolutions.opensdi2.mvc;

import static it.geosolutions.opensdi2.utils.ResponseConstants.RESULTS;
import static it.geosolutions.opensdi2.utils.ResponseConstants.ROOT;
import static it.geosolutions.opensdi2.utils.ResponseConstants.SUCCESS;
import it.geosolutions.opensdi2.service.FileUploadService;
import it.geosolutions.opensdi2.utils.ControllerUtils;
import it.geosolutions.opensdi2.utils.ResponseConstants;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.ImageIcon;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.multipart.MultipartFile;

/**
 * File Manager controller base for ExtJS
 * 
 * @author adiaz
 * @author Lorenzo Natali
 * @author DamianoG
 * 
 */
public class BaseFileManager extends AbstractFileController {

	protected final static Logger LOGGER = Logger.getLogger(BaseFileManager.class);
	
	private FileUploadService fileUploadService;

	/**
	 * Default width for thumb
	 */
	protected static final int THUMB_W = 100;

	/**
	 * Default height for thumb
	 */
	protected static final int THUMB_H = 100;

	/**
	 * Known operation: Extjs integration folder list
	 */
	public static final String EXTJS_FOLDER_LIST = "get_folderlist";

	/**
	 * Known operation: Extjs integration file list
	 */
	public static final String EXTJS_FILE_LIST = "get_filelist";

	/**
	 * Known operation: Extjs integration folder new
	 */
	public static final String EXTJS_FOLDER_NEW = "folder_new";

	/**
	 * Known operation: Extjs integration folder rename
	 */
	public static final String EXTJS_FOLDER_RENAME = "folder_rename";

	/**
	 * Known operation: Extjs integration folder delete
	 */
	public static final String EXTJS_FOLDER_DEL = "folder_delete";

	/**
	 * Known operation: Extjs integration file rename
	 */
	public static final String EXTJS_FILE_RENAME = "file_rename";

	/**
	 * Known operation: Extjs integration file delete
	 */
	public static final String EXTJS_FILE_DELETE = "file_delete";

	/**
	 * Known operation: Extjs integration file download
	 */
	public static final String EXTJS_FILE_DOWNLOAD = "file_download";

	/**
	 * Known operation: Extjs integration file upload
	 */
	public static final String EXTJS_FILE_UPLOAD = "file_upload";

	/**
	 * Known operation: Extjs integration file properties
	 */
	public static final String EXTJS_FILE_PROPERTIES = "file_properties";

	/**
	 * Known operation: Extjs integration file thumb
	 */
	public static final String EXTJS_FILE_THUMB = "get_thumb";

	/**
	 * Known operation: Extjs integration serve image
	 */
	public static final String EXTJS_IMAGE = "get_image";
	
	/**
	 * Name for new folders
	 */
	protected String newFolderName = "New Folder";

	/**
	 * Browser handler server side for ExtJS filebrowser.
	 * 
	 * @see https://code.google.com/p/ext-ux-filebrowserpanel/
	 * 
	 * @param action
	 *            to perform
	 * @param folder
	 *            folder to browse
	 * @param file
	 *            to perform an operation
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 * 
	 * @return
	 */
	public Object extJSbrowser(
	                 String rootDir,
			 String action,
			 String folder,
			 String name,
			 String oldName,
			 String file,
			HttpServletRequest request,
			HttpServletResponse response) {
	    
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Performing " + action + " in extJSFileBrowser");
		}
		
		String finalFolder = folder != null && !folder.equals("root") ? folder
				: null;

		Map<String, Object> result = new HashMap<String, Object>();

		// TODO: Known operations for ExtJS file browser.
		if (EXTJS_FILE_DELETE.equals(action)) {
			result.put(SUCCESS, deleteFile(rootDir, file, finalFolder));
		} else if (EXTJS_FILE_DOWNLOAD.equals(action)) {
			download(response, file, getFilePath(rootDir, file, finalFolder));
			return null;
		} else if (EXTJS_FILE_LIST.equals(action)) {
			return getFileList(rootDir, folder);
		} else if (EXTJS_FILE_PROPERTIES.equals(action)) {
			LOGGER.error("TODO operation: " + EXTJS_FILE_PROPERTIES);
		} else if (EXTJS_FILE_RENAME.equals(action)) {
			result.put(SUCCESS, renameFolder(rootDir, finalFolder, name, oldName));
		} else if (EXTJS_FILE_THUMB.equals(action)) {
			serveImageThumb(
					response,
					file,
					getFilePath(rootDir, file, finalFolder));
			return null;
		} else if (EXTJS_IMAGE.equals(action)) {
			download(
					"image/" + ControllerUtils.getExtension(file),
					null,
					response,
					file,
					getFilePath(rootDir, file, finalFolder));
			return null;
		} else if (EXTJS_FILE_UPLOAD.equals(action)) {
			LOGGER.error("TODO operation: " + EXTJS_FILE_UPLOAD);
		} else if (EXTJS_FOLDER_DEL.equals(action)) {
			result.put(SUCCESS, deleteFolder(rootDir, finalFolder, null));
		} else if (EXTJS_FOLDER_LIST.equals(action)) {
			return getFolderList(rootDir, folder);
		} else if (EXTJS_FOLDER_NEW.equals(action)) {
			result.put(SUCCESS, newFolder(rootDir, finalFolder));
		} else if (EXTJS_FOLDER_RENAME.equals(action)) {
			result.put(SUCCESS, renameFolder(rootDir, file, name, oldName));
		} else {
			LOGGER.error("Unknown operation " + action);
			result.put(SUCCESS, false);
			result.put(ROOT, action);
			result.put(RESULTS, 1);
		}

		return result;

	}

	/**
	 * Handler for upload files
	 * 
	 * @param operationId
	 * @param gotHeaders
	 * @param file
	 *            uploaded
	 * @param request
	 * @param model
	 * @return
	 * @throws IOException
	 */

	public void upload(
			String rootDir,
			MultipartFile file,
			String name,
			int chunks,
			int chunk,
			String folder,
			HttpServletRequest request,
			HttpServletResponse servletResponse)
			throws IOException {
		upload(rootDir, file, name, name, chunks, chunk, folder, request, servletResponse);
	}

	public void upload(
	                 String rootDir,
			 MultipartFile file,
			 String name,
			 String targetName,
			 int chunks,
			 int chunk,
			 String folder,
			HttpServletRequest request,
			HttpServletResponse servletResponse)
			throws IOException {

	    if (LOGGER.isDebugEnabled()) {
	        LOGGER.debug("upload (name, chunks, chunk) --> " + name + "," + chunks
	                + "," + chunk);
	        LOGGER.debug("Uploading " + fileUploadService.size() + " files");
	    }

	    if (chunks > 0) {
	        // init bytes for the chunk upload
	        Entry<String, ?> entry = fileUploadService.addChunk(name, chunks,
	                chunk, file);
	        if (entry == null) {
	            String msg = "Expired file upload dor file " + name;
	            LOGGER.error(msg);
	            throw new IOException(msg);
	        }
	        if (chunk == chunks - 1) {
	            // get the final file
	        	fileUploadService.getCompletedFile(name, getFilePath(rootDir, targetName, folder), entry);
	        }
	    } else {
	        // init bytes for the chunk upload
	        Entry<String, ?> entry = fileUploadService.addChunk(name, 1,
	                0, file);
	        // get the final file
        	fileUploadService.getCompletedFile(name, getFilePath(rootDir, targetName, folder), entry);
	    }
	}

	/**
	 * Scheduled every day at 4:00. If an user stop an upload, it
	 * will be removed from memory
	 */
	@Scheduled(cron = "0 0 4 * * ?")
	public void cleanupUploadedFiles() {
		fileUploadService.cleanup();
	}

	/**
	 * Download a file
	 * 
	 * @param folder
	 *            folder for the file
	 * @param file
	 *            to be downloaded
	 * @param resp
	 *            servlet response
	 */
	public void downloadFile(
	                String rootDir,
			String folder,
			String file,
			HttpServletResponse resp) {
		download(
				resp,
				file,
				getFilePath(rootDir, file,
						folder != null && !folder.equals("root") ? folder
								: null));
	}

	/**
	 * Delete a file
	 * 
	 * @param fileName
	 * @param subFolder
	 * @return true if the file has been delete or false otherwise
	 */
	protected boolean deleteFile(String rootDir, String fileName, String subFolder) {
		String filePath = getFilePath(rootDir, fileName, subFolder);
		LOGGER.debug("Deleting file '" + filePath + "'");
		File file = new File(filePath);
		if (file.exists()) {
			if (file.canWrite()) {
				try {
					if(file.isDirectory()){
						FileUtils.deleteQuietly(file);
					}else{
						file.delete();
					}
					return true;
				} catch (Exception e) {
					LOGGER.error("Error deleting '" + filePath + "' file");
					return false;
				}
			} else {
				LOGGER.error("Incorrect permissions on file '" + filePath
						+ "'. We can't delete it");
				return false;
			}
		} else {
			LOGGER.error("File '" + filePath + "' not exists");
			return false;
		}
	}

	/**
	 * Create a new folder in a subFolder
	 * 
	 * @param folder
	 * 
	 * @return true if the new folder is successfully created and false otherwise
	 */
	protected boolean newFolder(String rootDir, String absolutePath) {

		// get the new folder path
		String newFolderPath = absolutePath;
		// absolute path could contain target new path or only the parent folder
		if (newFolderPath != null
				&& newFolderPath.contains(ControllerUtils.SEPARATOR)) {
			File targetFile = new File(getFilePath(rootDir, newFolderPath, null));
			if (targetFile.exists()) {
				// absolute path is parent folder
				newFolderPath = generateNewFolderName(rootDir, newFolderPath);
			} else {
				// absolute path is target new folder. We're going to check the
				// parent folder
				String parentFolder = newFolderPath.substring(0,
						newFolderPath.lastIndexOf(ControllerUtils.SEPARATOR));
				File parent = new File(getFilePath(rootDir, parentFolder, null));
				if (!parent.exists()) {
					LOGGER.error("Can't create folder '" + absolutePath
							+ "'. Parent folder don't exists");
					return false;
				}
			}

		}
		// now, we create the folder
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Creating new folder in " + newFolderPath);
		}
		try {
			new File(getFilePath(rootDir, newFolderPath, null)).mkdir();
			return true;
		} catch (Exception e) {
			LOGGER.error("Error creating '" + newFolderPath + "' folder");
			return false;
		}
	}
	
	/**
	 * Generate a new folder name
	 * @param parent
	 * @return new name for the folder
	 */
	protected String generateNewFolderName(String rootDir, String parent){
		String filePath = getFilePath(rootDir, newFolderName, parent);
		File file = new File(filePath);
		// generate new folder name
		if(file.exists()){
			int i = 1;
			while (file.exists()){
				filePath = getFilePath(rootDir, newFolderName + " (" + (i++) + ")", parent);
				file = new File(filePath);
			}
		}
		return filePath;
	}

	protected Object renameFolder(String rootDir, String folder, String newName, String oldName) {
		String filePath = getFilePath(rootDir, oldName, folder);
		String newPath = getFilePath(rootDir, newName, folder);
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("Renaming folder " + folder + " to " + newName);
		}
		File file = new File(filePath);
		File targetFile = new File(newPath);
		if (file.exists()) {
			if(!targetFile.exists()){
				try {
					file.renameTo(targetFile);
					return true;
				} catch (Exception e) {
					LOGGER.error("Error renaming '" + filePath + "' file");
					return false;
				}
			}else{
				LOGGER.error("Target folder '" + newPath
						+ "' already exists");
				return false;
			}
		} else {
			LOGGER.error("File '" + filePath + "' don't exists exists");
			return false;
		}
	}

	/**
	 * @param folder
	 * @return folder list in the folder
	 */
	protected List<Map<String, Object>> getFolderList(String rootDir, String folder) {
		List<Map<String, Object>> data = new LinkedList<Map<String, Object>>();
		String subFolder = folder != null && !folder.equals("root") ? folder
				: "";
		String path = getFilePath(rootDir, "", subFolder);

		File folderToList = new File(path);
		if (folderToList.exists() && folderToList.isDirectory()) {
			for (String sub : folderToList.list()) {
				File file = new File(getFilePath(rootDir, sub, subFolder));
				Map<String, Object> objectData = new HashMap<String, Object>();
				String id = ControllerUtils.SEPARATOR + sub;
				if(folder != null){
					id = folder + id;
				}
				objectData.put("id", id);
//				objectData.put("id", ControllerUtils.SEPARATOR + sub);
				objectData.put("text", sub);
				objectData.put("size", file.length());
				objectData.put("mtime", file.lastModified());
				objectData.put("loaded", !file.isDirectory());
				objectData.put("iconCls", file.isDirectory() ? "folder" : "file");
				objectData.put("leaf", file.isDirectory() ? false: true);
				objectData.put("expanded", false);
				objectData.put("actions", getActions(file));
				data.add(objectData);
			}
		}
		return data;
	}

	/**
	 * This provides actions associated to the file
	 * @param file
	 * @return
	 */
	protected List<Object> getActions(File file) {
		
		return new ArrayList<Object>();
	}

	/**
	 * @param folder
	 * @return file list in the folder
	 */
	protected Map<String, Object> getFileList(String rootDir, String folder) {
		Map<String, Object> result = new HashMap<String, Object>();
		String subFolder = folder != null && !folder.equals("root") ? folder
				: "";
		String path = getFilePath(rootDir, "", subFolder);

		File folderToList = new File(path);
		List<Map<String, Object>> data = new LinkedList<Map<String, Object>>();
		int index = 0;
		if (folderToList.exists() && folderToList.isDirectory()) {
			String[] children = folderToList.list();
			for (String sub : children) {
				File file = new File(getFilePath(rootDir, sub, subFolder));
				Map<String, Object> objectData = new HashMap<String, Object>();
				objectData.put("name", sub);
				objectData.put("size", file.length());
				objectData.put("mtime", file.lastModified());
				objectData.put("iconCls", file.isDirectory() ? "folder" : "file");
				objectData.put("leaf", file.isDirectory() ? false: true);
				objectData.put("web_path", subFolder
						+ ControllerUtils.SEPARATOR + sub);
				result.put(index++ + "", objectData);
				data.add(objectData);
			}
		}
		result.put(ResponseConstants.DATA, data);
		result.put(ResponseConstants.COUNT, index);

		return result;
	}

	/**
	 * Delete a folder
	 * 
	 * @param folderName
	 * @param subFolder
	 */
	protected boolean deleteFolder(String rootDir, String folderName, String subFolder) {
		String folderPath = getFilePath(rootDir, folderName, subFolder);
		LOGGER.debug("Deleting folder '" + folderPath + "'");
		File file = new File(folderPath);
		if (file.exists()) {
			if (file.canWrite()) {
				try {
					FileUtils.deleteDirectory(file);
					return true;
				} catch (IOException e) {
					LOGGER.error("Error deleting '" + folderPath + "' folder");
					return false;
				}
			} else {
				LOGGER.error("Incorrect permissions on folder '" + folderPath
						+ "'. We can't delete it");
				return false;
			}
		} else {
			LOGGER.error("Folder '" + folderPath + "' not exists");
			return false;
		}
	}

	/**
	 * Download a file with a stream
	 * 
	 * @param contentType
	 * @param contentDisposition
	 * @param resp
	 * @param fileName
	 * @param filePath
	 * @return
	 */
	protected ResponseEntity<byte[]> serveImageThumb(HttpServletResponse resp,
			String fileName, String filePath) {

		String contentType = "image/jpg";

		final HttpHeaders headers = new HttpHeaders();
		File toServeUp = new File(filePath);
		InputStream inputStream = null;
		String thumbPath = filePath + "_thumb";
		File fileThumb = new File(thumbPath);

		if (fileThumb.exists()) {
			try {
				inputStream = new FileInputStream(fileThumb);
			} catch (FileNotFoundException e) {

				// Also useful, this is a good was to serve down an error
				// message
				String msg = "ERROR: Could not find the file specified.";
				headers.setContentType(MediaType.TEXT_PLAIN);
				return new ResponseEntity<byte[]>(msg.getBytes(), headers,
						HttpStatus.NOT_FOUND);

			}
		} else {
			try {
				inputStream = getImageThumb(toServeUp, thumbPath);
				fileThumb = new File(thumbPath);
			} catch (Exception e) {

				// Also useful, this is a good was to serve down an error
				// message
				String msg = "ERROR: Could not find the file specified.";
				headers.setContentType(MediaType.TEXT_PLAIN);
				return new ResponseEntity<byte[]>(msg.getBytes(), headers,
						HttpStatus.NOT_FOUND);

			}
		}

		// content type
		resp.setContentType(contentType);

		Long fileSize = fileThumb.length();
		resp.setContentLength(fileSize.intValue());

		OutputStream outputStream = null;

		try {
			outputStream = resp.getOutputStream();
		} catch (IOException e) {
			String msg = "ERROR: Could not generate output stream.";
			headers.setContentType(MediaType.TEXT_PLAIN);
			return new ResponseEntity<byte[]>(msg.getBytes(), headers,
					HttpStatus.NOT_FOUND);
		}

		byte[] buffer = new byte[1024];

		int read = 0;
		try {

			while ((read = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, read);
			}

			// close the streams to prevent memory leaks
			outputStream.flush();
			outputStream.close();
			inputStream.close();

		} catch (Exception e) {
			String msg = "ERROR: Could not read file.";
			headers.setContentType(MediaType.TEXT_PLAIN);
			return new ResponseEntity<byte[]>(msg.getBytes(), headers,
					HttpStatus.NOT_FOUND);
		}

		return null;
	}

	/**
	 * Generate a image thumb
	 * 
	 * @param toServeUp
	 * @param thumbPath
	 * @return
	 * @throws IOException
	 */
	protected InputStream getImageThumb(File toServeUp, String thumbPath)
			throws IOException {
		BufferedImage image = ImageIO.read(toServeUp);
		ImageIcon img = new ImageIcon(new ImageIcon(image).getImage()
				.getScaledInstance(THUMB_W, THUMB_H, Image.SCALE_FAST));
		BufferedImage bimage = new BufferedImage(THUMB_W, THUMB_H,
				BufferedImage.TYPE_INT_RGB);
		
		// Copy image to buffered image
		Graphics g = bimage.createGraphics();
		g.drawImage(img.getImage(), 0, 0, null);
		g.dispose();
		ImageIO.write(bimage, "jpg", new File(thumbPath));
		return new java.io.FileInputStream(thumbPath);
	}
	
	


	/**
	 * Download a file with a stream
	 * 
	 * @param resp
	 * @param fileName
	 * @param filePath
	 * @return
	 */
	protected ResponseEntity<byte[]> download(HttpServletResponse resp,
			String fileName, String filePath) {

		return download("application/octet-stream", "attachment; filename=\""
				+ fileName + "\"", resp, fileName, filePath);
	}

	/**
	 * Download a file with a stream
	 * 
	 * @param contentType
	 * @param contentDisposition
	 * @param resp
	 * @param fileName
	 * @param filePath
	 * @return
	 */
	protected ResponseEntity<byte[]> download(String contentType,
			String contentDisposition, HttpServletResponse resp,
			String fileName, String filePath) {

		final HttpHeaders headers = new HttpHeaders();
		File toServeUp = new File(filePath);
		InputStream inputStream = null;

		try {
			inputStream = new FileInputStream(toServeUp);
		} catch (FileNotFoundException e) {

			// Also useful, this is a good was to serve down an error message
			String msg = "ERROR: Could not find the file specified.";
			headers.setContentType(MediaType.TEXT_PLAIN);
			return new ResponseEntity<byte[]>(msg.getBytes(), headers,
					HttpStatus.NOT_FOUND);

		}

		// content type
		if (contentType != null) {
			resp.setContentType(contentType);
		}

		// content disposition
		if (contentDisposition != null) {
			resp.setHeader("Content-Disposition", contentDisposition);
		}

		Long fileSize = toServeUp.length();
		resp.setContentLength(fileSize.intValue());

		OutputStream outputStream = null;

		try {
			outputStream = resp.getOutputStream();
		} catch (IOException e) {
			String msg = "ERROR: Could not generate output stream.";
			headers.setContentType(MediaType.TEXT_PLAIN);
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e1) {
					// nothing
				}
			}
			return new ResponseEntity<byte[]>(msg.getBytes(), headers,
					HttpStatus.NOT_FOUND);
		}

		byte[] buffer = new byte[1024];

		int read = 0;
		try {

			while ((read = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, read);
			}

			// close the streams to prevent memory leaks
			outputStream.flush();
			outputStream.close();
			inputStream.close();

		} catch (Exception e) {
			String msg = "ERROR: Could not read file.";
			headers.setContentType(MediaType.TEXT_PLAIN);
			return new ResponseEntity<byte[]>(msg.getBytes(), headers,
					HttpStatus.NOT_FOUND);
		}

		return null;
	}

	/**
	 * @return the newFolderName
	 */
	public String getNewFolderName() {
		return newFolderName;
	}

	/**
	 * @param newFolderName the newFolderName to set
	 */
	public void setNewFolderName(String newFolderName) {
		this.newFolderName = newFolderName;
	}

	/**
	 * @return the fileUploadService
	 */
	public FileUploadService getFileUploadService() {
		return fileUploadService;
	}

	/**
	 * @param fileUploadService the fileUploadService to set
	 */
	@Autowired
	public void setFileUploadService(FileUploadService fileUploadService) {
		this.fileUploadService = fileUploadService;
	}

	@Override
	public String getInstanceID(HttpServletRequest req) {
	  //get the instanceID old skool
          return req.getParameter(INSTANCE_ID);
	}

}
