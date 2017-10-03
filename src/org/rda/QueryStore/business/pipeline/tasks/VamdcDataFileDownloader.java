package org.rda.QueryStore.business.pipeline.tasks;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.UUID;

import org.rda.QueryStore.beans.NotificationDetail;
import org.rda.QueryStore.dao.QueryDao;
import org.rda.QueryStore.dao.TechConfigDao;

public class VamdcDataFileDownloader extends PipelineTask {
	
	private static final Integer WAITING_TIME_SEC=20;
	
	public VamdcDataFileDownloader(UUID queryId, NotificationDetail notification) {
		super();
		this.queryId = queryId;
		this.notification = notification;
	}

	private UUID queryId;
	private NotificationDetail notification;
	private String dataFileLocalAbsolutePath;

	@Override
	public void runCurrentTask() throws PipelineNodeException {
		try {
			// the task is performed only if the data is not present
			Boolean isDataFilePresent = QueryDao.getInstance()
					.isDataFilePresent(queryId);

			System.out.println("### "+ getTime() + " data file present " + isDataFilePresent +" for query "+ this.queryId);

			if (!isDataFilePresent) {
				// wait some time for not over-charging the node
				Thread.sleep(new Long(1000*WAITING_TIME_SEC));
				
				// download the data
				downloadDataFile();
				System.out.println("### "+getTime()+" data file downloaded for query "+ this.queryId);
				// compute the local peristent url for the data
				String dataUrl = TechConfigDao.getInstance()
						.getServletContainerAddress()
						+ "/data/"
						+ queryId.toString() + ".zip";

				// set the url in the query table to the persistent computed one
				QueryDao.getInstance().addDataURL(dataUrl, queryId);
			}
			// if the data file is already present, we do nothing
		} catch (Exception e) {
			new ErrorHandler(notification, queryId, e.getMessage(), this.getClass().getCanonicalName());
			throw new PipelineNodeException(e.getMessage());
		}

	}

	@Override
	protected void instanciateFollowingTask() {
		this.setFollowingTask(new VamdcReferenceGetter(queryId,
				dataFileLocalAbsolutePath, notification));

	}

	public void downloadDataFile() throws IOException, ClassNotFoundException,
			SQLException {
		dataFileLocalAbsolutePath = TechConfigDao.getInstance()
				.getAbsoluteDataPath() + "/" + queryId.toString() + ".xsams";

		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(dataFileLocalAbsolutePath), "UTF-8"));

		URL urldecoded = new URL(notification.getDataURL());// new
															// URL(URLDecoder.decode(notification.getDataURL(),
															// "UTF-8"));

		URLConnection connection = urldecoded.openConnection();
		connection.setRequestProperty("User-Agent", "VAMDC Query store");

		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(connection.getInputStream(), "UTF-8"));

		String line = null;
		while ((line = bufferedReader.readLine()) != null) {
			writer.write(line+"\n");
		}
		bufferedReader.close();
		writer.close();
	}


}
