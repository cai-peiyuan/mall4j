//可改动页面
//统一接口域名，测试环境
var domain = "http://192.168.50.163:8086";
domain = "http://192.168.50.163:8086/mall-wx-api";
let openSource = true;

function compareVersion(v1, v2) {
    v1 = v1.split('.')
    v2 = v2.split('.')
    const len = Math.max(v1.length, v2.length)

    while (v1.length < len) {
        v1.push('0')
    }
    while (v2.length < len) {
        v2.push('0')
    }

    for (let i = 0; i < len; i++) {
        const num1 = parseInt(v1[i])
        const num2 = parseInt(v2[i])

        if (num1 > num2) {
            return 1
        } else if (num1 < num2) {
            return -1
        }
    }

    return 0
}

exports.domain = domain;
exports.openSource = openSource;
exports.compareVersion = compareVersion;
exports.debug = true;
exports.log = function (data) {
    if (exports.debug) {
        console.log(data);
    }
}

