package org.rda.QueryStore.business.pipeline.tasks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.rda.QueryStore.beans.NotificationDetail;
import org.rda.QueryStore.dao.TechConfigDao;

public class DataFileZipper extends PipelineTask {
	
	public DataFileZipper(UUID queryId, String dataFileLocalAbsolutePath, NotificationDetail notification) {
		super();
		this.queryId = queryId;
		this.dataFileLocalAbsolutePath = dataFileLocalAbsolutePath;
		this.notification = notification;
	}

	private UUID queryId;
	private String dataFileLocalAbsolutePath;
	private NotificationDetail notification;
	
	@Override
	public void runCurrentTask() throws PipelineNodeException {
		try {
			String zippedFileAbsolutePath = TechConfigDao.getInstance()
					.getAbsoluteDataPath() + "/" + queryId.toString() + ".zip";
			
			String zippedFileName = queryId.toString()+".xsams";
			
			// zip the file
			this.zipFile(dataFileLocalAbsolutePath, zippedFileAbsolutePath, zippedFileName);
			
			// delete the source file, after having it in zipped version
			this.deleteOriginalFileAfterZip(dataFileLocalAbsolutePath);
		} catch (Exception e) {
			new ErrorHandler(notification, queryId, e.getMessage(), this.getClass().getName());
			throw new PipelineNodeException(e.getMessage());
		}
		
	}
	

	@Override
	protected void instanciateFollowingTask() {
		// The work is over, nothing more to do 
		this.setFollowingTask(null);
	}
	
	
	private void deleteOriginalFileAfterZip(String fileToDeletePath){

		System.out.println("### " + getTime() + " xsams file deleted for query "+ this.queryId);
		File fileToDelete = new File(fileToDeletePath);
		if(fileToDelete.exists()){
			fileToDelete.delete();
		}
	}
	
	private void zipFile(String inputFilePath, String outputFilePath, String zippedFileName) throws IOException{
		byte[] buffer = new byte[1024];
		FileOutputStream fos = new FileOutputStream(outputFilePath);
		ZipOutputStream zos = new ZipOutputStream(fos);
		ZipEntry ze= new ZipEntry(zippedFileName);
		zos.putNextEntry(ze);
		FileInputStream in = new FileInputStream(inputFilePath);

		int len;
		while ((len = in.read(buffer)) > 0) {
			zos.write(buffer, 0, len);
		}

		in.close();
		zos.closeEntry();
		zos.close();
		System.out.println("### " + getTime() + " data file zipped for query "+ this.queryId);
	}

}
