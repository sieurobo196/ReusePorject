/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.customercare.services;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import vn.itt.msales.entity.Location;
import vn.itt.msales.entity.MCP;
import vn.itt.msales.entity.MCPDetails;
import vn.itt.msales.entity.POS;
import vn.itt.msales.entity.Status;
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.param.MsalesParameter;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.logex.MSalesException;
import vn.itt.msales.services.DataService;

/**
 *
 * @author cshiflett
 */
public class MCPServiceImpl implements MCPService {

    @Autowired
    private DataService dataService;
    
    @Override
    public Boolean insertMCPAndDetails(MCP mcp) {

        List<MCPDetails> mcpSalesDetailss = mcp.getMcpDetailss();
        if (mcpSalesDetailss != null && !mcpSalesDetailss.isEmpty()) {
            for (MCPDetails mSalesDetails : mcpSalesDetailss) {
                if (mSalesDetails != null) {
                    if (mSalesDetails.getImplementEmployeeId() != null) {
                        User user = dataService.getRowById(mSalesDetails.getImplementEmployeeId(), User.class);
                        if (user == null) {
                            return false;
                        }
                    }
                    if (mSalesDetails.getPosId() != null) {
                        POS pos = dataService.getRowById(mSalesDetails.getPosId(), POS.class);
                        //pos is not exist
                        if (pos == null) {
                            return false;
                        }
                        Location location = dataService.getRowById(pos.getLocations().getId(), Location.class);
                        mSalesDetails.setLocations(location);

                    }
                    if (mSalesDetails.getStatusId() != null) {
                        Status status = dataService.getRowById(mSalesDetails.getStatusId(), Status.class);
                        if (status == null) {
                            return false;
                        }
                    }
                    mSalesDetails.setCreatedUser(mcp.getCreatedUser());
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
        if (mcp.getImplementEmployeeId() != null) {
            //get ImplementEmployee from mcp
            User implementEmployee = dataService.getRowById(mcp.getImplementEmployeeId(), User.class);
            //implementEmployee is not exist
            if (implementEmployee == null) {
                //return message warning implementEmployee is not exist in DB
                return false;
            }
        }
        if (mcp.getStatusId() != null) {

            // get status from mcp
            Status status = dataService.getRowById(mcp.getStatusId(), Status.class);
            //status is not exist
            if (status == null) {
                //return message warning status is not exist in DB
                return false;
            }
        }
        if (mcp.getType() != null && mcp.getType() != 1) {
            return false;
        }
        if (mcp.getCreatedUser() != null) {
            User user = dataService.getRowById(mcp.getCreatedUser(), User.class);
            if (user == null) {
                return false;
            }
        }

        int ret1 = 0;
        int ret2 = 0;
        try {
            //save status to DB
            ret1 = dataService.insertRow(mcp);
        } catch (MSalesException ex) {
            Logger.getLogger(MCPService.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (ret1 > 0) {
            for (MCPDetails mcpSalesDetails : mcpSalesDetailss) {
                mcpSalesDetails.setMcpId(ret1);
                try {
                    //save status to DB
                    ret2 = dataService.insertRow(mcpSalesDetails);
                } catch (MSalesException ex) {
                    Logger.getLogger(MCPService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        //save Succcess
        if (ret2 > 0) {
            return true;
        }  else {
            return false;
        }
    }

    @Override
    public int checkIsExistMCP(int implementId, String beginDate) {
        String sql = "SELECT mcp.id as ID FROM mcp"
                + " where mcp.deleted_user = 0 "
                + " and mcp.IMPLEMENT_EMPLOYEE_ID=" + implementId
                + " and mcp.BEGIN_DATE='" + beginDate + "'"
                + " order by BEGIN_DATE desc";

        List<HashMap<String, Object>> list = (List<HashMap<String, Object>>) dataService.execSQL(sql);
        if (list != null && list.size() > 0) {
            HashMap<String, Object> firstObj = list.get(0);
            return (Integer) firstObj.get("ID");
        }
        return -1;
    }

    @Override
    public List<MCPDetails> getListMCPDetailByMcpIdAndImplementId(int mcpId, int mcpImplementId) {
        ParameterList param = new ParameterList();
        param.add("mcps.id", mcpId);
        param.add("implementEmployees.id", mcpImplementId);
        List<MCPDetails> mcpDetailList = dataService.getListOption(MCPDetails.class, param);
        return mcpDetailList;
    }

    @Override
    public Boolean updateMCPAndDetails(MCP mcp, List<Integer> tempPos, boolean override, int userId) {
        if (mcp.getId() != null) {
            MCP mcp2 = dataService.getRowById(mcp.getId(), MCP.class);

            if (mcp2 == null) {
                return false;
            }
            else{
                //fix ko sua ngay update mcp/mcp_details
                mcp.setBeginDate(mcp2.getBeginDate());
                mcp.setFinishTime(mcp2.getFinishTime());
            }
        }
        List<MCPDetails> mcpSalesDetailss = mcp.getMcpDetailss();
        for (MCPDetails mSalesDetails : mcpSalesDetailss) {
            if (mSalesDetails.getImplementEmployeeId() != null) {
                User user = dataService.getRowById(mSalesDetails.getImplementEmployeeId(), User.class);
                if (user == null) {
                    return false;
                }
            }
            if (mSalesDetails.getPosId() != null) {
                POS pos = dataService.getRowById(mSalesDetails.getPosId(), POS.class);
                //goods is not exist
                if (pos == null) {
                    return false;
                }
                Location location = dataService.getRowById(pos.getLocations().getId(), Location.class);
                mSalesDetails.setLocations(location);

            }
            if (mSalesDetails.getStatusId() != null) {
                Status status = dataService.getRowById(mSalesDetails.getStatusId(), Status.class);
                if (status == null) {
                    return false;
                }
            }
            mSalesDetails.setUpdatedUser(mcp.getUpdatedUser());
        }
        if (mcp.getImplementEmployeeId() != null) {
            //get ImplementEmployee from mcp
            User implementEmployee = dataService.getRowById(mcp.getImplementEmployeeId(), User.class);
            //implementEmployee is not exist
            if (implementEmployee == null) {
                //return message warning implementEmployee is not exist in DB
                return false;
            }
        }
        if (mcp.getStatusId() != null) {

            // get status from mcp
            Status status = dataService.getRowById(mcp.getStatusId(), Status.class);
            //status is not exist
            if (status == null) {
                //return message warning status is not exist in DB
                return false;
            }
        }
        if (mcp.getType() != null && mcp.getType() != 1) {
            return false;
        }
        if (mcp.getUpdatedUser() != null) {
            User user = dataService.getRowById(mcp.getUpdatedUser(), User.class);
            if (user == null) {
                return false;
            }
        }
        if (mcp.getUpdatedUser() == null) {
            return false;
        }

        int ret1 = 0;
        int ret2 = 0;
        try {
            //save status to DB
            ret1 = dataService.updateSync(mcp);
        } catch (Exception e) {
            Logger.getLogger(MCPService.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }

        if (ret1 > 0) {
            for (MCPDetails newMcpDetail : mcpSalesDetailss) {
                newMcpDetail.setMcpId(ret1);
                //fix ko sua ngay mcp/mcp_details
                newMcpDetail.setFinishTime(mcp.getFinishTime());
                try {
                    newMcpDetail.setCreatedUser(mcp.getUpdatedUser());
                    ret2 = dataService.insertRow(newMcpDetail);
                } catch (MSalesException e) {
                    Logger.getLogger(MCPService.class.getName()).log(Level.SEVERE, null, e);
                    return false;
                }
            }

            if (tempPos.size() > 0 && override == false) {
                String hql = "select MCPDetails from MCPDetails as MCPDetails "
                        + " WHERE MCPDetails.deletedUser=0"
                        + " AND MCPDetails.mcps.id=" + mcp.getId()
                        + " AND MCPDetails.implementEmployees.id=" + mcp.getImplementEmployeeId()
                        + " AND MCPDetails.poss.id NOT IN(";
                for (int i = 0; i < tempPos.size(); i++) {
                    Integer _id = tempPos.get(i);
                    hql += _id;
                    if (i < (tempPos.size() - 1)) {
                        hql += ",";
                    }
                }
                hql += ")";

                List<MCPDetails> list = dataService.executeSelectHQL(MCPDetails.class, hql, false, 0, 0);
                if (list != null && list.size() > 0) {
                    for (MCPDetails obj : list) {
                        MCPDetails mcpDetail = new MCPDetails();
                        mcpDetail.setId(obj.getId());
                        mcpDetail.setDeletedUser(userId);
                        dataService.deleteSynch(mcpDetail);
                    }
                }
            }
        }
        //save Succcess
        return true;
    }

    @Override
    public List<MCPDetails> getListMCPDetailByMcpId(int mcpId) {
        ParameterList param = new ParameterList();
        param.add("mcps.id", mcpId);
        List<MCPDetails> mcpDetailList = dataService.getListOption(MCPDetails.class, param);
        return mcpDetailList;
    }

    @Override
    public MCP getMCPById(int _id) {
        MCP mcp = dataService.getRowById(_id, MCP.class);
        return mcp;
    }

    @Override
    public List<MCP> checkMCPDublicated(MCP mcp, String posCode) {
        String hql = "FROM MCP WHERE implementEmployees.id =:imployerID AND beginDate =:beginDate AND DELETED_USER = 0";

        List<MsalesParameter> listParam = new ArrayList<>();
        listParam.add(MsalesParameter.create("imployerID", mcp.getImplementEmployeeId()));
        listParam.add(MsalesParameter.create("beginDate", mcp.getBeginDate(), 2));

        List<MCP> mcpList = dataService.executeSelectHQL(hql, listParam, false, 1, 1);
        if (mcpList != null && !mcpList.isEmpty()) {
//            MCP mcpFound = mcpList.get(0);
//
//            ParameterList param = new ParameterList();
//            param.add("posCode", posCode);
//            List<POS> poss = dataService.getListOption(POS.class, param);
//            if (poss != null && !poss.isEmpty()) {
//                POS pos = poss.get(0);
//                ParameterList paramMCPDetails = new ParameterList();
//                paramMCPDetails.add("implementEmployees.id", mcp.getImplementEmployeeId());
//                paramMCPDetails.add("mcps.id", mcpFound.getId());
//                paramMCPDetails.add("poss.id", pos.getId());
//                List<MCPDetails> mCPDetails = dataService.getListOption(MCPDetails.class, paramMCPDetails);
//                if (mCPDetails != null && !mCPDetails.isEmpty()) {
//                    return mcpList;
//                }
//            }
            return mcpList;
        }

        return null;
    }

    @Override
    public List<MCPDetails> checkMCPDetailsDuplicated(MCP mcp, String posCode) {
        List<MCPDetails> mcpDetails = new ArrayList<>();
        ParameterList param = new ParameterList();
        param.add("posCode", posCode);
        List<POS> poss = dataService.getListOption(POS.class, param);
        if (poss != null && !poss.isEmpty()) {
            POS pos = poss.get(0);
            ParameterList paramMCPDetails = new ParameterList(0, 0);
            paramMCPDetails.add("implementEmployees.id", mcp.getImplementEmployeeId());
            paramMCPDetails.add("mcps.id", mcp.getId());
            paramMCPDetails.add("poss.id", pos.getId());
            List<MCPDetails> mCPDetails = dataService.getListOption(MCPDetails.class, paramMCPDetails);
            if (mCPDetails != null && !mCPDetails.isEmpty()) {
                mcpDetails.add(mCPDetails.get(0));
            }
        }

        return mcpDetails;
    }


}
