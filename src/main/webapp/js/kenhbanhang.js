$(document).ready(function() {
	handleGlobalAjaxError();
});

function selectCity(domCity) {
	var $domCity = $(domCity);
	var cityUid = $domCity.val();
	var url = contextPath + "/kenhbh/findByCityUid/" + cityUid;
	if (cityUid != 0) {
		$.ajax({
			type : 'GET',
			url : url,
			success : function(data) {
				renderComboBox("#quanHuyen", data);
			}
		});
	} else {
		renderComboBoxEmpty("#quanHuyen");
	}
	renderComboBoxEmpty("#phuongXa");

}
function selectTown(domTown) {
	var $domTown = $(domTown);
	var townUid = $domTown.val();
	if (townUid != 0) {
		var url = contextPath + "/kenhbh/findByTownUid/" + townUid;
		$.ajax({
			type : 'GET',
			url : url,
			success : function(data) {
				renderComboBox("#phuongXa", data);
			}
		});
	} else {
		
		renderComboBoxEmpty("#phuongXa");
	}

}

function renderComboBox(comboBoxSelector, items) {
	// re-render provinces
	var $domCbo = $(comboBoxSelector);
	$domCbo.empty();// remove old options
	for ( var i = 0; i < items.length; i++) {
		var item = items[i];
		var $domOption = $("<option value = '" + item.uid + "'>" + item.name + "</option>");
		$domCbo.append($domOption);
	}
}
function renderComboBoxEmpty(comboBoxSelector) {
	// re-render provinces
	var $domCbo = $(comboBoxSelector);
	$domCbo.empty();// remove old options
}

function selectChiNhanh(domChiNhanh) {
	var $domChiNhanh = $(domChiNhanh);
	var chiNhanhId = $domChiNhanh.val();
	var url = contextPath + "/kenhbh/findTongDaiLy/" + chiNhanhId;	
	window.location.href = url;
	
	
	}

/*
 * render list
 */

function renderList(ulList, items, action) {
	var $domUl = $(ulList);
	$domUl.empty();
	var size = items.length;
	if (size == 0) {
		$domUl.append("Không tồn tại đại lý/tổng đại lý.! ");
		return;
	}
	var i = 0;
	if(action == "returnSave"){
		i = 1;
		
	}
	for( i ; i < size; i++) {		
		var item = items[i];
		var $domLI = $("<li><a href=capcon title=Xem Tong Dai Ly>" + item.ten + "</a></li>");
		$domUl.append($domLI);
	}
}

function submitSaveForm() {
	
	var $maVietTat = $("#maVietTat");
	var $dienThoai = $("#dienThoai");
	var $tinhThanhPho = $("#tinhThanhPho");
	var $quanHuyen = $("#quanHuyen");
	var $phuongXa = $("#phuongXa");
	var $ten = $("#ten");
	var $fax = $("#fax");
	var $diaChi = $("#diaChi");
	var $nguoiLienHe = $("#nguoiLienHe");
	
	var error = "";
	var maVietTat = $("#maVietTat").val();
	var ten = $("#ten").val();
	var diaChi = $("#diaChi").val();
	var dienThoai = $("#dienThoai").val();
	var fax = $("#fax").val();
	var nguoiLienHe = $("#nguoiLienHe").val();
	var tinhThanhPho = $("#tinhThanhPho").val();
	var quanHuyen = $("#quanHuyen").val();
	var phuongXa = $("#phuongXa").val();

	if (maVietTat == "") {
		error += "(*) Mã Viết Tắt không rỗng.";
	}
	if (dienThoai == "") {
		error += "(*) Số điện thoại không rỗng.";
	}
	if (ten == "") {
		error += "(*) Tên đại lý không được rỗng.";
	}
	if (tinhThanhPho == "0") {
		error += "(*) Chọn Tỉnh/Tp.";
	}
	if (quanHuyen == "0" || quanHuyen == null) {
		error += "(*) Chọn Quận/Huyện."
	}
	if (phuongXa == "0" || phuongXa == null) {
		error += "(*) Chọn phường/xã.";
	}
	var $errorID = $("#errorID");
	if (error != "") {
		$errorID.empty();
		$errorID.append(error);
		return;
	}

	if (fax == "") {
		fax = "-";
	}
	if (diaChi == "") {
		diaChi = "-";
	}
	if (nguoiLienHe == "") {
		nguoiLienHe = "-";
	}
	var url = contextPath + "/kbh/saveKenhBH/" + maVietTat + "/" + ten + "/"
			+ diaChi + "/" + dienThoai + "/" + fax + "/" + nguoiLienHe + "/"
			+ tinhThanhPho + "/" + quanHuyen + "/" + phuongXa;
	
		window.location.href = url;
	 
}

/*
 * render du lieu tam.
 */
function renderDefaultValueComponents(maVietTat, ten, dienThoai, fax, diaChi, nguoiLienHe, tinhThanhPho, quanHuyen, phuongXa){	
	
	var $maVietTat    = $(maVietTat);
	var $ten          =  $(ten);
	var $dienThoai    =  $(dienThoai);
	var $fax          =  $(fax);
	var $diaChi       =  $(diaChi);
	var $nguoiLienHe  =  $(nguoiLienHe);
	var $tinhThanhPho =  $(quanHuyen);
	var $phuongXa     =  $(phuongXa);
	$maVietTat.val('');
	$ten.val('');
	$dienThoai.val('');
	$fax.val('');
	$diaChi.val('');
	$nguoiLienHe.val('');	
	renderComboBoxEmpty(quanHuyen);
	renderComboBoxEmpty(phuongXa);
	
}

/* thuc hien tim kiem*/
function timKiemDL(){
	
	var $maVietTat = $("#maVietTat");
	var $dienThoai = $("#dienThoai");
	var $tinhThanhPho = $("#tinhThanhPho");
	var $quanHuyen = $("#quanHuyen");
	var $phuongXa = $("#phuongXa");
	var $ten = $("#ten");
	var $fax = $("#fax");
	var $diaChi = $("#diaChi");
	var $nguoiLienHe = $("#nguoiLienHe");
	
	var maVietTat = $("#maVietTat").val();
	var ten = $("#ten").val();
	var diaChi = $("#diaChi").val();
	var dienThoai = $("#dienThoai").val();
	var fax = $("#fax").val();
	var nguoiLienHe = $("#nguoiLienHe").val();
	var tinhThanhPho = $("#tinhThanhPho").val();
	var quanHuyen = $("#quanHuyen").val();
	var phuongXa = $("#phuongXa").val();
	
	var $errorID = $("#errorID");
	$errorID.empty();
	$("#result_title").empty();
	$("#result").empty();
	
	if(maVietTat == ""){
		maVietTat = "Rong";
	}
	if(ten == ""){
		ten = "Rong";
	}
	if(diaChi == ""){
		diaChi = "Rong";
	}
	if(dienThoai== ""){
		dienThoai = "Rong";
	}
	if(fax == ""){
		fax = "Rong";
	}
	if(nguoiLienHe == ""){
		nguoiLienHe = "Rong";
	}
	if(tinhThanhPho == "" || tinhThanhPho == null){
		tinhThanhPho ="Rong";
	}
	if(quanHuyen == "" || quanHuyen == null){
		quanHuyen = "Rong";
	}
	if(phuongXa == "" || phuongXa == null){
		phuongXa = "Rong";
	}
	
	if(maVietTat =="Rong" && ten == "Rong" && diaChi == "Rong" && dienThoai == "Rong" && fax == "Rong" && nguoiLienHe == "Rong"){
		
		$errorID.empty();		
		$errorID.append("Nhập thông tin để tìm kiếm.");
		return;	
	}
	
	var url = contextPath + "/kbh/timkiemDaiLy/" + maVietTat + "/" + ten + "/"
	+ diaChi + "/" + dienThoai + "/" + fax + "/" + nguoiLienHe + "/"
	+ tinhThanhPho + "/" + quanHuyen + "/" + phuongXa;

	$.ajax({
		type : 'GET',
		url : url,
		success : function(data) {	
			
			var length = data.length;			
			if(length == 0){
				$("#result_title").empty();
				$("#result").empty();
				$("#result_title").append("Không tìm thấy dữ liệu!!!");
				return;			
			}			
				window.location.href = url;
		}
	});
}

/*
 * result list.
 */

function renderResultList(tb, titleID, items) {	
	var $domUl = $(tb);
	var $domTitle = $(titleID);
	$domUl.empty();
	$domTitle.empty();

	$domTitle.append("Có "+ items.length +"được tìm thấy");
	var $plainData ="";
	$plainData += "<form:form method='POST' id='userList' commandName= 'frmUserList'>";
	$plainData += "<input type='hidden' id= 'page' value='${page}' />";
	$plainData += "<input type='hidden' id='size' value='${size}'/>";
	$plainData += "<table width='100%' border= '0' cellspacing= '3px' cellpadding= '0'	class= 'table-2' style=' border: 1 px; border-color: black;>'";
	$plainData += "<tr> <td>Mã Viết Tắt</td> <td>Tên</td> <td>Địa Chỉ</td> <td>Điện thoại</td> <td>Fax</td> <td>Người Liên Hệ</td> <td>Hạnh Động</td> </tr>";

	for ( var i = 0; i < items.length; i++) {
		var item = items[i];
		$plainData += "<tr>";
		if(item.maVietTat != null){
			$plainData += "<td>" + item.maVietTat + "</td>";					
		}
		else {
			$plainData += "<td>" + " " + "</td>";					
		}
		
		if(item.ten != null){
			$plainData += "<td>" + item.ten + "</td>";					
		}
		else {
			$plainData += "<td>" + " " + "</td>";					
		}
		
		if(item.diaChi != null){
			$plainData += "<td>" + item.diaChi + "</td>";
		}
		else{
			$plainData += "<td>" + " " + "</td>";
		}
		if(item.dienThoai != null){
			$plainData += "<td>" + item.dienThoai + "</td>";
		}
		else {
			$plainData += "<td>" + " " + "</td>";					
		}
		
		if(item.fax != null){
			$plainData += "<td>" + item.fax + "</td>";
		}
		else {
			$plainData += "<td>" + " " + "</td>";
		}
		
		if(item.nguoiLienHe != null){
			$plainData += "<td>" + item.nguoiLienHe + "</td>";
		}
		else {
			$plainData += "<td>" + " " + "</td>";
		}
						
		$plainData += "<td> Edit | Delete </td>";
		$plainData += "</tr>";
	}

	$plainData += "<tr> <td colspan='4'><util:pagination maxPages='10' submitMethod='POST' formSubmit='userList' page='${page}' size='${size}' id='userBottomPagination'/></td> </tr>";
	$plainData += " </table> </form:form>";
	$domUl.html($plainData);

}
/*
 * view dai ly cap 2
 */

function viewDLCap2(idDL,cap){	
	var url = contextPath + "/kbh/kbhViewCap2/"+ idDL +"/"+ cap;	
	window.location.href = url;
}



//eidt user: gui ajax kem theo user id de nhan ve la mot user voi thong tin day du dua vao form.
function editDL(idDaiLy){
	var url = contextPath + "/kbh/editDL/"+idDaiLy;
	$.ajax({
		type : 'GET',
		url : url,
		success : function(data) {
			fillFormData(data);
		}
	});	
}

function fillFormData(daily){
	
	$("#btnUpdate").attr('class','btn-1 btnUpdateVisible');
	$("#idDL").val(daily.id);
	$("#maVietTat").val(daily.maVietTat);
	$("#ten").val(daily.ten);
	$("#diaChi").val(daily.diaChi);
	$("#dienThoai").val(daily.dienThoai);
	$("#fax").val(daily.fax);
	$("#nguoiLienHe").val(daily.nguoiLienHe);
	
	$("#tinhThanhPho").val(daily.tinhThanhPho);
	
	 if(daily.tinhThanhPho == "" && daily.quanHuyen == "" && daily.phuongXa == ""){
		 selectCity("#tinhThanhPho");
		 selectTown("#quanHuyen");
		 renderComboBoxEmpty("#phuongXa");		 
		 return;
	 	}	 
	 
	 if(daily.phuongXa == ""){
		 selectTown("#phuongXa");
		 return;
		}
	 
	$("#quanHuyen").val(daily.quanHuyen);
	$("#phuongXa").val(daily.phuongXa);
}

function updateDL() {
	document.getElementById('dailyForm').submit();	
}

function deleteDL(id){
	var url = contextPath + "/kenhbh/deleteDL/" + id;
	window.location.href = url;
	
}


