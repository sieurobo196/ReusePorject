/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
function deleteCat(id) {
    if (confirm('Bạn chắc chắn xóa loại hàng hóa này không?')) {
                var url = getAppRootPath() + "/category/delete?id=" + id;
                $.ajax({type: 'POST', 
                    dataType: 'json', 
                    url: url, 
                    success: function (data) {
                        if (data.status != 'OK') {
                            hideLoading();
                            alert('Xóa không thành công vì loại hàng hóa đã có hàng hóa!');
                        }
                        else {
                            window.location.reload();
                        }
                    } 
                });
            }
}


