package org.rda.QueryStore.business.pipeline.tasks;

import java.io.File;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.UUID;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.rda.QueryStore.beans.NotificationDetail;
import org.rda.QueryStore.dao.QueryDao;
import org.rda.QueryStore.dao.TechConfigDao;

public class VamdcReferenceGetter extends PipelineTask {

	public VamdcReferenceGetter(UUID queryId, String dataFileLocalAbsolutePath,
			NotificationDetail notification) {
		super();
		this.queryId = queryId;
		this.dataFileLocalAbsolutePath = dataFileLocalAbsolutePath;
		this.notification = notification;
	}

	private UUID queryId;
	private String dataFileLocalAbsolutePath;
	private Boolean isDataFileAlreadyProcessed;
	private NotificationDetail notification;

	@Override
	public void runCurrentTask() throws PipelineNodeException {
		try {
			String storedReferences = QueryDao.getInstance().getReferences(
					queryId);
			isDataFileAlreadyProcessed = (null != storedReferences);

			// if the data file has not already been processed we compute them
			// from the data file
			if (!isDataFileAlreadyProcessed) {
				System.out.println("### " + getTime()
						+ " no references stored for query " + this.queryId);
				System.out.println("### " + getTime()
						+ " getting references for query " + this.queryId);
				String references = getReferences();
				// update references into the DB
				QueryDao.getInstance().addReferences(references, queryId);
			}
			// if the references are already stored, there
			// is nothing to do
			System.out.println("### " + getTime()
					+ "  references up to date for query " + this.queryId);
		} catch (Exception e) {
			new ErrorHandler(notification, queryId, e.getMessage(), this
					.getClass().getName());
			throw new PipelineNodeException(e.getMessage());
		}
	}

	@Override
	protected void instanciateFollowingTask() {
		// if the file has not already been processed, we zip the freshly
		// processed xsams file
		if (isDataFileAlreadyProcessed) {
			this.setFollowingTask(null);
		} else {
			this.setFollowingTask(new DataFileZipper(queryId,
					dataFileLocalAbsolutePath, notification));
		}
	}

	private String getReferences() throws PipelineNodeException {
		String toReturn = "";
		// compute the local peristent url for the data
		try {
			String styleSheetAbsolutePath = TechConfigDao.getInstance()
					.getAbsoluteConfigPath() + "/XsamsToBibtex.xsl";

			File styleSheet = new File(styleSheetAbsolutePath);
			File xsamsFile = new File(dataFileLocalAbsolutePath);

			StringWriter writer = new StringWriter();

			TransformerFactory f = TransformerFactory.newInstance();

			Transformer t = f.newTransformer(new StreamSource(styleSheet));
			Source source = new StreamSource(xsamsFile);
			Result result = new StreamResult(writer);
			t.transform(source, result);
			toReturn = writer.toString();
			Charset.forName("UTF-8").encode(toReturn);

		} catch (TransformerException | ClassNotFoundException | SQLException e) {
			throw new PipelineNodeException(e.getMessage());
		}
		return toReturn;
	}

}
