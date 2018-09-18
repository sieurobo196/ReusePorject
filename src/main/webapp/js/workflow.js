function checkEmpty(textbox) {
    return (textbox.val().length > 0);
}

function postForm(text, form) {
    var textGui = $(text);
    $('#file_error').html('');
    if (checkEmpty(textGui)) {
        showLoading();
        $(form).submit();
    } else {
        $('#file_error').html('Vui lòng chọn tập tin dữ liệu.');
    }
}

function postGui() {
    postForm('#file_gui', '#form_gui');
}

function postCare() {
    postForm('#file_care', '#form_care');
}

function postSales() {
    postForm('#file_sales', '#form_sales');
}

function postWorkflowByExcel() {
    postForm('#file_workflow', '#form_workflow_excel');
}

function removeError(textBox) {
    var text = $(textBox);
    if (checkEmpty(text)) {
        $('#file_error').html('');
        $('#gui_massage').html('');
    }
}
