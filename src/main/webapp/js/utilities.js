/**
 * request to server get data.
 * @param {type} method is mothed request to server
 * @param {type} url is url you want request to server
 * @param {type} data is json data you want request
 * @param {type} successCall when response from serrver is success, this is function
 * @param {type} errorCall when response from serrver is error, this is function
 */
function requestToServer(method, url, data, successCall, errorCall) {
    $.ajax({
        type: method,
        url: url,
        dataType: 'json',
        data: JSON.stringify(data),
        contentType: 'application/json',
        mimeType: 'application/json',
        success: function (data) {
            successCallback(data, successCall);
        },
        error: function (error) {
            errorCallback(error, errorCall);
        }
    });
}
/**
 * call back to function success
 * @param {type} data json data
 * @param {type} callback is function you want callback
 */
function successCallback(data, callback) {
    callback(data, callback);
}

/**
 * call back to function success
 * @param {type} data json data
 * @param {type} callback is function you want callback
 */
function errorCallback(data, callback) {
    callback(data, callback);
}

/**
 * render combobox
 *
 * @param {type} comboBoxSelector is combobox is
 * @param {type} items is array data
 */
function renderComboBox(comboBoxSelector, items, label) {
    var $domCbo = $(comboBoxSelector);
    renderComboBoxEmpty(comboBoxSelector, label);
    for (var i = 0; i < items.length; i++) {
        var item = items[i];
        var $domOption = $("<option value = '" + item.id + "'>" + item.name + "</option>");
        $domCbo.append($domOption);
    }
}

function renderComboBoxEmpty(comboBoxSelector, label) {
    var $domCbo = $(comboBoxSelector);
    $domCbo.empty();// remove old options
    var $domOption = $("<option value = '0'>" + label + "</option>");
    $domCbo.append($domOption);
    $domCbo.select2();
}