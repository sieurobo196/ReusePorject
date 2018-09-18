package vn.itt.msales.database.dbrouting;

/**
 *
 * @author vtm036
 */
public class MsalesBranches {

    public static int DATABASE_TYPE_VT = 1; // Database Vien Thong
    public static int DATABASE_TYPE_TC = 2; // Database Tai Chinh
    public static int DATABASE_TYPE_BH = 3; // Database Bao hiem
    public static int DATABASE_TYPE_DC = 4; // Database Duoc
    public static int DATABASE_TYPE_TD = 5; // Database Tieu dung

    public static int DATABASE_TYPE_VT_TEST = -1; // Database Vien Thong Test
    public static int DATABASE_TYPE_TC_TEST = -2; // Database Tai Chinh Test
    public static int DATABASE_TYPE_BH_TEST = -3; // Database Bao hiem Test
    public static int DATABASE_TYPE_DC_TEST = -4; // Database Duoc Test
    public static int DATABASE_TYPE_TD_TEST = -5; // Database Tieu dung Test

    /**
     *
     */
    public static int BRANCHES[] = {MsalesBranches.DATABASE_TYPE_VT, MsalesBranches.DATABASE_TYPE_TC, MsalesBranches.DATABASE_TYPE_BH, MsalesBranches.DATABASE_TYPE_DC,MsalesBranches.DATABASE_TYPE_TD,
        MsalesBranches.DATABASE_TYPE_VT_TEST, MsalesBranches.DATABASE_TYPE_TC_TEST, MsalesBranches.DATABASE_TYPE_BH_TEST, MsalesBranches.DATABASE_TYPE_DC_TEST, MsalesBranches.DATABASE_TYPE_TD_TEST};
}
