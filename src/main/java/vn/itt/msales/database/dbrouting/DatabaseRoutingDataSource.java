package vn.itt.msales.database.dbrouting;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DatabaseRoutingDataSource extends AbstractRoutingDataSource {

	@Override
	protected Object determineCurrentLookupKey() {
				
		if (DatabaseContextHolder.getCustomerType() == null) {
			DatabaseContextHolder.setCustomerType(DatabaseType.DATABASE_TYPE_COMPANY);
		}
		return DatabaseContextHolder.getCustomerType();
	}

}
