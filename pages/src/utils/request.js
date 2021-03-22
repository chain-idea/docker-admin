/**
 * request 网络请求工具
 * 更详细的 api 文档: https://github.com/umijs/umi-request
 */
import {extend} from 'umi-request';
import {message, notification} from 'antd';
import {serverUrl} from "../config";
import moment from 'moment';
import {history} from 'umi';

const codeMessage = {
  200: '服务器成功返回请求的数据。',
  201: '新建或修改数据成功。',
  202: '一个请求已经进入后台排队（异步任务）。',
  204: '删除数据成功。',
  400: '发出的请求有错误，服务器没有进行新建或修改数据的操作。',
  401: '请登录',
  403: '权限不足',
  404: '接口未定义',
  406: '请求的格式不可得。',
  410: '请求的资源被永久删除，且不会再得到的。',
  422: '当创建一个对象时，发生一个验证错误。',
  500: '服务器发生错误，请检查服务器。',
  502: '网关错误。',
  503: '服务不可用，服务器暂时过载或维护。',
  504: '网关超时。',
};

const errorHandler = error => {
  const {response} = error;
  if (response && response.status) {
    if (response.status == 401) {
      // 直接跳到登录界面，不提示信息
      history.push('/login');
      return response
    }
    const errorText = codeMessage[response.status] || response.statusText;
    notification.error({
      message: errorText,
    });
    response.error = true;
    response.msg = errorText;
  } else if (!response) {
    notification.error({message: '网络异常'});
    return {error: true, msg: '网络异常'}
  }

  return response
};
const request = extend({
  errorHandler,
  credentials: 'omit', // 默认请求不带上cookie
});


// 设置登陆header
request.interceptors.request.use((url, options) => {
  let jwt = localStorage.getItem("jwt");
  options.headers['Authorization'] = jwt;
  console.log(url);

  let isFullUrl = url.startsWith("http") || url.startsWith("https");
  if (!isFullUrl) {
    // 防止双斜杠出现
    if (serverUrl.endsWith("/") && url.startsWith("/")) {
      url = url.substr(1)
    }
    // 添加请求前缀
    url = serverUrl + url;
  }
  console.log(url);

  return {url: url, options: options}
});


// 拦截响应，全局提示错误信息, 正式环境全部提示服务器忙
request.interceptors.response.use(async (response) => {
  if (response == null) {
    return
  }
  const {status, url} = response;

  if (status == null) {
    notification.error({message: '服务器忙'});
    return {error: true, msg: '服务器忙'}
  }

  if (status == 200) {
    const data = await response.clone().json();
    // 后台的提示信息
    if (data && data.error) {
      let msg = data.msg;
      console.log(msg);
      if (msg == null || msg.indexOf("Exception") >= 0) {
        msg = '服务器忙'
      }
      notification.error({message: msg});
    }

    return response;
  }

  if (status == 401) {
    return response;
  }


  const errorText = codeMessage[status] || response.statusText || '服务器忙';


  notification.error({message: errorText});

  response.error = true;
  response.msg = errorText;
  return response;
});


const handleParams = (values) => {
  if (!values) {
    return null
  }
  for (let item in values) {
    let obj = values[item];
    if (obj && obj.constructor === Object) {
      for (let key in obj) {
        if (!obj[key]) {
          delete obj[key];
        }
      }
      if (Object.keys(obj).length == 0) {
        values[item] = null;
      }
    } else if (obj && obj instanceof moment) {
      values[item] = obj._i;
    }
  }
  return values;
};

function get(url, params) {
  return request(url, {
    params,
  });
}

function post(url, params) {
  return request(url, {
    method: 'POST',
    data: params,
  });
}

/**
 *
 * @param isGet
 * @param url
 * @param params
 * @param autoAlertMsg 表示是否安静的请求， 当true时，不显示加载中，和成功消息
 * @returns {Promise<unknown>}
 */
function send(isGet, url, params, autoAlertMsg = true, alertTitle) {
  return new Promise((resolve, reject) => {
    let hide = () => { // 空函数
    };
    if (autoAlertMsg) {
      hide = message.loading('加载中...', 0);
    }

    params = handleParams(params);
    const promise = isGet ? get(url, params) : post(url, params);
    promise.then(rs => {
      if (autoAlertMsg) {
        hide();

        if (rs) {
          let msg = alertTitle || rs.msg;
          if (!rs.error && msg) {
            message.success(msg);
          }
        }

      }

      if (rs.error) {
        reject(rs)
      } else {
        resolve(rs)
      }
    }).catch(e => {
      hide();
      reject(e);
      message.error(e);
    })

  })
}


const http = {
  request: request,
  get: (url, params,autoAlertMsg=true) => {
    return send(true, url, params, autoAlertMsg)
  },
  post: (url, params, alertTitle) => {
    return send(false, url, params, true, alertTitle)
  },
  quietGet: (url, params) => {
    return send(true, url, params, false)
  },
  quietPost: (url, params) => {
    return send(false, url, params, false)
  },
  // 获得分页数据
  getPageableData: (url, params, sort) => {
    // 分页参数
    params.pageNumber = params.current;
    delete  params.current
    if (sort) {
      let keys = Object.keys(sort);
      if (keys.length > 0) {
        let key = keys[0];
        let dir = sort[key] == 'ascend' ? 'asc' : 'desc';
        params.orderBy = key + "," + dir
      }
    }



    return new Promise((resolve, reject) => {
      request.get(url, {params: params}).then(pageable => {
        // 按pro table 的格式修改数据结构
        pageable.data = pageable.content;
        pageable.success = true;
        pageable.total = pageable.totalElements;
        resolve(pageable)
      }).catch(e => {
        message.error(e);
        reject(e)
      })
    })
  }
};

export default http;
