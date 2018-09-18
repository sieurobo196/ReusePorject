package vn.itt.msales.customercare.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import vn.itt.msales.common.RPTBarcode;
import vn.itt.msales.customercare.model.ReportFilter;
import vn.itt.msales.customercare.services.MCPService;
import vn.itt.msales.entity.MCPDetails;
import vn.itt.msales.export.services.ExportsService;
import vn.itt.msales.services.DataService;
import vn.itt.msales.user.controller.MsalesUserController;
import vn.itt.msales.user.model.LoginContext;
/*
import vn.itt.customercare.models.DiemBanHang;
import vn.itt.customercare.models.NguoiSuDung;
import vn.itt.customercare.models.NhaPhanPhoi;
import vn.itt.customercare.models.QuanHuyen;
import vn.itt.customercare.models.RPTChart;
import vn.itt.customercare.models.filters.PosListFilter;
import vn.itt.customercare.models.filters.RPTChartFilter;
import vn.itt.customercare.models.filters.ReportFilter;
import vn.itt.customercare.models.filters.SupplierFilter;
import vn.itt.customercare.services.DiemBanHangService;
import vn.itt.customercare.services.FilterService;
import vn.itt.customercare.services.NguoiSuDungService;
import vn.itt.customercare.services.NhaPhanPhoiService;
import vn.itt.customercare.services.QuanHuyenService;
import vn.itt.customercare.services.RPTChartService;
import vn.itt.report.services.ExportsService;
import vn.itt.utils.converter.RPTBarcode;
*/
@Controller
public class RPTBarCodeController {

	private final String FILE_PATH_SMALL = "/rpt_barcode_small.jrxml";
	private final String FILE_PATH_LARGE = "/rpt_barcode_large.jrxml";
	private final String FILE_NAME = "Report_Barcode";
	private final String FORM = "transListForm";
        
        /*
	@Autowired
	private DiemBanHangService diemBanHangService;

	@Autowired
	private RPTChartService rptChartService;

	@Autowired
	private NhaPhanPhoiService nhaPhanPhoiService;

	@Autowired
	private QuanHuyenService quanHuyenService;

	@Autowired
	private ExportsService downloadService;
	
	@Autowired
	private FilterService filterService;
	
	@Autowired
	private NguoiSuDungService userService;
        */
        
        @Autowired
	private ExportsService downloadService;
        
        @Autowired
        private MCPService mcpService;
        @Autowired
        private DataService dataService;
        
	/**
	 * Export small barcode
	 * 
	 * @param type
	 *            (export type)
	 * @param RPTNhatKyBanHangFilter
	 *            class
	 * @param ModelMap
	 *            class
	 * @param HttpServletResponse
	 *            class
	 * 
	 * @return HttpServletResponse(export file)
	 */
        /*
	@RequestMapping(value = "/export/barcodeSmall", method = RequestMethod.POST)
	public void exportBarcodeSmall(
			@ModelAttribute("frmPOSList") @Valid PosListFilter posListFilter,
			Model model, HttpServletResponse response) {
		String token = downloadService.generate();
		
		NguoiSuDung user = userService.getLogin();
		filterService.addAreaSelectionForUserToModel(user, posListFilter, model);
		
		List<DiemBanHang> diemBanHangList = diemBanHangService.list(posListFilter);
		JRDataSource jrDataSource = null;
		if (diemBanHangList.size() > 0) {
			List<RPTBarcode> list = new ArrayList<RPTBarcode>();
			int i = 0;
			while (i <= diemBanHangList.size()) {
				i++;
				RPTBarcode barcode = new RPTBarcode();
				barcode.setBarcode1(diemBanHangList.get(i - 1).getMaCuaHang());
				if (i++ < diemBanHangList.size()) {
					barcode.setBarcode2(diemBanHangList.get(i - 1)
							.getMaCuaHang());
				} else {
					list.add(barcode);
					break;
				}
				if (i++ < diemBanHangList.size()) {
					barcode.setBarcode3(diemBanHangList.get(i - 1)
							.getMaCuaHang());
				} else {
					list.add(barcode);
					break;
				}
				if (i++ < diemBanHangList.size()) {
					barcode.setBarcode4(diemBanHangList.get(i - 1)
							.getMaCuaHang());
				} else {
					list.add(barcode);
					break;
				}
				if (i++ < diemBanHangList.size()) {
					barcode.setBarcode5(diemBanHangList.get(i - 1)
							.getMaCuaHang());
				} else {
					list.add(barcode);
					break;
				}
				list.add(barcode);
			}

			jrDataSource = new JRBeanCollectionDataSource(list);
		} else
			jrDataSource = new JREmptyDataSource();
		ReportFilter filter = new ReportFilter();
		filter.setTemplate(FILE_PATH_SMALL);
		filter.setFileName(FILE_NAME);
		filter.setExportType("xls");
		downloadService.download(filter, token, jrDataSource, response);
	}
        */
	/**
	 * Export large barcode
	 * 
	 * @param type
	 *            (export type)
	 * @param RPTNhatKyBanHangFilter
	 *            class
	 * @param ModelMap
	 *            class
	 * @param HttpServletResponse
	 *            class
	 * 
	 * @return HttpServletResponse(export file)
	 */
        /*
	@RequestMapping(value = "/export/barcodeLarge", method = RequestMethod.POST)
	public void exportBarcodeLarge(
			@ModelAttribute("frmPOSList") @Valid PosListFilter posListFilter,
			Model model, HttpServletResponse response) {
		String token = downloadService.generate();
		
		NguoiSuDung user = userService.getLogin();
		filterService.addAreaSelectionForUserToModel(user, posListFilter, model);
		
		List<DiemBanHang> diemBanHangList = diemBanHangService.list(posListFilter);
		JRDataSource jrDataSource = null;
		if (diemBanHangList.size() > 0) {
			List<RPTBarcode> list = new ArrayList<RPTBarcode>();
			int i = 0;
			while (i < diemBanHangList.size()) {
				i++;
				RPTBarcode barcode = new RPTBarcode();
				barcode.setCode1(diemBanHangList.get(i - 1).getMaCuaHang());
				barcode.setBarcode1(diemBanHangList.get(i - 1).getMaCuaHang());
				barcode.setBarcode1Name(diemBanHangList.get(i - 1)
						.getTenCuaHang());
				if (diemBanHangList.get(i - 1).getQuanHuyen() != null) {
					QuanHuyen quanHuyen1 = quanHuyenService
							.find(diemBanHangList.get(i - 1).getQuanHuyen());
					if (quanHuyen1!=null)
					barcode.setBarcode1Quan(quanHuyen1.getTen());
				}
				if (i++ < diemBanHangList.size()) {
					barcode.setCode2(diemBanHangList.get(i - 1).getMaCuaHang());
					barcode.setBarcode2(diemBanHangList.get(i - 1)
							.getMaCuaHang());
					barcode.setBarcode2Name(diemBanHangList.get(i - 1)
							.getTenCuaHang());
					if (diemBanHangList.get(i - 1).getQuanHuyen() != null) {
						QuanHuyen quanHuyen2 = quanHuyenService
								.find(diemBanHangList.get(i - 1).getQuanHuyen());
						if (quanHuyen2!=null)
						barcode.setBarcode2Quan(quanHuyen2.getTen());
					}
					
				} else {
					list.add(barcode);
					break;
				}
				if (i++ < diemBanHangList.size()) {
					barcode.setCode3(diemBanHangList.get(i - 1).getMaCuaHang());
					barcode.setBarcode3(diemBanHangList.get(i - 1)
							.getMaCuaHang());
					barcode.setBarcode3Name(diemBanHangList.get(i - 1)
							.getTenCuaHang());
					if (diemBanHangList.get(i - 1).getQuanHuyen() != null) {
						QuanHuyen quanHuyen3 = quanHuyenService
								.find(diemBanHangList.get(i - 1).getQuanHuyen());
						if (quanHuyen3!=null)
						barcode.setBarcode3Quan(quanHuyen3.getTen());
					}
				} else {
					list.add(barcode);
					break;
				}				
				list.add(barcode);
			}

			jrDataSource = new JRBeanCollectionDataSource(list);
		} else
			jrDataSource = new JREmptyDataSource();
		ReportFilter filter = new ReportFilter();
		filter.setTemplate(FILE_PATH_LARGE);
		filter.setFileName(FILE_NAME);
		filter.setExportType("xls");
		downloadService.download(filter, token, jrDataSource, response);
	}
        */
	/**
	 * Export large barcode
	 * 
	 * @param type
	 *            (export type)
	 * @param RPTNhatKyBanHangFilter
	 *            class
	 * @param ModelMap
	 *            class
	 * @param HttpServletResponse
	 *            class
	 * 
	 * @return HttpServletResponse(export file)
	 */
    @RequestMapping(value = "/export/plan/barcodeLarge", method = RequestMethod.POST)
    public void exportBarcodeLargeInPlan(
            @RequestParam(value = "id", required = true) int planId, HttpServletRequest request, HttpServletResponse response) throws IOException {

        int login = LoginContext.isLogin(request, dataService);
        if (login != 1) {
            return;
        } 

        List<MCPDetails> mcpDetailList = mcpService.getListMCPDetailByMcpId(planId);
        if (mcpDetailList == null || mcpDetailList.isEmpty()) {
            return;
        }

        String token = downloadService.generate();

        JRDataSource jrDataSource = null;
        if (mcpDetailList != null && mcpDetailList.size() > 0) {
            List<RPTBarcode> list = new ArrayList<RPTBarcode>();
            int i = 0;
            while (i < mcpDetailList.size()) {
                i++;
                RPTBarcode barcode = new RPTBarcode();
                barcode.setCode1(mcpDetailList.get(i - 1).getPoss().getPosCode());
                barcode.setBarcode1(mcpDetailList.get(i - 1).getPoss().getPosCode());
                barcode.setBarcode1Name(mcpDetailList.get(i - 1).getPoss().getName());
                barcode.setBarcode1Quan(mcpDetailList.get(i - 1).getPoss().getAddress());
                if (i++ < mcpDetailList.size()) {
                    barcode.setCode2(mcpDetailList.get(i - 1).getPoss().getPosCode());
                    barcode.setBarcode2(mcpDetailList.get(i - 1).getPoss().getPosCode());
                    barcode.setBarcode2Name(mcpDetailList.get(i - 1).getPoss().getName());
                    barcode.setBarcode2Quan(mcpDetailList.get(i - 1).getPoss().getAddress());
                } else {
                    list.add(barcode);
                    break;
                }
                if (i++ < mcpDetailList.size()) {
                    barcode.setCode3(mcpDetailList.get(i - 1).getPoss().getPosCode());
                    barcode.setBarcode3(mcpDetailList.get(i - 1).getPoss().getPosCode());
                    barcode.setBarcode3Name(mcpDetailList.get(i - 1).getPoss().getName());
                    barcode.setBarcode3Quan(mcpDetailList.get(i - 1).getPoss().getAddress());
                } else {
                    list.add(barcode);
                    break;
                }
                list.add(barcode);
            }

            jrDataSource = new JRBeanCollectionDataSource(list);
        } else {
            jrDataSource = new JREmptyDataSource();
        }
        ReportFilter filter = new ReportFilter();
        filter.setTemplate(FILE_PATH_LARGE);
        filter.setFileName(FILE_NAME);
        filter.setExportType("xls");
        downloadService.download(filter, token, jrDataSource, response);
    }
	
        /*
	@RequestMapping(value = "/export/barcodeLargeNPP", method = RequestMethod.POST)
	public void exportBarcodeLargeNPP(Model model,
			HttpServletResponse response, 
			@ModelAttribute("frmNPPList") @Valid SupplierFilter supFilter) {
		String token = downloadService.generate();
		
//		NguoiSuDung user = userService.getLogin();
//		filterService.checkCompanyPermission(user, supFilter, model);
//		filterService.addAreaSelectionForUserToModel(user, supFilter, model);
		
		List<NhaPhanPhoi> listNPP = nhaPhanPhoiService.list(supFilter);
		JRDataSource jrDataSource = null;
		if (listNPP.size() > 0) {
			List<RPTBarcode> list = new ArrayList<RPTBarcode>();
			int i = 0;
			while (i < listNPP.size()) {
				i++;
				RPTBarcode barcode = new RPTBarcode();
				barcode.setPrefix("npp_id:");
				barcode.setCode1(listNPP.get(i - 1).getMaNPP());
				barcode.setBarcode1(listNPP.get(i - 1).getMaNPP());
				barcode.setBarcode1Name(listNPP.get(i - 1).getTenNPP());
				if (listNPP.get(i - 1).getQuanHuyen() != null) {
					QuanHuyen quanHuyen1 = quanHuyenService
							.find(listNPP.get(i - 1).getQuanHuyen());
					if (quanHuyen1!=null)
					barcode.setBarcode1Quan(quanHuyen1.getTen());
				}
				if (i++ < listNPP.size()) {
					barcode.setCode2(listNPP.get(i - 1).getMaNPP());
					barcode.setBarcode2(listNPP.get(i - 1).getMaNPP());
					barcode.setBarcode2Name(listNPP.get(i - 1).getTenNPP());
					if (listNPP.get(i - 1).getQuanHuyen() != null) {
						QuanHuyen quanHuyen2 = quanHuyenService
								.find(listNPP.get(i - 1).getQuanHuyen());
						if (quanHuyen2!=null)
						barcode.setBarcode2Quan(quanHuyen2.getTen());
					}
				} else {
					list.add(barcode);
					break;
				}
				if (i++ < listNPP.size()) {
					barcode.setCode3(listNPP.get(i - 1).getMaNPP());
					barcode.setBarcode3(listNPP.get(i - 1).getMaNPP());
					barcode.setBarcode3Name(listNPP.get(i - 1).getTenNPP());
					if (listNPP.get(i - 1).getQuanHuyen() != null) {
						QuanHuyen quanHuyen3 = quanHuyenService
								.find(listNPP.get(i - 1).getQuanHuyen());
						if (quanHuyen3!=null)
						barcode.setBarcode3Quan(quanHuyen3.getTen());
					}
				} else {
					list.add(barcode);
					break;
				}
				list.add(barcode);
			}

			jrDataSource = new JRBeanCollectionDataSource(list);
		} else
			jrDataSource = new JREmptyDataSource();
		ReportFilter filter = new ReportFilter();
		filter.setTemplate(FILE_PATH_LARGE);
		filter.setFileName(FILE_NAME);
		filter.setExportType("xls");
		downloadService.download(filter, token, jrDataSource, response);
	}

	@RequestMapping(value = "/export/testReport", method = RequestMethod.POST)
	public void exportChart(@ModelAttribute(FORM) @Valid RPTChartFilter filter,
			ModelMap model, HttpServletResponse response) {
		String token = downloadService.generate();
		List<RPTChart> rptChartList = rptChartService.list();
		JRDataSource jrDataSource = null;
		if (rptChartList.size() > 0) {

			jrDataSource = new JRBeanCollectionDataSource(rptChartList);
		} else
			jrDataSource = new JREmptyDataSource();
		filter.setTemplate("/report3.jrxml");
		filter.setFileName("Test_chart");
		downloadService.download(filter, token, jrDataSource, response);
	}
        */
}