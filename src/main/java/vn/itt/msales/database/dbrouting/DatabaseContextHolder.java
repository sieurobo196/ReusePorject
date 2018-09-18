package vn.itt.msales.database.dbrouting;

import org.springframework.util.Assert;

public class DatabaseContextHolder {
	private static final ThreadLocal<DatabaseType> contextHolder = 
            new ThreadLocal<DatabaseType>();
	
   public static void setCustomerType(DatabaseType customerType) {
      Assert.notNull(customerType, "customerType cannot be null");
      contextHolder.set(customerType);
   }

   public static DatabaseType getCustomerType() {
      return (DatabaseType) contextHolder.get();
   }

   public static void clearCustomerType() {
      contextHolder.remove();
   }
}
