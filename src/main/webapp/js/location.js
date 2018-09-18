/**
 * Get province
 * 
 * @param {type} locationType location type
 * @param {type} successCallback when server response is success then this function will be called.
 * @param {type} errorCallback when server response is errot then this function will be called.
 */
function getListProvince(locationType, successCallback, errorCallback) {
    var contents = new Object();
    contents.locationType = locationType;

    requestToServer('POST', contextPath + '/ajax/location/getListProvince', contents,
        function (data) {
            success(data, successCallback);
        },
        function (data) {
        error(data, errorCallback);
    }
    );
}

/**
 * Get list district of the province
 *
 * @param {type} locationType location type
 * @param {type} successCallback when server response is success then this function will be called.
 * @param {type} errorCallback when server response is errot then this function will be called.
 */
function getListDistrict(provinceID, successCallback, errorCallback) {
    var contents = new Object();
    contents.parentId = provinceID;

    requestToServer('POST', contextPath + '/ajax/location/getListDistrict', contents,
        function (data) {
            success(data, successCallback);
        },
        function (data) {
            error(data, errorCallback);
        }
    );
}

/**
 * Get list ward of the district
 *
 * @param {type} locationType location type
 * @param {type} successCallback when server response is success then this function will be called.
 * @param {type} errorCallback when server response is errot then this function will be called.
 */
function getListWard(districtId, successCallback, errorCallback) {
    var contents = new Object();
    contents.parentId = districtId;

    requestToServer('POST', contextPath + '/ajax/location/getListWard', contents,
        function (data) {
            success(data, successCallback);
        },
        function (data) {
            error(data, errorCallback);
        }
    );
}

/**
 * Get get list status by type
 * @param {type} statusType status type
 * @param {type} locationType location type
 * @param {type} successCallback when server response is success then this function will be called.
 * @param {type} errorCallback when server response is errot then this function will be called.
 */
function getListStatus(statusType, successCallback, errorCallback) {
    var contents = new Object();
    contents.statusTypeId = statusType;
    var page = new Object();
    page.pageNo = 1;
    page.recordsInPage = 10;

    var req = new Object();
    req.page = page;
    req.contents = contents;

    requestToServer('POST', contextPath + '/ajax/status/getListStatus', req,
        function (data) {
            success(data, successCallback);
        },
        function (data) {
            error(data, errorCallback);
        }
    );
}

/**
 * get list channel type by parent.
 * Get all channel type < current channel type
 */
function getListChannelTypeByParent(channelTypeParent, successCallback, errorCallback) {
    var json = new Object();
    json.id = channelTypeParent;
    requestToServer('POST', contextPath + '/ajax/channel/getListChannelTypeByParent', json,
        function (data) {
            success(data, successCallback);
        },
        function (data) {
            error(data, errorCallback);
        }
    );
}

function getListChannelTypeByParentId(channelParent, successCallback, errorCallback) {
    var json = new Object();
    json.parentId = channelParent;
    if(page !== undefined)
    {
        json.page = page;
    }
    if(size != undefined)
    {
        json.size = size;
    }
    
    requestToServer('POST', contextPath + '/ajax/channel/getListChannelByParentId', json,
        function (data) {
            success(data, successCallback);
        },
        function (data) {
            error(data, errorCallback);
        }
    );
}

function getListChannelByTypeId(channelId, channelType, successCallback, errorCallback) {
    var json = new Object();
    json.channelType = channelType;
    json.channelId = channelId;
    if(page !== undefined)
    {
        json.page = page;
    }
    if(size !== undefined)
    {
        json.size = size;
    }
    requestToServer('POST', contextPath + '/ajax/channel/getListChannelByChannelType', json,
        function (data) {
            success(data, successCallback);
        },
        function (data) {
            error(data, errorCallback);
        }
    );
}

/**
 * Get list parent channel;
 */
function getListChannelParent(channelId, successCallback, errorCallback) {
    var json = new Object();
    json.id = channelId;
    requestToServer('POST', contextPath + '/ajax/channel/getListChannelParent', json,
            function (data) {
                success(data, successCallback);
            },
            function (data) {
                error(data, errorCallback);
            }
    );
}

/**
 * call back to function success
 * @param {type} data json data
 * @param {type} callback is function you want callback
 */
function success(data, callback) {
    callback(data, callback);
}

/**
 * call back to function success
 * @param {type} data json data
 * @param {type} callback is function you want callback
 */
function error(data, callback) {
    callback(data, callback);
}


