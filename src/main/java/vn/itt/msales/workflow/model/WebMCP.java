package vn.itt.msales.workflow.model;

import vn.itt.msales.entity.MCP;

/**
 *
 * @author ChinhNQ
 */
public class WebMCP {

    private MCP mcp;
    private int month;

    public WebMCP() {

    }

    public WebMCP(MCP mcp, int month) {
        this.mcp = mcp;
        this.month = month;
    }

    public MCP getMcp() {
        return mcp;
    }

    public void setMcp(MCP mcp) {
        this.mcp = mcp;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

}
