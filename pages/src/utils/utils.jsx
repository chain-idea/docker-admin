import { parse } from 'querystring';
import pathRegexp from 'path-to-regexp';
import {Tag} from "antd";
import React from "react";
import {fileUrl} from "@/utils/appConfig";

/* eslint no-useless-escape:0 import/prefer-default-export:0 */
const reg = /(((^https?:(?:\/\/)?)(?:[-;:&=\+\$,\w]+@)?[A-Za-z0-9.-]+(?::\d+)?|(?:www.|[-;:&=\+\$,\w]+@)[A-Za-z0-9.-]+)((?:\/[\+~%\/.\w-_]*)?\??(?:[-\+=&;%@.\w_]*)#?(?:[\w]*))?)$/;
export const isUrl = path => reg.test(path);
export const isAntDesignPro = () => {
  if (ANT_DESIGN_PRO_ONLY_DO_NOT_USE_IN_YOUR_PRODUCTION === 'site') {
    return true;
  }

  return window.location.hostname === 'preview.pro.ant.design';
}; // 给官方演示站点用，用于关闭真实开发环境不需要使用的特性

export const isAntDesignProOrDev = () => {
  const { NODE_ENV } = process.env;

  if (NODE_ENV === 'development') {
    return true;
  }

  return isAntDesignPro();
};
export const getPageQuery = () => parse(window.location.href.split('?')[1]);
/**
 * props.route.routes
 * @param router [{}]
 * @param pathname string
 */

export const getAuthorityFromRouter = (router = [], pathname) => {
  const authority = router.find(
    ({ routes, path = '/' }) =>
      (path && pathRegexp(path).exec(pathname)) ||
      (routes && getAuthorityFromRouter(routes, pathname)),
  );
  if (authority) return authority;
  return undefined;
};
export const getRouteAuthority = (path, routeData) => {
  let authorities;
  routeData.forEach(route => {
    // match prefix
    if (pathRegexp(`${route.path}/(.*)`).test(`${path}/`)) {
      if (route.authority) {
        authorities = route.authority;
      } // exact match

      if (route.path === path) {
        authorities = route.authority || authorities;
      } // get children authority recursively

      if (route.routes) {
        authorities = getRouteAuthority(path, route.routes) || authorities;
      }
    }
  });
  return authorities;
};

export const statusTag = (status, comment) => {
  let color = '#E6F7FF';
  switch(status) {
    case 'NEW':
      color = '#47C479';
      break;
    case 'ASSESSING':
      color = '#1890FF';
      break;
    case 'REJECT':
      color = '#D9D9D9';
      break;
    case 'APPROVE':
      color = '#52C41A';
      break;
    case 'WITHDRAWING':
      color = '#B0C4DE';
      break;
    case 'WITHDRAW':
      color = '#FF4D4F';
      break;
    case 'NORMAL':
      color = '#5BC726';
      break;
    case 'DISABLED':
      color = '#D9D9D9';
      break;

  }
  return (<Tag color={color}>{comment}</Tag>);
};

export const levelTag = (status, comment) => {
  let color = '#00ff66';
  switch(status) {
    case 'NORMAL':
      color = '#1890FF';
      break;
    case 'IMPORTANT':
      color = '#FFC125';
      break;
    case 'EMERGENCY':
      color = '#FF4040';
      break;
  }
  return (<Tag color={color}>{comment}</Tag>);
};

export const downloadFile = (url) => {
  return <a href={url.replace(fileUrl, fileUrl+"download/")} key={"download"} target="_blank">
    下载 &nbsp;
  </a>
};
