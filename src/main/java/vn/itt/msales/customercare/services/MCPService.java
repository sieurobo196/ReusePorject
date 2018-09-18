/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.customercare.services;

import java.util.List;
import vn.itt.msales.entity.MCP;
import vn.itt.msales.entity.MCPDetails;

/**
 *
 * @author cshiflett
 */
public interface MCPService {
    public Boolean insertMCPAndDetails(MCP mcp);
    
    public Boolean updateMCPAndDetails(MCP mcp, List<Integer> tempPos, boolean override, int userId);
    
    // Return implement-id if exist.
    public int checkIsExistMCP(int implementId, String beginDate);
    
    public MCP getMCPById(int _id);
    
    public List<MCPDetails> getListMCPDetailByMcpId(int mcpId);
    
    public List<MCPDetails> getListMCPDetailByMcpIdAndImplementId(int mcpId, int mcpImplementId);

    public List<MCP> checkMCPDublicated(MCP mcp, String posCode);

    public List<MCPDetails> checkMCPDetailsDuplicated(MCP mcp, String posCode);
}
