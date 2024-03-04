//可改动页面
//统一接口域名，测试环境
var domain = "http://192.168.50.163:8086";
domain = "https://xdyx.bhgk.cc/wx-apis";
let imageDomain = "http://cdn.bhgk.cc/xdyx/images"
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
module.exports = {
  version: '14.14.2',
  note: '保存小程序码到手机相册，用户拒绝后给你提示并引导用户打开设置去开启权限', // 这个为版本描述，无需修改
  subDomain: 'tz', // 此处改成你自己的专属域名。什么是专属域名？请看教程 https://www.it120.cc/help/qr6l4m.html
  merchantId: 951, // 商户ID，可在后台工厂设置-->商户信息查看
  sdkAppID: 1400450467, // 腾讯实时音视频应用编号，请看教程 https://www.it120.cc/help/nxoqsl.html
  bindSeller: false, // true 开启三级分销抢客； false 为不开启
  customerServiceType: 'QW', // 客服类型，QW为企业微信，需要在后台系统参数配置企业ID和客服URL，XCX 为小程序的默认客服
  log: function (data) {
    if (exports.debug) {
      console.log(data);
    }
  },
  domain: domain,
  openSource: openSource,
  compareVersion: compareVersion,
  debug: true,
  imageDomain: imageDomain,
}