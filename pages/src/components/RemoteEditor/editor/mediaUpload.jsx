import {message} from 'antd';
import {serverUrl} from "../../../config";


import React from "react";

/**
 *
 * @param {file} file 源文件
 * @desc 限制为图片文件
 * @retutn 是图片文件返回true否则返回false
 */

export const isImageFile = (file, fileTypes) => {
  const types = fileTypes || [
    'image/png',
    'image/gif',
    'image/jpeg',
    'image/jpg',
    'image/bmp',
    'image/x-icon',
    'image/webp',
    'image/apng',
    'image/svg',
  ];

  const isImage = types.includes(file.type);
  if (!isImage) {
    message.error('上传文件非图片格式!');
    return false;
  }

  return true;
};

/**
 *
 * @param {file} file 源文件
 * @param {number} fileMaxSize  图片限制大小单位（MB）
 * @desc 限制为文件上传大小
 * @retutn 在限制内返回true否则返回false
 */

export const maxFileSize = (file, fileMaxSize = 2) => {
  const isMaxSize = file.size / 1024 / 1024 < fileMaxSize;
  if (!isMaxSize) {
    message.error('上传图片大小不能超过 ' + fileMaxSize + 'MB!');
    return false;
  }
  return true;
};

/**
 *
 * @param {file} file 源文件
 * @desc 读取图片文件为base64文件格式
 * @retutn 返回base64文件
 */
// 读取文件
export const readFile = file => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = e => {
      const data = e.target.result;
      resolve(data);
    };
    reader.onerror = () => {
      const err = new Error('读取图片失败');
      reject(err.message);
    };

    reader.readAsDataURL(file);
  });
};

/**
 *
 * @param {string} src  图片地址
 * @desc 加载真实图片
 * @return 读取成功返回图片真实宽高对象 ag: {width:100,height:100}
 */

export const loadImage = src => {
  return new Promise((resolve, reject) => {
    const image = new Image();
    image.src = src;
    image.onload = () => {
      const data = {
        width: image.width,
        height: image.height
      };
      resolve(data);
    };
    image.onerror = () => {
      const err = new Error('加载图片失败');
      reject(err);
    };
  });
};

/**
 *
 * @param {file} file 源文件
 * @param {object} props   文件分辨率的宽和高   ag: props={width:100, height :100}
 * @desc  判断图片文件的分辨率是否在限定范围之内
 * @throw  分辨率不在限定范围之内则抛出异常
 *
 */
export const isAppropriateResolution = async (file, props) => {
  try {
    const { width, height } = props;
    const base64 = await readFile(file);
    const image = await loadImage(base64);
    if (image.width !== width || image.height !== height) {
      throw new Error('上传图片的分辨率必须为' + width + '*' + height);
    }
  } catch (error) {
    throw error;
  }
};

// 上传图片 根据自己项目更换
export const uploadImage = (param) => {
  return new Promise((resolve, reject) => {
    const serverURL = serverUrl + 'common/upload';
    const xhr = new XMLHttpRequest;
    const fd = new FormData();

    // libraryId可用于通过mediaLibrary示例来操作对应的媒体内容
    console.log(param.libraryId);

    const successFn = (response) => {
      // 假设服务端直接返回文件上传后的地址
      // 上传成功后调用param.success并传入上传后的文件地址
      console.log(xhr);
      console.log(xhr.responseText);
      var responseText =  xhr.responseText;//返回结果
      var object = JSON.parse(responseText);
      console.log(object);
      if(object.error){
        param.error('图片上传失败！')
      }else{
        param.success({
          url: object.url,
          //meta: {
          //id: 'xxx',
          // title: 'xxx',
          //alt: 'xxx',
          //loop: false, // 指定音视频是否循环播放
          //autoPlay: false, // 指定音视频是否自动播放
          //controls: false, // 指定音视频是否显示控制栏
          // }
        })
      }
    };

    const progressFn = (event) => {
      // 上传进度发生变化时调用param.progress
      param.progress(event.loaded / event.total * 100)
    };

    const errorFn = (response) => {
      // 上传发生错误时调用param.error
      param.error({
        msg: 'unable to upload.'
      })
    };

    xhr.upload.addEventListener('progress', progressFn, false);
    xhr.addEventListener('load', successFn, false);
    xhr.addEventListener('error', errorFn, false);
    xhr.addEventListener('abort', errorFn, false);

    fd.append('file', param.file);
    xhr.open('POST', serverURL, true);
    xhr.setRequestHeader('Authorization',localStorage.getItem("jwt"));
    xhr.send(fd);
  });
};

// 上传html  根据自己项目更换
export const uploadHtml = (editorContent) => {
  return new Promise((resolve, reject) => {
  });
};
