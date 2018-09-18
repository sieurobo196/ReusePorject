package vn.itt.msales.database.dbrouting;

public enum DatabaseType {

    DATABASE_TYPE_VT(MsalesBranches.DATABASE_TYPE_VT), // Database Vien Thong
    DATABASE_TYPE_TC(MsalesBranches.DATABASE_TYPE_TC), // Database Tai Chinh
    DATABASE_TYPE_BH(MsalesBranches.DATABASE_TYPE_BH), // Database Bao hiem
    DATABASE_TYPE_DC(MsalesBranches.DATABASE_TYPE_DC), // Database Duoc
    DATABASE_TYPE_TD(MsalesBranches.DATABASE_TYPE_TD), // Database Tieu dung
    DATABASE_TYPE_COMPANY(0), // Database msales_company
    DATABASE_TYPE_VT_TEST(MsalesBranches.DATABASE_TYPE_VT_TEST), // Database Vien Thong test
    DATABASE_TYPE_TC_TEST(MsalesBranches.DATABASE_TYPE_TC_TEST), // Database Tai Chinh test
    DATABASE_TYPE_BH_TEST(MsalesBranches.DATABASE_TYPE_BH_TEST), // Database Bao hiem test
    DATABASE_TYPE_DC_TEST(MsalesBranches.DATABASE_TYPE_DC_TEST), // Database Duoc test
    DATABASE_TYPE_TD_TEST(MsalesBranches.DATABASE_TYPE_TD_TEST); // Database Tieu dung

    private int branch;

    private DatabaseType() {

    }

    private DatabaseType(int branch) {
        this.branch = branch;
    }

    public int getBranch() {
        return branch;
    }

    public void setBranch(int branch) {
        this.branch = branch;
    }

}
