/**
 * get stattus from json
 * @param {type} data json response from server
 * @returns {unresolved}
 */
function getStatus(data) {
    var responseStatus = JSON.stringify(data);
    var json = JSON.parse(responseStatus);
    var sta = json.status;
    return sta;
}

/**
 * get code response from server
 * @param {type} data is json data you request to server
 * @returns {String.code|unresolved.code|getStatus.sta.code}
 */
function getCode(data) {
    var status = getStatus(data);
    var code = -1;
    if (status != null) {
        code = status.code;
    }
    return code;
}

/**
 * get messgae from server
 * @param {type} data is json data request from server
 * @returns {String.message|getStatus.sta.message|unresolved.message}
 */
function getMessage(data) {
    var status = getStatus(data);
    var message = "";
    if (status != null) {
        message = status.message;
    }
    return message;
}

/**
 * get contents from json
 * @param {type} data is json response from server
 */
function getContent(data) {
    var code = getCode(data);
    var con;
    if (code === 200) {
        var json = JSON.stringify(data);
        var obj = JSON.parse(json);
        con = obj.contents;
    }
    return con;
}

function getCount(data)
{
    var count = 0;
    if(getCode(data)==200)
    {
        var json = JSON.stringify(data);
        var obj = JSON.parse(json);
        count = obj.contents.count;
    }
    return count;
}

/**
 * Get array ojbect from server
 * @param {type} data is json data response from server
 * @returns {unresolved.contentsList|getContent.con.contentsList|Window.contentsList}
 */
function getContentList(data) {
    var content = getContent(data);
    var cntList;
    if (content != null) {
        cntList = content.contentList;
    }
    return cntList;
}

/**
 * get max page rom json
 * @param {type} data is json data response from server
 * @returns {getContentList.cntList.count|getContent.con.contentsList.count|unresolved.contentsList.count|Window.contentsList.count}
 */
function getMaxpage(data) {
    var maxpage = getContentList(data);
    var count = 0;
    if (maxpage != null) {
        count = maxpage.count;
    }
    return count;
}

