/**
 * UMI Runtime Config https://umijs.org/docs/runtime-config
 */
import { history } from 'umi';

import auth from "./utils/auth";

/**
 *  权限判断
 * @param oldRender
 */

export function render(oldRender) {
  if(!auth.isLogin()) {
    let path = history.location.pathname;
    console.log('未登录，当前页面',     path)
    if(path && path.startsWith('/admin')) {

      history.push('/login')
      return
    }
  }

  oldRender()
}

