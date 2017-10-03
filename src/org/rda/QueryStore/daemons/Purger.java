package org.rda.QueryStore.daemons;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.rda.QueryStore.dao.QueryDao;

public class Purger {

	private void purgeData() {

	}

	public static void main(String[] args) throws ClassNotFoundException,
			SQLException {
		Purger purger = new Purger();
		purger.purgeHeadQueries();
	}

	private void purgeHeadQueries() throws ClassNotFoundException, SQLException {
		// get the current timestamp
		long nowTimeStamp = Instant.now().toEpochMilli();
		Long timeInf = nowTimeStamp - 30 * 60 * 60 * 1000;
		Long timeSup = nowTimeStamp - 2 * 60 * 60 * 1000;

		// Get the UUIDs of the HEAD type queries whose age is between two hours
		// and 30 hours
		List<String> uuidToProcess = QueryDao
				.getInstance()
				.getHeadQueriesIdsByTime(timeInf.toString(), timeSup.toString());

		List<String> uuidHavingGETQueries;

		List<String> uuidToRemove = new ArrayList<String>();

		// Look for each UUID if there exist no GET request associated
		for (String currentUUID : uuidToProcess) {
			uuidHavingGETQueries = QueryDao.getInstance()
					.getUUIDHavingGETQueryToken(currentUUID);

			// if there is no GET query associated
			if (uuidHavingGETQueries.size() < 1) {
				// we add the query to the list to remove
				uuidToRemove.add(currentUUID);
				System.out.println("query " + currentUUID + " will be deleted");
			}
		}
		QueryDao.getInstance().purgeQuery(uuidToRemove, true, true);

		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		System.out.println("Execution time " + dateFormat.format(date));

		if (uuidToRemove.size() > 0) {
			System.out.println("queries listed above are now deleted");
		} else {
			System.out.println("there is no queries to pourge \n ");
		}

	}
}
