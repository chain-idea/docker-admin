const mediaBaseconfig = {
  // 文件限制
  accepts: {
    image: 'image/png,image/jpeg,image/gif,image/webp,image/apng,image/svg',
    video: false,
    audio: false,
  },

  //   允许插入的外部媒体的类型
  externals: {
    // 是否允许插入外部图片，
    image: false,
    //    是否允许插入外部视频，
    video: false,
    //    是否允许插入外部视频，
    audio: false,
    //    是否允许插入嵌入式媒体，例如embed和iframe标签等，
    embed: false,
  },
};

export default mediaBaseconfig;
